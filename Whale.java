/* A class to represent a player
 */
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.*;
import java.net.*; 
import java.io.*;
import javafx.scene.text.*;

public class Whale {
	int playerNum; //1 - 4 
	boolean alive; 
	boolean yourTurn;
	ImageView displayImg; 
    ObjectInputStream recieve;
	ObjectOutputStream send;
	int x;
	int y;
	Text name;

	static Image happy = new Image("happy.png");
	static Image dead = new Image("dead.png");
    static final int canvasHeight = 400;
	static final int canvasWidth = 400;

	public Whale(int playerNum, ObjectInputStream in, 
			ObjectOutputStream out, String name) {
		this.playerNum = playerNum;
		this.alive = true;
		this.yourTurn = (playerNum == 1);
		this.displayImg = new ImageView();
		this.displayImg.setImage(happy);
		this.name = new Text(name);
		//you will not have a socket to yourself
		this.recieve = in;
		this.send = out;

		//position this whale on canvas
		switch (playerNum) {
			case 1:
				this.x = 10;
				this.y = canvasHeight / 2 + 10;
				break;
			case 2:
			    this.x = canvasWidth / 2;	
				this.y = 20;
				break;
			case 3:
				this.x = canvasWidth - 10;
				this.y = canvasHeight / 2 + 10;
				break;
			case 4:
				this.x = canvasWidth / 2;
				this.y = canvasHeight - 50;
		} 
	}

	public void move() {
		this.displayImg.setLayoutX(this.x);
		this.displayImg.setLayoutY(this.y);
		this.name.setLayoutX(this.x);
		this.name.setLayoutY(this.y + 110);
	}

	public void kill() {
		alive = false;
		displayImg.setImage(dead);
		this.move();
	} 
} 
