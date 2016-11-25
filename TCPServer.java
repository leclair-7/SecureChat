
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


import javax.crypto.Cipher;

import java.util.Base64;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SealedObject;

public class TCPServer {

  static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  static SecureRandom rnd = new SecureRandom();

  public static String nonce( int len )
  {
   StringBuilder sb = new StringBuilder( len );
   for( int i = 0; i < len; i++ ) 
      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
   return sb.toString();
  }


  public static String hashPS( String password) throws NoSuchAlgorithmException
  {    
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    
    // I'm not sure what the hell this update thing does
    md.update(password.getBytes() );

    //this computes the hash
    byte byteData[] = md.digest();

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
     sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }
    
    // that v v v v outputs 32 as it should
    //System.out.println( byteData.length);
    //System.out.println("Hex format : " + sb.toString());
    return sb.toString();
  }

  public static byte[] encrypt(String text, PublicKey key) {
    byte[] cipherText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // encrypt the plain text using the public key
      cipher.init(Cipher.ENCRYPT_MODE, key);
      cipherText = cipher.doFinal(text.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cipherText;
  }

  /**
   * Decrypt text using private key.
   * 
   * @param text
   *          :encrypted text
   * @param key
   *          :The private key
   * @return plain text
   * @throws java.lang.Exception
   */
  public static String decrypt(byte[] text, PrivateKey key) {
    byte[] dectyptedText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);

      // decrypt the text using the private key
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(text);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return new String(dectyptedText);
  }

    public static final String ALGORITHM = "RSA";
    private static HashMap < String, userInfo > userProfiles;
    private static int NumPeople;

    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    private static String publicKeyAsString;
    private static String sortOfSharedKey;
    private static String sortOfIV;

    public TCPServer (){ 

        NumPeople =0;
        userProfiles = new HashMap<>();
    
        String fileName = "chatusers.txt";    
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
          String line;
          while ((line = br.readLine()) != null) {
            String[] line_arr = line.split("\\s+");
            //System.out.println(line_arr[0] + line_arr.length);
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

    public static HashSet<String> cls_name = new HashSet<String>();
    public static HashSet<Printw2> prinList = new HashSet<Printw2>();

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
        String cl_Alias = "";
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
                    //System.out.println(publicKeyAsString);
                    //decrypt and see if it is a user / ps combination

                    Boolean isAuthenticated = false;
                    String plainText = "";
                    while ( !isAuthenticated )
                    {
                      /*
                      *       
                      *
                      *
                      */
                      SealedObject clientInfo = (SealedObject) inStream.readObject();

                      Cipher dec = Cipher.getInstance("RSA");
                      // Initiate the Cipher, telling it that it is going to Decrypt, giving it the private key
                      dec.init( Cipher.DECRYPT_MODE, privateKey );
                      String message = (String) clientInfo.getObject(dec);  

                      System.out.println("The catch = "+ message);

                      /*
                      String student = (String) inStream.readObject();
                      System.out.println("Object received = " + student);
                      */

                      // should decrypt to the sortof
                      //(sss.substring(32, 47));
                      if ( message.length() != 48)
                      {
                        System.out.println("initial message decrypt fail");
                        continue;
                      }


                      String theAuthCred = inFromClient.readLine();
                      

                      System.out.println("pausing server here: " + theAuthCred);
                      String aSKey = message.substring(0, 32);
                      String anIV = message.substring(32, 48); 

                      //SharedKey.decrypt(String encrypted, String key, String initVector)
                      String isItThemString = SharedKey.decrypt( theAuthCred , aSKey, anIV.trim());                     
                      System.out.println("Is it them line: " + isItThemString);
                      //System.out.println("IV length: " + anIV.length() + " ??? "+ anIV + " !! "+ aSKey);

                      //String sessionKey
                      System.out.println("pausing server here 1");
                      cl_Alias = inFromClient.readLine();

                      System.out.println("pausing server here 2");
                      cl_Alias = inFromClient.readLine();
                      
 
                      System.out.println("pausing server here 3");
                      cl_Alias = inFromClient.readLine();
                      
                      cl_Alias = inFromClient.readLine();
                      cl_Alias = decrypt( cl_Alias.getBytes(), privateKey);
                      System.out.println("cl_Alias: "+ cl_Alias);
                      
                      // 1 line will be the shared key encrypted by the public key 
                      
                      
                      plainText = decrypt( cl_Alias.getBytes(), privateKey);
                      
                      System.out.println("plainText "+ plainText);
                      
                      String [] userAuth1 = plainText.split("\\s+"); 
                      if ( userAuth1.length != 3 )
                      {
                        System.out.println("Unable to authenticate, try again" );
                      }
                      else if ( userProfiles.containsKey(userAuth1) )
                      {
                          if ( userAuth1[1].equals(userProfiles.get(userAuth1[0]).getPS() ))
                          {
                              isAuthenticated = true;
                              System.out.println("Authenticated, you are in" );
                          }
                      }
                    }


              }  /* end of try */ catch (IOException ex){
                   //   System.out.println("this is about to run" );
               		//ex.printStackTrace();
	           } catch ( NullPointerException e ) {  }
               catch ( Exception ex2 ) {  }
		      finally{
                   NumPeople = NumPeople - 1;

                   // find person's named printwriter object and then remove from prinList
                   Printw2 temp= null; 
                   for(Printw2 wr : prinList )
                   {
                        if ( !wr.getName().equals(cl_Alias) )
                        {
                            wr.println("ACK " + cl_Alias + ", has exited session, "+ NumPeople+ " are still in the session" );
                           	cls_name.remove(cl_Alias);
        			    }			      
    				    else
    				    {
    						temp = wr;
    				    }
				    }

                try{
                    temp.close();
                } catch ( Exception probablyAlreadyDisconnected) {}
                prinList.remove( temp);
                           
			   	String te = "";
			   	for ( Printw2 person: prinList)
				{
					te = te +" " +  person.getName();
				}
				for (Printw2 per : prinList)
				{
					per.println("Current chatters: " + te);				
				}
		}

     } // end of run
 }// end of handle client

}


