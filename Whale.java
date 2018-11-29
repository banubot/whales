/* A class to represent a player
 */
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.*;
import java.net.*; 
import java.io.*;

public class Whale {
	int playerNum; //0 - 3 
	boolean alive; 
	boolean yourTurn;
	ImageView displayImg; //maybe this will also have stuff 
    ObjectInputStream recieve;
	ObjectOutputStream send;
	int x;
	int y;

	static Image happy = new Image("happy.png");
	static Image sad = new Image("sad.png");
	static Image dead = new Image("dead.png");
    static final int canvasHeight = 400;
	static final int canvasWidth = 400;

	public Whale(int playerNum, ObjectInputStream in, ObjectOutputStream out) {
		this.playerNum = playerNum;
		this.alive = true;
		this.yourTurn = (playerNum == 1);
		this.displayImg = new ImageView();
		this.displayImg.setImage(happy);
			
		//you will not have a socket to yourself
		this.recieve = in;
		this.send = out;

		//position this whale on canvas
		switch (playerNum) {
			case 0:
				this.x = 10;
				this.y = canvasHeight / 2 + 10;
				break;
			case 1:
			    this.x = canvasWidth / 2;	
				this.y = 20;
				break;
			case 2:
				this.x = canvasWidth - 10;
				this.y = canvasHeight / 2 + 10;
				break;
			case 3:
				this.x = canvasWidth / 2;
				this.y = canvasHeight - 40;

			this.displayImg.setLayoutX(this.x);
			this.displayImg.setLayoutY(this.y);
		} 
	}

	public void kill() {
		alive = false;
		displayImg.setImage(dead);
	} 
} 
