import java.util.*;
import java.net.*; 
import java.io.*;


public class SmthAboutWhales {
	public static void main(String[] args) {
		connectPlayers(); 
	}


	//TODO
	public static void connectPlayers() {
		Scanner scanny = new Scanner(System.in);
		System.out.print("Are you player 1? ");
		//make a conection to 2, 3, 4 
		//then connect them to each other
		if (scanny.next().toLowerCase().charAt(0) == 'y') {
			System.out.println("Establishing server...");
			try {
				ServerSocket sv = new ServerSocket(1200);
				InetAddress inetAddr;
				Socket p2sock, p3sock, p4sock;
				while (true) {
					System.out.println("Waiting for connection...");
					p2sock = sv.accept();
					Scanner in2 = new Scanner(p2sock.getInputStream());
					System.out.println(in2.nextLine());
				}
			} catch (IOException e) {
				System.out.println("Error: Could not establish server connection.");
				System.exit(1);
			}
				
		//make a listen socket
		} else {
			System.out.println("Host IP address: ");
			String host = scanny.next();
			System.out.println("Port: ");
			int port = scanny.nextInt();
			try {
				Socket sock = new Socket(host, port);
				PrintWriter out = new PrintWriter(sock.getOutputStream());
				out.println("Hello, world!");
			} catch (Exception e) {
				System.out.println("Error: Host unknown.");
				System.exit(1);
			}
		}
	}


	public static void makeGui() {
		GUI gui = new GUI();
		(new Thread(gui)).start();
		//Give time for GUI to be initialized before whales spawn on it
		try {
			do {
			Thread.sleep(1000);
			Thread.yield();
		} 
		while (gui.pane == null) ; 
		}	catch (InterruptedException e) {}

		for (int i = 0; i < 4; i++) {
			Whale player = new Whale(i);
			gui.pane.getChildren().add(player.displayImg);
		}
		Bomb bomb = new Bomb();
		gui.pane.getChildren().add(bomb.displayImg);
		bomb.move(80, 200); //just whatever
	}
}
