
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
    public  static int messageNumber;

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

    public TCPClient() 
    {
        protocolLoginStep1 = false;
        protocolLoginStep2 = false;
        protocolLoginStep3 = false;
        protocolLoginStep4 = false;
        messageNumber = 0;
    }
    
    public void run() throws IOException {

       BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

       //sends address to DNS, sends connection request
       Socket clientSocket = new Socket("localhost", 12004);

       //the true is to autoflush KEEP IT IN!!
       PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
       ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

       
       Boolean t = true;
       
       //starts a new thread to take in information from the server 
       // just reads FROM the server... until it doesn't
       Client_IO clll = new Client_IO(clientSocket);
       new Thread( clll ).start(); 
       
        ////  Change to read from a file or something for name, etc. --------------------------------
        System.out.println("Type in your <username password> here: ");
        String forLogin = "";
        while (t) 
        {                 
                // user puts in alias password here, this is where that's stopping
                System.out.println("We see this ll");
                sentence = inFromUser.readLine();
                System.out.println("This came afterwards");
                String [] userAndPS = sentence.split("\\s+");
                if ( userAndPS.length != 2 ) 
                { 
                    System.out.print("You are an idiot");
                    System.out.print("Type in your <username password> here: ");
                    continue;
                }

                try 
                {
                    String nonceForSession = SecureChatUtils.nonce(1024);
                    sessionKey_Kas = SecureChatUtils.hashPS( nonceForSession ).substring(0,32);

                    forLogin = userAndPS[0] +"\t"+ SecureChatUtils.hashPS(userAndPS[1]) +
                               "\t"+ nonceForSession;

                    if ( publicKey != null)
                    {
                        forLogin = SharedKey.encrypt( forLogin, key, initVector).toString();
                    }
                    
                    /*
                    *     Presumably we use the server's public  key to  
                    *           encrypt send a shared key then initial 
                    *           info to authenticate
                    */
                    String sharedKey_and_IV= key + initVector;

                    //outToServer.println( encryptInfo );
                    String aaaa="sunny side up";

                    // start with public key as a string, get back to public key

                    byte[] originalPublicKey = Base64.getDecoder().decode(publicKeyAsString);
                    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(originalPublicKey);      
                    KeyFactory keyFact = KeyFactory.getInstance("RSA");
                    PublicKey pubKey2 = keyFact.generatePublic(x509KeySpec);
                    Cipher c = Cipher.getInstance("RSA");
                    // Initiate the Cipher, telling it that it is going to Encrypt, giving it the public key
                    c.init(Cipher.ENCRYPT_MODE, pubKey2 );

                    SealedObject myEncryptedMessage= new SealedObject( sharedKey_and_IV, c);    
                    outputStream.writeObject(myEncryptedMessage);

                }catch (Exception exc){}

                if ("exit".equals(sentence)) {
                    t = false;
                }
                outToServer.println(forLogin);    
        }// end of while

        clientSocket.close();
    } //end of run

    ////  Change to read from a file or something for name, etc. --------------------------------
    public static void main(String argv[]) throws Exception {
    
        TCPClient person = new TCPClient();
        person.run();
    }


// this class gets messages from the server

public  class Client_IO implements Runnable {

    private Socket fromServerIO;
    private String a_msg;

    //if messageNumber  = 0 then we check to see if we received the public key from the server
    int messageNumber = 0;

    public Client_IO ( Socket so)
    {
        this.fromServerIO = so;
    }    

    public void run() {
    a_msg ="";
        try {
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(fromServerIO.getInputStream()));
            while (true) {

        // this message is long to see if CBC is working among other things
        String message = "Perhaps you will tire sooner than he will. It is a sad thing to think of, but there is no doubt that genius lasts longer than beauty. That accounts for the fact that we all take such pains to over-educate ourselves. In the wild struggle for existence, we want to have something that endures, and so we fill our minds with rubbish and facts, in the silly hope of keeping our place. The thoroughly well-informed man—that is the modern ideal. And the mind of the thoroughly well-informed man is a dreadful thing. It is like a bric-a-brac shop, all monsters and dust, with everything priced above its proper value. I think you will tire first, all the same. Some day you will look at your friend, and he will seem to you to be a little out of drawing, or you won't like his tone of colour, or something. You will bitterly reproach him in your own heart, and seriously think that he has behaved very badly to you. The next time he calls, you will be perfectly cold and indifferent. It will be a great pity, for it will alter you. What you have told me is quite a romance, a romance of art one might call it, and the worst of having a romance of any kind is that it leaves one so unromantic.";

        //System.out.println(key.length());

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

                    System.out.println("We has the public key");
       
                 //System.out.println("And we happy? good.");
                }
                else if ( messageNumber == 1)
                {
                    // this should be the buddy list
                    a_msg = fromServer.readLine();                    
                    System.out.println("buddyList and hash: "+ a_msg);

                    // parse a_msg so we know who we can talk to
                    System.out.println(SharedKey.decrypt(a_msg, sessionKey_Kas , initVector ));
                    String line = SharedKey.decrypt(a_msg, sessionKey_Kas , initVector );
                    int pos = line.toLowerCase().indexOf("ACK_X1".toLowerCase());    
                    //System.out.println( "Found at: " + pos  );
                    System.out.println( "Buddy List: " + line.substring(0,pos-1).trim() );
                    System.out.println( "Who would you like to chat with?");
                    /*
                        line.substring(0,pos-1).trim()
                    */                    
                    a_msg = fromServer.readLine();
                    messageNumber += 1; 
                }
                else if ( messageNumber == 2)
                {
                  System.out.println("got here to messageNumber = 2 ");
                    a_msg = fromServer.readLine();
                }

                System.out.println("Server 1");
                //a_msg = fromServer.readLine();
                System.out.println("Server 2");

                if (a_msg == null) {return;}       			         
                 System.out.println( a_msg );                

            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        catch (Exception bleh) {
            System.err.println(bleh);
        }
    } // end of clientIO run
}// end of clientio


}
