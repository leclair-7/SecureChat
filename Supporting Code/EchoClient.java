import java.io.*;
import java.net.*;

public class EchoClient {

    /*
        when we put this on TCPClient, we'll replace SharedKey.key with shared key in the constructor
    */

    public EchoClient( int serverPort, int clientPort )
    {
        Runnable theServer = new theServer( serverPort );
        Thread recieveInfo = new Thread( theServer );
        
        Runnable theClient = new theClient( "localhost", clientPort );
        Thread sendInfo = new Thread( theClient );
        
        recieveInfo.start();
        sendInfo.start();
    }

    public static void main(String[] args) throws IOException {
        
        EchoClient e = new EchoClient( 12006, 12005);        
    }  
    
}

class theClientBob implements Runnable {

    private String hostName;
    private int portNumber;

    public theClientBob ( String h, int p)
    {
        hostName = h;
        portNumber = p;
    }

    public void run() 
    {
        /*
        if (args.length != 2) {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        */
        while (true)
        {
        try {
            Socket echoSocket = new Socket(hostName, portNumber);
            PrintWriter out =
                new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream()));
            BufferedReader stdIn =
                new BufferedReader(
                    new InputStreamReader(System.in));
         
            String userInput;
            System.out.println("got here");
            
            while ((userInput = stdIn.readLine()) != null ) 
            {
                 /* 
                    the following sends {message || hash(message) }K_ab 
                    to the other person
                */                   
                
                
                userInput = userInput+ "ACK_X1" + SecureChatUtils.hashPS(userInput);   
                userInput = SharedKey.encrypt( userInput, SharedKey.key, SharedKey.initVector);
                out.println(SharedKey.encrypt(userInput, SharedKey.key, SharedKey.initVector)); 
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            //System.err.println("Couldn't get I/O for the connection to " + hostName);
            //System.exit(1);
            try{ 
                Thread.sleep(1000);
            }catch (Exception e2){}
            continue;
        } catch (Exception exce) {}


        }//end of while
    
    }
}

// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

class theServerBob implements Runnable {

        private int port;
        public theServerBob ( int portNumber)
        {
            port = portNumber;
        }
        public void run() 
        {
            /*
             if (args.length != 1) {
                System.err.println("Usage: java EchoServer <port number>");
                System.exit(1);
            }
            
            int portNumber = Integer.parseInt(args[0]);
            */
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();

                System.out.println("Conected to LHS");    
                       

                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                    
             
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    
                    /* 
                    the following checks for {message || hash(message) }K_ab 
                        to the other person
                    */

                    String semiOriginal = SharedKey.decrypt( inputLine, SharedKey.key, SharedKey.initVector);
                    int pos = semiOriginal.toLowerCase().indexOf("ACK_X1".toLowerCase()); 
                    
                    String msgCheck = semiOriginal.substring(0,pos).trim();
                    

                    if ( SecureChatUtils.hashPS(msgCheck).equals( semiOriginal.substring(pos + 6, semiOriginal.length() ) )) 
                    {
                    
                        System.out.println( "Message Received: " + msgCheck);
                    }
                    else
                    {
                        System.out.println( "Invalid message, may have been tampered with");                
                    } 

                }
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
                System.out.println(e.getMessage());
            } catch ( Exception e){}
        }
}
