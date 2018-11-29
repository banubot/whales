import java.util.*;
import java.net.*; 
import java.io.*;
import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.lang.Thread.*;
import javafx.event.*;

public class SmthAboutWhales extends Application implements Runnable {
	static Whale[] whales;
	static Bomb bomb;
	static Boolean monitor = false;
	static Text status;
	static Button go;
	static Slider slide;
	static ChoiceBox<String> throwTo;
	
	public static void main(String[] args) {
		Scanner scanny = new Scanner(System.in);
		System.out.print("Welcome to the ocean! What player are you? ");
		int thisPlayer;
		Thread gui = new Thread(new SmthAboutWhales());
		gui.start();
		try {
			thisPlayer = scanny.nextInt();
			whales = connectPlayers(thisPlayer);
			bomb = createBomb(thisPlayer, whales);
			
			synchronized (monitor) {
				monitor.notify(); 
			}
			repl(thisPlayer, bomb, whales);
			gameOver(whales);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void gameOver(Whale[] whales) {
		int winner = 0;
		for (Whale whale : whales) {
			if (whale.alive) {
				winner = whale.playerNum;
			}
		}
		status.setText("Game over! Player " + winner + " wins!!!");
	}


	public void run() {
		launch();
	}
	

	public void start(Stage stage) {
		stage.setTitle("Something about whales...");
		AnchorPane pane = new AnchorPane();
	  	stage.setScene(new Scene(pane));
		
		//background
		Image ocean = new Image("file:bkg.gif");		  
		ImageView bkg = new ImageView();
	    bkg.setImage(ocean);	
		
		//control box
		Rectangle rect = new Rectangle(500,150);
		rect.setLayoutX(0);
		rect.setLayoutY(466); 
		rect.setFill(Color.rgb(0, 255, 252, 0.6));
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetY(-1.0);
		dropShadow.setColor(Color.rgb(85,87,83,0.2));
		rect.setEffect(dropShadow);
		
		//font
		Font font = null;
		try {
			font = Font.loadFont(new FileInputStream(new File("font.ttf")), 18);
		} catch (FileNotFoundException e) {
			System.out.println("Error: Could not load font.");
		}
		
		//action status - can be changed to things like 
		//your turn
		//player _ exploded
		//player _ threw the bomb to player _
		status = new Text("Player 3 held the bomb for 3 seconds!");
		status.setFont(font);
		status.setLayoutX(5);
		status.setLayoutY(495);

		//TODO set event handlers 
		//button
		go = new Button("go!");
		go.setFont(font);
		go.setStyle("-fx-background-color: #FFFFFF;");
		go.setLayoutX(400);
		go.setLayoutY(550);
		//set behavior of button when clicked
		go.setOnAction(new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent e) {
				synchronized(monitor) {
					monitor.notify();
				}
			}
		});
		
		//hold slider
		Text slideTxt = new Text("hold");
	    slide = new Slider(1, 10, 1);
		slideTxt.setLayoutX(40);
		slideTxt.setLayoutY(532);
		slide.setLayoutX(35);
		slide.setLayoutY(550);
		slide.setShowTickMarks(true);
		slide.setShowTickLabels(true);
		slide.setMajorTickUnit(3);
		slide.setSnapToTicks(true);
		
		//choose victim
		Text throwTxt = new Text("throw to");
		throwTo = new ChoiceBox<>();
		throwTo.setStyle("-fx-background-color: #FFFFFF;");
		throwTo.getItems().addAll("Player 1", "Player 2",
			   	"Player 3", "Player 4");
		throwTxt.setLayoutX(255);
		throwTxt.setLayoutY(532);
		throwTo.setLayoutX(250);
		throwTo.setLayoutY(550);
		
		//top banner
		Text title = new Text("Something about whales!");
		title.setLayoutX(100);
		title.setLayoutY(20);
		title.setFont(font);
		
		pane.getChildren().addAll(bkg, rect, status, go, slideTxt, slide,
				throwTxt, throwTo, title);
		
		try {
			synchronized (monitor) {
				monitor.wait();
			}
		} catch (Exception e)  {System.out.println("exception while waiting" +e);} //Will throw interrupted upon notify

		 pane.getChildren().add(bomb.displayImg);
		 for (Whale whale : whales) {
			 pane.getChildren().add(whale.displayImg);
		 }

		stage.show();
	}	


	/* what to do each turn
	 */
	public static void repl(int thisPlayer, Bomb bomb, Whale[] whales) 
		throws Exception {
		status.setText("Ready to play!");
		Thread.sleep(4000); //just so you have a chance to actually read it
		Scanner scanny = new Scanner(System.in);
		boolean playing = true;
		int holdTime = 0; 
		int nextPlayer = 0;
		Whale you = null;
		for (Whale whale : whales) {
			if (whale.playerNum == thisPlayer) {
				you = whale;
			}
		}

		while (playing) {
			if (you.yourTurn) {
				bomb.move(you.x, you.y);
				status.setText("It's your turn...");
				synchronized(monitor) {
					monitor.wait();
				}

				holdTime = (int) slide.getValue();
				nextPlayer = Integer.valueOf(throwTo.getValue().charAt(7)); 
				//TODO GET THE SLIDEY FROM THE GUI BUT HOW?
				//also need to remover player from slidey when 
				//their turn is over
				bomb.hold(holdTime);
				if (bomb.isExploded()) {
					bomb.explode();
					you.kill();
					status.setText("You are dead :(");
					Thread.sleep(4000);
					bomb = createBomb(you.playerNum, whales);
				}
			    status.setText("Throwing the bomb to Player " + nextPlayer);
				for (Whale whale : whales) {
					if (whale != you) {
						whale.send.writeObject(holdTime);
						whale.send.writeObject(nextPlayer);
					}
				}
				Thread.sleep(4000);
			} else {
				for (Whale whale : whales) {
					if (whale.yourTurn) {
						bomb.move(whale.x, whale.y);
						status.setText("Player " + whale.playerNum +
								"'s turn...");
						Thread.sleep(4000);
						holdTime = (int) whale.recieve.readObject();
						nextPlayer = (int) whale.recieve.readObject();
						
						bomb.hold(holdTime);
						status.setText("Player " + whale.playerNum + 
								" held the bomb for " + holdTime +
								" seconds!");
						Thread.sleep(4000);
						if (bomb.isExploded()) {
							bomb.explode();
							whale.kill();
							throwTo.getItems().remove("Player "+whale.playerNum);
							status.setText("Player " + whale.playerNum +
									" died :(");
							Thread.sleep(4000);
						}
						status.setText("Player " + whale.playerNum + 
								" threw the bomb to Player " +
								nextPlayer);
						Thread.sleep(4000);
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
			bomb.explodeCounter = (int) (Math.random() * Bomb.MAX);
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
