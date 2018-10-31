#### How to use the chat server/client
by: Lucas Hagel, and Susmitha Manda

#### Purpose:
This repo implements an encryption protocol made my the authors for a graduate network security course. It is a Java based chat application that provides confidentiality, authenticity, and integrity.

It uses a multithreaded server (one thread to handle each client), a client/server handshake for authentication, and a connection passing mechanism from client-server to client-client during connection phases of the session. In the end two users can chat.

#### Dependencies:
This project was implemented and tested on Ubuntu 16.04 with Java 1.8

#### Build and Run Info

Note: usernames and passwords can be found in chatusers.txt

Compile and Running instructions:
1. In the bash terminal, type "javac *.java" to compile the client and the server.
2. Open 3 new terminals, in one of the terminals start the server by typing, "java TCPServer".

3. In another bash shell login as a client: type, "java TCPClient".
4. You will be prompted to type in a "username<space>password" that already exists on the system. Type in a username and password.

5. Login as a different user using Steps 3-4.
6. On the first client, type in a buddy name (case-sensitive), and hit enter. If the buddy whose name the first client, buddy 2, entered is online, then buddy 2 has to type in the name of the first client, then they can chat.
