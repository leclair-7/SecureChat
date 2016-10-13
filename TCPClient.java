/**
 * Created by Lucas Hagel on 9/21/2015.
 */
import java.util.Scanner;
import java.io.*;
import java.net.*;
class TCPClient {

    String sentence;
    String modifiedSentence;
    String alias = "Bambino";

//Create input stream
	
    public void run() throws IOException {

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        //sends address to DNS, sends connection request
        Socket clientSocket = new Socket("localhost", 12004);
      //the true is to autoflush KEEP IT IN!!
      PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        //DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

       Boolean t = true;
	//starts a new thread to take in information from the server 
       Client_IO c = new Client_IO(clientSocket);
       new Thread(c ).start();
	
	System.out.print("Type in REG followed by your desired username to register: ");
	while (t) { 
		sentence = inFromUser.readLine();

            if ("exit".equals(sentence)) {
                t = false;
            }
            outToServer.println(sentence);    
        }// end of while

        clientSocket.close();
    } //end of run

    public static void main(String argv[]) throws Exception {
	
        TCPClient person = new TCPClient();
        person.run();
    }


public  class Client_IO implements Runnable{
    //inheriting at the outset if case we want to add anything to it
    private Socket fromServerIO;
    private String a_msg;

    public Client_IO ( Socket so)
    {
        this.fromServerIO = so;

}

    public void run() {
	a_msg ="";
        try {
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(fromServerIO.getInputStream()));
            while (true) {
                a_msg = fromServer.readLine();
                if (a_msg == null) {return;}
       			         
                 System.out.println( a_msg );

            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }
}// end of clientio



}
