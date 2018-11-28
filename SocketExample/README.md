To use:

GreetingServer: 
javac GreetingServer
java GreetingServer <port number, e.g. 9997>

GreetingClient:
javac GreetingClient
java GreetingClient localhost <same port number>

Enter a string into the client, and it will be sent to server and then sent to all clients currently connected to server.