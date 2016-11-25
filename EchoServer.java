import java.net.*;
import java.io.*;

public class EchoServer {

    public EchoServer( int serverPort, int clientPort )
    {
        Runnable theServer = new theServer( serverPort );
        Thread recieveInfo = new Thread( theServer );
        
        Runnable theClient = new theClient( "localhost", clientPort );
        Thread sendInfo = new Thread( theClient );
        
        recieveInfo.start();
        sendInfo.start();
    }

    public static void main(String[] args) throws IOException {
        
        EchoServer e = new EchoServer( 12005, 12006);
        
    }
}

class theServer implements Runnable {

        private int port;

        public  theServer ( int portNumber)
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
                ServerSocket serverSocket =
                    new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();

               System.out.println("Connected to RHS");                
                

                PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);                   
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
             
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    //Thread.sleep(1000);
                    System.out.println("Recieved msg: " + inputLine);
                    //out.println("From LHS " + inputLine);
                }
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
                System.out.println(e.getMessage());
            } catch ( Exception e){}

        }

}


// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


class theClient implements Runnable {

    private String hostName;
    private int portNumber;

    public theClient ( String h, int p)
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

        while ( true)
        {
        try 
        {
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
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                
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
        }

        } // end of client while
    
    }
} // end of client server definition