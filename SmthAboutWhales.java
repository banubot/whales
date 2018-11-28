import java.util.*;
import java.net.*; 
import java.io.*;


public class SmthAboutWhales {
	public static void main(String[] args) {
		Scanner scanny = new Scanner(System.in);
		System.out.print("Welcome to the ocean! What player are you? ");
		try {
			Whale[] whales = connectPlayers(scanny.nextInt());
		} catch (Exception e) {
			System.err.println("Error: Could not connect players. " + e);
		}
		while(true);
	    //Bomb bomb = createBomb(thisPlayer, whales);
		//repl(thisPlayer, bomb, whales);
	}


	/* what to do each turn
	 */
	public static void repl(int thisPlayer, Bomb bomb, Whale[] whales) {
		Scanner scanny = new Scanner(System.in);
		boolean playing = true;
		Whale you = null;
		for (Whale whale : whales) {
			if (whale.playerNum == thisPlayer) {
				you = whale;
			}
		}
		while (playing) {
			int holdTime = 0; //NO NOT ZERO
			int nextPlayer = 0;
			if (you.yourTurn) {	
				//TODO GET THE SLIDEY FROM THE GUI BUT HOW?
				//also need to remover player from slidey when 
				//their turn is over
				bomb.hold(holdTime);
				if (bomb.isExploded()) {
					bomb.explode();
					you.kill();
				}
				for (Whale whale : whales) {
					if (whale != you) {
						try {
							whale.send.writeObject(holdTime);
							whale.send.writeObject(nextPlayer);
						} catch (IOException e) {
							System.err.println(e);
						}
					}
				}
			} else {
				for (Whale whale : whales) {
					if (whale.yourTurn) {
						try {
							holdTime = (int) whale.recieve.readObject();
							nextPlayer = (int) whale.recieve.readObject();
						} catch (Exception e) {
							System.err.println(e);
						}
						bomb.hold(holdTime);
						if (bomb.isExploded()) {
							bomb.explode();
							whale.kill();
						}
					}
				}
			}
			setTurns(whales, nextPlayer);
			playing = stillPlaying(whales);
		}
	}


	/* set whos turn it is for the next pass
	 */
	public static void setTurns(Whale[] whales, int nextPlayer) {
		for (Whale whale : whales) {
			if (whale.playerNum == nextPlayer) {
				whale.yourTurn = true;
			} else {
				whale.yourTurn = false;
			}
		}
	}


	/* check if all are dead but 1
	 */
	public static boolean stillPlaying(Whale[] whales) {
		int numDead = 0;
		for (Whale whale : whales) {
			if (!whale.alive) {
				numDead++;
			}
		}
		return (numDead == 3);
	}


	/* Whoever is the original server needs to make the bomb 
	 * and send its info to the other players otherwise its not
	 * going to be the same for each player
	 */
	public static Bomb createBomb(int thisPlayer, Whale[] players) {
		Bomb bomb = new Bomb();
		if (thisPlayer == 1) {
			bomb.explodeCounter = (int) Math.random() * Bomb.MAX;
			for (Whale player : players) {
				if (player.playerNum != thisPlayer) {
					try {
						player.send.writeObject(bomb.explodeCounter);
					} catch (Exception e) {
						System.err.println(e);
					}
				}
			}
		} else {
			for (Whale player : players) {
				if (player.playerNum == 1) {
					try {
						bomb.explodeCounter = (int) player.recieve.readObject(); 
					} catch (Exception e) {
						System.err.println(e);
					}
				}
			}
		}
		return bomb;
	} 


