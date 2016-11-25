/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.io.*;
import java.net.*;

public class EchoClient {
    public static void main(String[] args) throws IOException {
        

        Runnable theServerBob = new theServerBob( 26262 );
        Thread recieveInfo = new Thread( theServerBob );                

        Runnable theClientBob = new theClientBob( "localhost", 12004 );
        Thread sendInfo = new Thread( theClientBob );
        
        //recieveInfo.start();
        sendInfo.start();
        
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
            while ((userInput = stdIn.readLine()) != null) 
            {

            System.out.println("got something from the server");
                out.println(userInput);
                //System.out.println("recieved msg: " + in.readLine());
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            //System.exit(1);
            try{ 
                Thread.sleep(1000);
            }catch (Exception e2){}
            continue;
        }


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
                ServerSocket serverSocket =
                    new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();

                System.out.println("Connected to LHS");            

                /*
                PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);     
                */
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
             
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Recieved msg: " + inputLine);
                    //Thread.sleep(1000);
                    //out.println("From RHS --> " + inputLine);
                }
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
                System.out.println(e.getMessage());
            } catch ( Exception e){}
        }
}