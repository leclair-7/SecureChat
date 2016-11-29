
import java.io.*;
import java.net.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;
import java.util.Base64;
import javax.crypto.SealedObject;

class TCPClient {

    /* incoming message handling objects */
    String sentence;
    String modifiedSentence;
    String alias = "Bambino";
    public static int messageNumber;

    /* public key */
    private static String publicKeyAsString; 
    private static PublicKey publicKey;
    public static String serverPublicKey;

    /* protocol login step switches */
    private static Boolean protocolLoginStep1;
    private static Boolean protocolLoginStep2;
    private static Boolean protocolLoginStep3;
    private static Boolean protocolLoginStep4;
    
    /* shared key keys */
    public String key = "Bar12345Bar12345Bar12345Bar12345"; // 256 bit key --> key.length() == 32 bytes
    public String initVector = "RandomInitVector"; // 16 bytes IV
    public String sessionKey_Kas;

    public static int onStep;
    public String myBuddyListString;
    public String nameSelectionAttemptChat;
    public Boolean tcpClientMode;

    

    public TCPClient() 
    {
        protocolLoginStep1 = false;
        protocolLoginStep2 = false;
        protocolLoginStep3 = false;
        protocolLoginStep4 = false;
        messageNumber = 0;

        onStep = 0;
        myBuddyListString = "";
        nameSelectionAttemptChat = "";
        tcpClientMode = false;
    }
    
    public void run() throws IOException {

       BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

       //sends address to DNS, sends connection request
       Socket clientSocket = new Socket("localhost", 12004);

       //the true is to autoflush KEEP IT IN!!
       PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
       ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());    
       
       /*
       Client_IO clll = new Client_IO(clientSocket);
       new Thread( clll ).start(); 
       */
       Boolean haveServerPublicKey = false;

       BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        try{    
            while ( !haveServerPublicKey) 
            {
                if ( messageNumber==0 )
                {
                    serverPublicKey = fromServer.readLine();
                    messageNumber += 1;
                    //System.out.println( serverPublicKey );
                    publicKeyAsString = serverPublicKey;

                    byte[] originalPublicKey = Base64.getDecoder().decode(publicKeyAsString);
                    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(originalPublicKey);      
                    KeyFactory keyFact = KeyFactory.getInstance("RSA");
                    publicKey = keyFact.generatePublic(x509KeySpec);

                    haveServerPublicKey = true;
                    System.out.println("We have the public key");
                }
            }

        } catch ( Exception nosuchalgy){}
       
       /*
       System.out.println("Pausing client because reasons");
       String newVar = fromServer.readLine();
       newVar = fromServer.readLine();
       newVar = fromServer.readLine();
       newVar = fromServer.readLine();
       newVar = fromServer.readLine();
       */

        int protocolTracker = 1;
        Boolean areTheyIn = false;
        ////  Change to read from a file or something for name, etc. --------------------------------
        
