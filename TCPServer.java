import java.io.*;
import java.net.*;
import java.util.*;

import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;

import javax.crypto.Cipher;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SealedObject;


public class TCPServer {

    private static HashMap < String, userInfo > userProfiles;
    private static int NumPeople;

    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    private static String publicKeyAsString;
    private static String sortOfSharedKey;
    private static String sortOfIV;

    private static int newAlicePort;    
    private static int newBobPort;

    public static HashSet<Printw2> prinList = new HashSet<Printw2>();
    
    public TCPServer (){ 

        newAlicePort = 12005;
        newBobPort   = 12006;

        NumPeople =0;
        userProfiles = new HashMap<>();
    
        String fileName = "chatusers.txt";    
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
          String line;
          while ((line = br.readLine()) != null) {
            String[] line_arr = line.split("\\s+");
            
            userProfiles.put( line_arr[0], new userInfo(line_arr) );
          }
        } catch (IOException e) {
          e.printStackTrace();
        }

        // want to print userProfiles info for diagnostics
        /*
        for (String name: userProfiles.keySet())
        {
            String key = name;
            String value = userProfiles.get(name).toString();  
            //System.out.println(key + "'s buddies are: " + value);
            System.out.println(key + "'s password hash is: " + userProfiles.get(name).getPS());
            System.out.println( userProfiles.get(name).getPS().length() + " \n\n");
        }
        */
        try 
        {
            // Check if the pair of keys are present else generate those.     
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair myPair = kpg.generateKeyPair();
            
            publicKey = myPair.getPublic();
            privateKey=myPair.getPrivate();

            byte [] array = publicKey.getEncoded();
            //System.out.println("pubKeyasString: " + Base64.getEncoder().encodeToString(array));
            publicKeyAsString = Base64.getEncoder().encodeToString(array);


         } catch (Exception e) {
              e.printStackTrace();
            }
    } // end of server constructor

    public static void main(String argv[]) throws Exception 
    {
        //we have the server sock continuously listen for new incoming connections and then handle them accordingly.
        TCPServer t = new TCPServer();
        ServerSocket welcomeSocket = new ServerSocket(12004);
      ///ECB/PKCS1Padding
        try 
        {
            while (true)
            {
                //thread for each client
                new HandleClient(welcomeSocket.accept()).start();
            }
        }
        finally 
        {
            welcomeSocket.close();            
        }
    }

    /*
    * Printw2 is a Printwriter with an outputstream and a name
    *
    */
    private static class Printw2 extends PrintWriter {

        private String alias;
        public PrintWriter p;

        public String getName(){return alias;}

        public Printw2( String st, OutputStream out) 
        { 
            super(out, true);
            alias = st;
        }
    }

	  //makes a new thread per client that it is handling
    private static class HandleClient extends Thread 
    {
        private Socket connectionSocket;
        private PrintWriter serverToClient;
        String userNameOfClient = "";
        String message;

        private Printw2 prin;

        // sock comes from ... server's ... welcomeSocket.accept()
        public HandleClient(Socket sock) {
            this.connectionSocket = sock;               
        }

		    ObjectInputStream inStream = null; 

        @Override
            public void run() {
                try {
                    //create input stream attached to socket
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    inStream = new ObjectInputStream(connectionSocket.getInputStream());
                    
                    //takes info from serverside to clientside of the socket 
                    serverToClient =  new PrintWriter(connectionSocket.getOutputStream(), true);

                    serverToClient.println( publicKeyAsString );                   
                    

                    Boolean isAuthenticated = false;
                    String plainText = "";
                    while ( !isAuthenticated )
                    {
                     
                      SealedObject clientInfo = (SealedObject) inStream.readObject();
                      Cipher dec = Cipher.getInstance("RSA");
                      dec.init( Cipher.DECRYPT_MODE, privateKey );
                      String message = (String) clientInfo.getObject(dec);        
                      //System.out.println(message);
                      //login {userName, hash(PS), sessionNonce} K_shared

                      String theAuthCred = inFromClient.readLine();                   
                      String aSKey = message.substring(0, 32);
                      String anIV = message.substring(32, 48);

                      String isItThemString = SharedKey.decrypt( theAuthCred , aSKey, anIV.trim());                     
                      //System.out.println("Is it them line: " + isItThemString);                      
                      
                      //  in this protocol step, the client should send alias passwordHash nonce
                      //  so we sort of check for that with the first method that comes to mind
                      String[] userAndPSAuthTest = isItThemString.split("\\s+"); 
                      String sessionKey_Kas = "";
                      
                      String tck = " ";
                      String Ticket = "";
                      String ticket_encryption=" ";  
                      String s_to_a=" ";
                     
                      if ( userAndPSAuthTest.length == 3 ) 
                      {                            
                            userInfo value = userProfiles.get(userAndPSAuthTest[0]);
                            //if there is a matching username
                            if (value != null) {
                                if ( value.getPS().equals( userAndPSAuthTest[1] ) )
                                {
                                    serverToClient.println( "Access Granted");

                                    // if they are a valid user we'll hash the nonce to get a session key
                                    // CREATING SESSION NONCE
                                    sessionKey_Kas = SecureChatUtils.hashPS( userAndPSAuthTest[2] ).substring(0,32);                                      
                                    
                                    //{BUDDYLIST | HASH(BUDDYLIST) }Kas
                                    serverToClient.println( SharedKey.encrypt(SecureChatUtils.hashBuddyList( value.getBuddyList() ), sessionKey_Kas, anIV.trim() ) ); 
                            
                                    //this is the name of the current client
                                    userNameOfClient = userAndPSAuthTest[0];
                                    prin = new Printw2( userAndPSAuthTest[0], connectionSocket.getOutputStream() );
                                    prinList.add(prin);

                                    Boolean leaveHandleClient = false;                                    
                                    Boolean validName = false;
                                    
                                    while ( !validName )
                                    {
                                                                                 
                                         String namePickerThing = inFromClient.readLine();
                                         namePickerThing = SharedKey.decrypt( namePickerThing, sessionKey_Kas, anIV.trim() );                                          
                                         int pos = namePickerThing.toLowerCase().indexOf("ACK_X1".toLowerCase());                        

                                         //System.out.println("Are we here " + namePickerThing);

                                         System.out.println( namePickerThing.substring(0,pos) );
                                         String namePartOfMessage = namePickerThing.substring(0,pos);
                                         try{
                                         if ( SecureChatUtils.hashPS(namePartOfMessage).equals( namePickerThing.substring(pos+6, namePickerThing.length())))
                                         {

                                          //System.out.println( " checksum works Pause server here ");                    
                                         }
                                         else
                                         {
                                            System.out.println( "Invalid message on integrity check" );
                                         }
                                         }catch (Exception wer){}
                                          
                                         namePickerThing = namePartOfMessage;

                                         System.out.println( "Selected: " + namePickerThing);
                                         Printw2 temp= null;

                                         Boolean sparker = false;

                                         for(Printw2 wr : prinList )
                                         {
                                              if ( !wr.getName().equals(namePickerThing) )
                                              {
                                                  int uselessvar = 9;                                         
                                              }           
                                              else // this should mean they are connected, ready, and waiting for a chat session
                                              {
                                                  sparker = true;

                                                  validName = true;
                                                  temp = wr;
                                                  //System.out.println( "We can proxy now!!");
                                                  

                                                  // ticket should be generated here
                                                  // this and 2 more steps of the protocol to go
                                                  
                                                  // Producing Ticket: {K_AB | hash( K_AB ) }K_BS
                                                  String theKAB = SecureChatUtils.hashPS(SecureChatUtils.nonce(32) ).substring(0,32);
                                                  tck = (theKAB + SecureChatUtils.hashPS(theKAB));
                                                  Ticket = (SharedKey.encrypt(tck, sessionKey_Kas, anIV.trim()));
                                   
                                                  
                                    
                                                  // Server to Alice: {K_AB , ticket |hash(K_AB, ticket))K_AS
                                                  ticket_encryption = theKAB + Ticket;
                                                  s_to_a = ( theKAB + Ticket + SecureChatUtils.hashPS(ticket_encryption));
                                                  /*
                                                  System.out.println( "\n Server sends ticket and shared key to the client \n" + SharedKey.encrypt( s_to_a, sessionKey_Kas, anIV.trim()) + "\n");
                                                  
                                                  System.out.println("\n TICKET IS \n" + Ticket + "\n");
                                                  */
                                                  wr.println("PROXY"+"\t"+theKAB+"\t"+newBobPort+"\t"+newAlicePort);
                                                                                                    
                                                  //userNameOfClient - chat session requester
                                                  for(Printw2 wr2 : prinList )
                                                  {
                                                    if ( wr2.getName().equals(userNameOfClient) )
                                                    {
                                                        wr2.println("PROXY"+"\t"+theKAB+"\t"+newAlicePort+"\t"+newBobPort);
                                                    }
                                                  }

                                                  newBobPort += 2;
                                                  newAlicePort += 2;

                                                  //System.out.println( "Hopefully this crazy thing worked..");                                                  

                                                   temp  = null;
                                                   Printw2 temp2 = null; 
                                                   int polly =0;
                                                   for(Printw2 wr3 : prinList )
                                                   {
                                                        
                                                        polly += 1;
                                                        if ( wr3.getName().equals(userNameOfClient) && temp2 == null)
                                                        {
                                                            
                                                            temp = wr3;
                                                        }           
                                                        else
                                                        {
                                                        temp = wr3;
                                                        }
                                                        if ( wr3.getName().equals(namePickerThing) && temp2 == null)
                                                        {
                                                          temp2 = wr;
                                                        }
                                                        
                                                   } // end of Printw2 for loop
                                                  try
                                                  {
                                                      temp.close();
                                                      temp2.close();
                                                  } catch ( Exception probablyAlreadyDisconnected) {}                                                  
                                                  prinList.remove( temp);
                                                  prinList.remove( temp2);                                                  
                                                  leaveHandleClient = true;                                                  
                                                  break;
                                              } // end of else whose inside corresponds to a name match

                                         } // end of prinList for
                                         //serverToClient.println("Not there, try another person or wait");
                                         //namePickerThing = inFromClient.readLine();
                                         if (leaveHandleClient  == true){  break;   }

                                         if ( !sparker ) {serverToClient.println( namePickerThing + " is not available"); } 

                                         //try{Thread.sleep(500); }catch(Exception tralalalala){}
                                    } // end of while loop
                                }
                                else
                                {
                                    serverToClient.println("The password was incorrect");
                                }                                
                            } // end of if(value != null)
                            else {
                                serverToClient.println("No matching username, try again");
                                continue;
                            }                           
                      }                                        
                      
                    }
              }  catch (IOException ex){ } 
                 catch ( NullPointerException e ) {  }
                 catch ( Exception ex2 ) {  }
               /*   take the connection off prinList    */
		           finally
               {
                   
		          } // end of the finally
     } // end of run
 }// end of handle client
} // is this the end of the class definition?? who knows..


