How to use the chat server/client
by: Lucas Hagel


To use this chat application, first log onto cs2.utdallas.edu. Put the files contained in this folder onto your cs2.utdallas.edu account using winscp or something else. Next, in the bash terminal, type "javac *.java" to compile the client and the server. Then, open a new terminal and log in to cs2.utdallas.edu with your netid. Then ssh into net14.utdallas.edu with your netid credentials. cd to the same folder that you put the .java files in. Next start the server by typing, "java TCPServer".

For each client login type, "java TCPClient" on the shell ( assuming it is a bash shell). You will be prompted to select an alias for the chat sesssion. Type "REG <username>" where <username> is the username you want to go by for the duration of the session. Then to send a message to everybody on the chat type 'MESG <message>' where <message> is what you want to broadcast to everybody. To send to a particular user type "PMSG <message>"  where <message> is what you want to broadcast to everybody. To exit type "EXIT" and then hit Enter. 