        String forLogin = "";
        while (true) 
        {                 
                // user puts in alias password here, this is where that's stopping
                //System.out.println("We see this ll");
                if ( protocolTracker == 1 )
                {
                    while ( ! areTheyIn)
                    {    

                        System.out.println("Type in your <username password> here: ");
                        sentence = inFromUser.readLine();
                        //System.out.println("if you don't see this then yes mate");

                        //System.out.println("This came afterwards");
                        String [] userAndPS = sentence.split("\\s+");
                        if ( userAndPS.length != 2 && onStep != 3 ) 
                        {                             
                            System.out.println("Invalid request format, type the <username password> here: ");
                            continue;
                        }
                        // --------------------  sends object with shared key encrypted with server public key    ----------------------------------
                        // ------------------------------    THEN it sends login info   ------------------------
                        try 
                        {
                            String nAuth_sessionNonce = SecureChatUtils.nonce(1024);

                            /*Step 1 part 1 of 3 of protocol */                            
                            forLogin = userAndPS[0] +"\t"+ SecureChatUtils.hashPS(userAndPS[1]) +
                                       "\t"+ nAuth_sessionNonce;

                            sessionKey_Kas = SecureChatUtils.hashPS( nAuth_sessionNonce ).substring(0,32).trim();
                            
                            if ( publicKey != null)
                            {
                                forLogin = SharedKey.encrypt( forLogin, sessionKey_Kas, initVector).toString();
                            }

                            /*Step 2 of protocol */   
                            

                            /*
                            *     Presumably we use the server's public  key to  
                            *           encrypt send a shared key then initial 
                            *           info to authenticate
                            */
   
                            String sharedKey_and_IV= sessionKey_Kas + initVector;

                            byte[] originalPublicKey = Base64.getDecoder().decode(publicKeyAsString);
                            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(originalPublicKey);      
                            KeyFactory keyFact = KeyFactory.getInstance("RSA");
                            PublicKey pubKey2 = keyFact.generatePublic(x509KeySpec);
                            Cipher c = Cipher.getInstance("RSA");
                            // Initiate the Cipher, telling it that it is going to Encrypt, giving it the public key
                            c.init(Cipher.ENCRYPT_MODE, pubKey2 );
                            SealedObject myEncryptedMessage= new SealedObject( sharedKey_and_IV, c);

                            /*Step 1 part 2 of 3 of protocol */     
                            outputStream.writeObject(myEncryptedMessage);
                        }catch (Exception exc){}    

                        /*Step 1 part 3 of 3 of protocol */ 
                        // send the {user, hash(PS), nonce}K_Shared to server
                        outToServer.println(forLogin);
                        String resultOfLoginRequest = fromServer.readLine();

                        if (resultOfLoginRequest.equals("Access Granted")) 
                        {
                            protocolTracker = 2;
                            areTheyIn = true;
                            break;
                        }                        

                        System.out.println(resultOfLoginRequest);

                        // --------------------------------------------------------------------   
                        // -------------------------------------------------------------------- 
                    }//end of while 
                } // end of protocolTracker == 1 

            
                if ( protocolTracker == 2)
                {
                    String currentMessageFromServer = fromServer.readLine();                  
                    
                    String line = SharedKey.decrypt(currentMessageFromServer, sessionKey_Kas , initVector );
                    int pos = line.toLowerCase().indexOf("ACK_X1".toLowerCase());                        

                    System.out.println( "Buddy List: " + line.substring(0,pos-1).trim() );
                    onStep = 3;
                    myBuddyListString = line.substring(0,pos-1).trim();
                    System.out.println( "Who would you like to chat with?"); 

                    String[] namesArray = myBuddyListString.split("\\s+");
                    List<String> list = Arrays.asList(namesArray); 
                    Set <String> nameList = new HashSet<String>(list);
                        
                    Boolean selected = false;
                    String serverResult = "";
                    String selectedName = "";

                    System.out.println( "buddy list: ");
                    for ( String ss : nameList )
                    {
                    System.out.println(ss );
                    }
                    System.out.println( "");
                    while( !selected )
                    {                                                                  
                        /* in its current setup, you'll have to loop through the entire hashset */
                        if ( (selectedName = inFromUser.readLine()) != null && nameList.contains(selectedName) )
                        {
                            System.out.println("Checking with server to see if " + selectedName + " is available"); 

                            try{// this is Alice --> Bob : {bob || hash(bob) } KAS
                            selectedName = selectedName + "ACK_X1" + SecureChatUtils.hashPS(selectedName);                           
                            outToServer.println( SharedKey.encrypt( selectedName, sessionKey_Kas, initVector ) );
                            } catch (Exception asdfasdf){}


                            if ( ( (serverResult = fromServer.readLine()) != null) && serverResult.startsWith("PROXY") )
                            {
                                //insert proxy connect code here
                                /*
                                System.out.println("Finally got the right buddy thing, pausing client here");
                                System.out.println( "Lucas uno: " + serverResult);
                                String resultOfLoginRequest4 = fromServer.readLine();
                                resultOfLoginRequest4 = fromServer.readLine();
                                System.out.println(resultOfLoginRequest4);
                                resultOfLoginRequest4 = fromServer.readLine();
                                resultOfLoginRequest4 = fromServer.readLine();
                                */
                                    System.out.println("Server: yes, " + selectedName +" is available, waiting for him/her to select you");
                                    String [] funPart = serverResult.split("\\s+");
                                    //shared key is funPart[1]
                                    int clientPort = Integer.parseInt(funPart[2]);
                                    int serverPort = Integer.parseInt(funPart[3]);
                                    EchoServer e = new EchoServer( serverPort, clientPort, funPart[1] );
                                    tcpClientMode = true;  
                                    break;                         

                            }
                            else
                            {
                                System.out.println("The selected person is not available, try another person or wait");
                            }
                                                                                   
                        } 
                        else
                        {
                            System.out.println("Name not on your buddy list try again:");                                
                        } 
                        //sentence = inFromUser.readLine();
                    }
                }
                if ( tcpClientMode)
                {
                    break;
                }

                
                if ("exit".equals(sentence)) {
                    break;
                }
        }// end of while
        try{
        clientSocket.close();
    } catch (Exception purrt) {}
    } //end of run

    ////  Change to read from a file or something for name, etc. --------------------------------
    public static void main(String argv[]) throws Exception {
    
        TCPClient person = new TCPClient();
        person.run();
    }


