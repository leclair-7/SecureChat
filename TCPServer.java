/**
 * Created by Lucas Hagel on 9/21/2015. After he gave up on doing this in Python
 */

import java.util.HashSet;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPServer {
    private static int NumPeople;

    public TCPServer (){ NumPeople =0;
    }
    public static void main(String argv[]) throws Exception 
    {
        ServerSocket welcomeSocket = new ServerSocket(12004);
        //we have the server sock continuously listen for new incoming connections and then handle them accordingly.
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

    private static class Printw2 extends PrintWriter {

        private String alias;

        public String getName(){return alias;} 

        public PrintWriter p;

        public Printw2( String st, OutputStream out) { 
            super(out, true);
            alias = st;
        }
    }
	//makes a new thread per client that it is handling
    private static class HandleClient extends Thread {
        private Socket connectionSocket;
        private PrintWriter see_out;
        String cl_Alias = "";
        String message;

        private Printw2 prin;

        public HandleClient(Socket sock) {
            this.connectionSocket = sock;           
            //this.run();    
        }

	
        @Override
            public void run() {
                try {
                    //create input stream attached to socket
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    see_out =  new PrintWriter(connectionSocket.getOutputStream(), true);

                    //see_out.print("Enter alias: ");
                    Boolean fla = false;
                   String[] inputCheck = cl_Alias.split(" ",2);



                    while ( !fla )
                    {
			cl_Alias = inFromClient.readLine();	
			inputCheck = cl_Alias.split(" ",2);
		       if ( cl_Alias.startsWith("REG "))
                        {

                            if ( !(cl_Alias.contains(" ")))
                            {
                                continue;
                            }
                            else if (  cl_Alias.length() > 4 )
                            {

                                if(  cls_name.contains(inputCheck[1]))
                                {
                                    see_out.println("\nERR 0: Name already taken, enter another one: " );
                                    continue;
                                }
                                else
                                {
                                    fla = true;
                                }
                            }
                        }

                        else
                        {
                            see_out.println("ERR 2: Unknown message format.");
                            see_out.println("Input alias with 'REG alias' formatting");

                        }
		}

                    see_out.println("\nACK Alias accepted \n");
		    cl_Alias = inputCheck[1].trim();
			cls_name.add(cl_Alias);
                    prin = new Printw2( cl_Alias, connectionSocket.getOutputStream() );

                    prinList.add(prin);
                    NumPeople += 1;
                    String nameLi = "";
                    for (Printw2 pr : prinList)
                    {
                        nameLi = nameLi + pr.getName() + " ";

                        if ( !(pr.getName().equals(cl_Alias))  )

                        {
                           pr.println("MESG " + cl_Alias + " has entered the chat session");
                        }
                    }


                    for (Printw2 pr: prinList)
                    {
                        pr.println("Currently, "+NumPeople +" chat members are online: "+ nameLi+"\n");
                    }

			//takes in message and then handles them accordingly
                    String messg= "init";
                    while ( true  )
                    {
                        if ( messg == null)
			
                        {
                            return; 
                        }
                        messg = inFromClient.readLine();
                        //print for diagnostics
                        // System.out.println(messg) ;
                        String[] pa = messg.split(" ",3);   

                        switch ( pa[0] ) {
                            case "MESG": 

                                for(Printw2 pw: prinList ){ 

                                    if  ( !(pw.getName().equals(cl_Alias )))
                                    {
                                        pw.println(cl_Alias + ": " + messg );

                                    }
                                }
                            //broadcast to all
                            break;
                            case "ACK": 
                                System.out.println("ack");
                            //
                            break;
                            case "PMSG": 
                                // send to specific person
                                String str_p ="";

                            if (pa[2] == null){str_p =  pa[2];}
                            else { str_p = pa[2];}
                            for (Printw2 pw : prinList)
                            {
                                if ( pw.getName().equals(pa[1]) )
                                {
                                    pw.println( "PM from " + cl_Alias + ": " + str_p );     
                                }
                            }
                            break;
                            case "EXIT": 
                               NumPeople = NumPeople - 1;
                               // take person off list
                               Printw2 temp= null; 
                               for(Printw2 wr : prinList ){

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
                                temp.close();
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


			   break;

                            default:
                        }
                    } // end of while loop



              }  /* end of try */ catch (IOException ex) {
                   //   System.out.println("this is about to run" );
               		ex.printStackTrace();
	       }
                catch ( NullPointerException e )
                {       
                }
		finally{
                               NumPeople = NumPeople - 1;
                               // take person off list
                               Printw2 temp= null; 
                               for(Printw2 wr : prinList ){

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
                                temp.close();
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
