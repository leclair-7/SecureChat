////////////////////////////////////////////////////////////

                    //serverToClient.print("Enter alias: ");
                    String[] inputCheck = cl_Alias.split(" ",2);

                    serverToClient.println("\nACK Alias accepted \n");
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
                    // end of chat where broadcast to everybody online who's online when new person logs in



			        //takes in message and then handles them accordingly
                    message= "init";
                    while ( true  )
                    {
                        if ( message == null)			
                        {
                            return; 
                        }
                        message = inFromClient.readLine();

                        //print on the server terminal for diagnostics
                        // System.out.println(message) ;
                        String[] pa = message.split(" ",3);   

                        switch ( pa[0] ) {
                            case "MESG":
                                for(Printw2 pw: prinList ){ 

                                    if  ( !(pw.getName().equals(cl_Alias )))
                                    {
                                        pw.println(cl_Alias + ": " + message );

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
                               
                               // take person off list
                               Printw2 temp= null; 
                               for(Printw2 wr : prinList )
                               {

                                    if ( !wr.getName().equals(cl_Alias) )
                                    {
                                        int sortOfNumPeople = NumPeople - 1;

                                        wr.println("ACK " + cl_Alias + ", has exited session, "+ sortOfNumPeople+ " are still in the session" );
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
                        } // end of the switch, hopefully
                    } // end of while loop