// this class gets messages from the server

public  class Client_IO implements Runnable {

    private Socket fromServerIO;
    private String currentMessageFromServer;    

    public Client_IO ( Socket so)
    {
        this.fromServerIO = so;
    }    

    public void run() {
    currentMessageFromServer ="";
        try {
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(fromServerIO.getInputStream()));
            while (true) {

                if ( messageNumber==0 )
                {
                    serverPublicKey = fromServer.readLine();
                    messageNumber += 1;
                    //System.out.println( serverPublicKey );
                    publicKeyAsString = serverPublicKey;

                    byte[] originalPublicKey = Base64.getDecoder().decode(publicKeyAsString);
                    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(originalPublicKey);      
                    KeyFactory keyFact = KeyFactory.getInstance("RSA");
                    publicKey = keyFact.generatePublic(x509KeySpec);

                    System.out.println("We have the public key");
       
                 //System.out.println("And we happy? good.");
                }
                else if ( messageNumber == 1)
                {
                    // this should be the buddy list
                    currentMessageFromServer = fromServer.readLine();                   
                    
                    String line = SharedKey.decrypt(currentMessageFromServer, sessionKey_Kas , initVector );
                    int pos = line.toLowerCase().indexOf("ACK_X1".toLowerCase());                        

                    System.out.println( "Buddy List: " + line.substring(0,pos-1).trim() );
                    onStep = 3;
                    myBuddyListString = line.substring(0,pos-1).trim();
                    System.out.println( "Who would you like to chat with?");               
                    
                    messageNumber += 1; 
                }
                else if ( messageNumber == 2)
                {
                    //System.out.println("got here to messageNumber = 2, awaiting proxy connect info");
                    currentMessageFromServer = fromServer.readLine();
                    if ( currentMessageFromServer.startsWith("PROXY") && currentMessageFromServer.split("\\s+").length == 4)
                    {
                        String [] funPart = currentMessageFromServer.split("\\s+");
                        //shared key is funPart[1]
                        int clientPort = Integer.parseInt(funPart[2]);
                        int serverPort = Integer.parseInt(funPart[3]);

                        EchoServer e = new EchoServer( serverPort, clientPort, funPart[1] );
                        tcpClientMode = true;
                    }
                    else
                    {

                    }
                }
                if (currentMessageFromServer == null) {return;}			   
            }
        } catch (IOException ex) {}
        catch (Exception bleh) {}
    } // end of clientIO run
}// end of clientio


}