	/* TODO return a set of four whales with sockets between all
	 * of them and player numbers assigned
	 */ 
	public static Whale[] connectPlayers(int playerNum) throws Exception {
		Whale[] whales = new Whale[4];
		Scanner scanny = new Scanner(System.in);	
	    InetAddress addressP2 = null; //for connecting 2 - 4 to eachother
		InetAddress addressP3 = null;
		int numPlayers;
		int clientPlayerNum;
		Socket sock;
		//connect everyone to player 1
		if (playerNum == 1) {
			System.out.println("Establishing server...");
			ServerSocket sv = new ServerSocket(1200);
			InetAddress inetAddr = sv.getInetAddress();
			System.out.println("Server established at address " 
					+ inetAddr + " port " + sv.getLocalPort());
			whales[0] = new Whale(1, null, null);
			numPlayers = 1;
			while (numPlayers < 4) {
				System.out.println("Waiting for connection...");
				sock = sv.accept();
				System.out.println("Accepted a socket: "+ sock);
				ObjectInputStream inputFromClient = new ObjectInputStream(sock.getInputStream());
				clientPlayerNum = 0;
				clientPlayerNum = (int) inputFromClient.readObject();
				System.out.println("Connected to player " + clientPlayerNum);
				whales[clientPlayerNum - 1] = 
					new Whale(clientPlayerNum, inputFromClient, 
							new ObjectOutputStream(sock.getOutputStream()));
				if (clientPlayerNum == 2) {
					addressP2 = sock.getInetAddress(); //TODO check might be getLocalAddress??
				}
				if (clientPlayerNum == 3) {
					addressP3 = sock.getInetAddress();
				}
				numPlayers++;
			}
				
		//clients connect to p1 : players 2 - 4
		} else {
			whales[playerNum - 1] = new Whale(playerNum, null, null); //you
			System.out.print("Player 1 IP address: ");
			String host = scanny.next();
			System.out.print("Port: ");
			int port = scanny.nextInt();
			sock = new Socket(host, port);
			ObjectOutputStream outputToServer = new ObjectOutputStream(sock.getOutputStream());
			System.out.println("Connected to player 1");
			outputToServer.writeObject(playerNum);
			whales[0] = 
				new Whale(1, new ObjectInputStream(sock.getInputStream()), 
					outputToServer);
			//	while(true);
		}
		
		int portP2, portP3;
		//connect 2 - 4 to eachother
		if (playerNum == 1) {
			portP2 = (int) whales[1].recieve.readObject(); //port srv p2
			System.out.println("Player 2 connecting to 3 & 4 at port " + portP2);
			portP3 = (int) whales[2].recieve.readObject(); //port srv p3
			System.out.println("Player 3 connecting to 4 at port " + portP3);
			whales[2].send.writeObject(addressP2); //addr p2 -> p3
			whales[3].send.writeObject(addressP2); //addr p2 -> p4
			whales[2].send.writeObject(portP2); //port p2 -> p3
			whales[3].send.writeObject(portP2); //port p2 -> p4
			whales[3].send.writeObject(addressP3); //addr p3 -> p4
			whales[3].send.writeObject(portP3); //port p3 -> p4
		} 
		//server to 3 and 4
		if (playerNum == 2) {
			System.out.println("Establishing server for Player 3 & 4...");
			ServerSocket srvP2 = new ServerSocket(1201);
			whales[0].send.writeObject(srvP2.getLocalPort());
			numPlayers = 0;
			while (numPlayers < 2) {
				System.out.println("Waiting for connection...");
				sock = srvP2.accept();
				System.out.println("Accepted a socket: "+ sock);
				ObjectInputStream inputFromClient = new ObjectInputStream(sock.getInputStream());
				clientPlayerNum = (int) inputFromClient.readObject();
				System.out.println("Connected to player " + clientPlayerNum);
				whales[clientPlayerNum - 1] = 
					new Whale(clientPlayerNum, inputFromClient,
							new ObjectOutputStream(sock.getOutputStream()));
				numPlayers++;
			}	
		}
		//client to 2, server to 3
		if (playerNum == 3) {
			System.out.println("Establishing server for player 4...");
			ServerSocket srvP3 = new ServerSocket(1202);
			whales[0].send.writeObject(srvP3.getLocalPort());
			//connect to 2
			InetAddress host = 
				(InetAddress) whales[0].recieve.readObject();
			int port = (int) whales[0].recieve.readObject();
			sock = new Socket(host, port);
			ObjectOutputStream outputToServer = new ObjectOutputStream(sock.getOutputStream());
			System.out.println("Connected to player 2");
			outputToServer.writeObject(playerNum);
			whales[1] = 
				new Whale(2, new ObjectInputStream(sock.getInputStream()),
						outputToServer);
			//connect to 3
			System.out.println("Waiting for connection...");
			sock = srvP3.accept();
			System.out.println("Accepted a socket: "+ sock);
			ObjectInputStream inputFromClient = new ObjectInputStream(sock.getInputStream());
			clientPlayerNum = (int) inputFromClient.readObject();
			System.out.println("Connected to player " + clientPlayerNum);
			whales[clientPlayerNum - 1] = 
					new Whale(clientPlayerNum, inputFromClient,
							new ObjectOutputStream(sock.getOutputStream()));
			}
		//client to 2 and 3
		if (playerNum == 4) {
			//connect to 2
			InetAddress host = 
				(InetAddress) whales[0].recieve.readObject();
			int port = (int) whales[0].recieve.readObject();
			sock = new Socket(host, port);
			ObjectOutputStream outputToServer = new ObjectOutputStream(sock.getOutputStream());
			System.out.println("Connected to player 2");
			outputToServer.writeObject(playerNum);
			whales[1] = 
				new Whale(2, new ObjectInputStream(sock.getInputStream()),
						outputToServer);
			//connect to 3
			host = (InetAddress) whales[0].recieve.readObject();
			port = (int) whales[0].recieve.readObject();
			sock = new Socket(host, port);
			outputToServer = new ObjectOutputStream(sock.getOutputStream());
			System.out.println("Connected to player 3");
			outputToServer.writeObject(playerNum);
			whales[2] = 
				new Whale(3, new ObjectInputStream(sock.getInputStream()),
						outputToServer);
		}
		return whales; 
	}


	public static void makeGui() {
		GUI gui = new GUI();
		(new Thread(gui)).start();
	}
}
