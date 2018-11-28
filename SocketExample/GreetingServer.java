// File Name GreetingServer.java
import java.net.*;
import java.io.*;
import java.util.*;


public class GreetingServer extends Thread {
	
   private static List<ObjectOutputStream> clientList = Collections.synchronizedList(new ArrayList<ObjectOutputStream>());  
   private ServerSocket serverSocket;
   
   public GreetingServer(int port) throws IOException {
      
	  try {
      serverSocket = new ServerSocket(port);
	  } catch (IOException e1) {
      e1.printStackTrace();
      }
      //serverSocket.setSoTimeout(10000);
	  System.out.println("Server started on port num " + serverSocket.getLocalPort());
   }

   public void run() {
      while(true) {
         try {
            System.out.println("Waiting for client on port " + 
               serverSocket.getLocalPort() + "...");
            Socket client = serverSocket.accept();
            
            System.out.println("Just connected to " + client.getRemoteSocketAddress());            
			ObjectOutputStream outputToClient = new ObjectOutputStream(client.getOutputStream());
			      ObjectInputStream inputFromClient = new ObjectInputStream(client.getInputStream());
			      clientList.add(outputToClient);
			      ClientHandler handler = new ClientHandler(inputFromClient, clientList);
			      handler.start();
            outputToClient.writeObject("Server sending this to clients");
            
         } catch (IOException e) {
            e.printStackTrace();
            break;
         }
      }
   }
   
   public static void main(String [] args) {
      int port = Integer.parseInt(args[0]);
	  try {
         Thread t = new GreetingServer(port);
         t.start();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   class ClientHandler extends Thread {
    private ObjectInputStream inputStream;
    private List<ObjectOutputStream> clients;
	private String line;
    public ClientHandler(ObjectInputStream inputStream,
    List<ObjectOutputStream> clientList) {
      this.clients = clientList;
      this.inputStream = inputStream;
    }

    @Override
    // Start a new thread
    // Read from the client update the server and write back out
    public void run() {
      while (true) {
        try {
          line = (String) inputStream.readObject();
          System.out.println("handling clients");
        } catch (IOException e) {
			this.cleanUp();
			return;
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
			this.cleanUp();
			return;
        }
        this.writeOwnerToClients();
      }
    } //end run()

// Write out to every client
// if a client is done remove the client from the list and don't write to it
// anymore.
    private void writeOwnerToClients() {
      synchronized (clients) {
        ObjectOutputStream toRemove = null;
        for (ObjectOutputStream client : clients) {
          try {
            client.writeObject(line);
			//System.println()
          } catch (IOException e) {
            toRemove = client;
          }
        }
        clients.remove(toRemove);
      }
    } //end writeOwnerToClients

    // Close the input stream
    private void cleanUp() {
      try {
        this.inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } //end cleanUp()
  }
}