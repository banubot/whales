import java.util.*;
import java.net.*; 
import java.io.*;


public class SmthAboutWhales {
	public static void main(String[] args) {
		int thisPlayer = 1; //TODO get this from user 
		Whale[] whales = connectPlayers(thisPlayer);
	    Bomb bomb = createBomb(thisPlayer, whales);
		repl(thisPlayer, bomb, whales);
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
	public static Whale[] connectPlayers(int playerNum) {
		Scanner scanny = new Scanner(System.in);
		System.out.print("Are you player 1? "); //TODO get this as arg not just testing 
		//make a conection to 2, 3, 4 
		//then connect them to each other
		if (scanny.next().toLowerCase().charAt(0) == 'y') {
			System.out.println("Establishing server...");
			try {
				ServerSocket sv = new ServerSocket(1200);
				InetAddress inetAddr;
				Socket p2sock;
				int numPlayers = 1;
				while (numPlayers < 4) {
					System.out.println("Waiting for connection...");
					p2sock = sv.accept();
					System.out.println("Accepted a socket: "+p2sock);
					ObjectInputStream inputFromClient = new ObjectInputStream(p2sock.getInputStream());

					String line = "";
					try {
						line = (String) inputFromClient.readObject();
					} catch (Exception e) {
						System.err.println("Reading in object error");
					}
					System.out.println(line);
					numPlayers++;
				}
			} catch (IOException e) {
				System.out.println("Error: Could not establish server connection. "+e);
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
				ObjectOutputStream outputToServer = new ObjectOutputStream(sock.getOutputStream());
				outputToServer.writeObject("Hello world!");
				out.println("Hello, world!");
				while(true);
			} catch (Exception e) {
				System.out.println("Error: Host unknown.");
				System.exit(1);
			}
		}
		//TODO assign each player a num will be done 
		//by original server
		return new Whale[4]; //NO NO NOT REAL
	}


	public static void makeGui() {
		GUI gui = new GUI();
		(new Thread(gui)).start();
	}
}
