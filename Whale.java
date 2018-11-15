/* A class to represent a player
 */
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Whale {
	int playerNum; //0 - 3 
	boolean alive; 
	boolean yourTurn;
	ImageView displayImg; //maybe this will also have stuff 
						 //for what client they are

	static Image happy = new Image("happy.png");
	static Image sad = new Image("sad.png");
	static Image dead = new Image("dead.png");
    static final int canvasHeight = 400;
	static final int canvasWidth = 400;

	public Whale(int playerNum) {
		this.playerNum = playerNum;
		this.alive = true;
		this.yourTurn = false;
		this.displayImg = new ImageView();
		this.displayImg.setImage(happy);
		
		switch (playerNum) {
			case 0:
				this.displayImg.setLayoutX(10); //idk just guessing for now
				this.displayImg.setLayoutY(canvasHeight / 2 + 10); 
				break;
			case 1: 
				this.displayImg.setLayoutX(canvasWidth / 2);
				this.displayImg.setLayoutY(20);
				break;
			case 2:
				this.displayImg.setLayoutX(canvasWidth - 10);
				this.displayImg.setLayoutY(canvasHeight / 2 + 10); 
				break;
			case 3:
				this.displayImg.setLayoutX(canvasWidth / 2);
				this.displayImg.setLayoutY(canvasHeight - 40);
		} 
	}

	public void kill() {
		alive = false;
		displayImg.setImage(dead);
	} 
} 
