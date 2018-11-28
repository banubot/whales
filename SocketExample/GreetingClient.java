// File Name GreetingClient.java
import java.net.*;
import java.io.*;
import java.util.*;

public class GreetingClient {
	
	private Socket socket;
  private ObjectOutputStream outputStream;
  private ObjectInputStream inputStream;

   public static void main(String [] args) {
      GreetingClient gc = new GreetingClient(args);
   }

   public GreetingClient(String [] args) {
    String serverName = args[0];
    int port = Integer.parseInt(args[1]);
  
    System.out.print("Enter something : ");
    String input = System.console().readLine();
    try {
       System.out.println("Connecting to " + serverName + " on port " + port);
       Socket client = new Socket(serverName, port);
       
       System.out.println("Just connected to " + client.getRemoteSocketAddress());
   
   outputStream = new ObjectOutputStream(client.getOutputStream());
   outputStream.flush();
   inputStream = new ObjectInputStream(client.getInputStream());
   outputStream.writeObject(input);
   
   ServerListener serverListener = new ServerListener();
   serverListener.start();
  
       //client.close();
    } catch (IOException e) {
       e.printStackTrace();
    }
   }
   
   private class ServerListener extends Thread {
    public void run() {
      try {
        while (true) {
          String line  = (String) inputStream.readObject();
		  System.out.println(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }
  
}