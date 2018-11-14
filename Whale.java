/* A class to represent a player
 */
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Whale {
	int playerNum; //0 - 3 
	boolean alive; 
	ImageView displayImg; //maybe this will also have stuff 
						 //for what client they are

	static Image happy = new Image("happy.png");
	static Image sad = new Image("sad.png");
	static Image dead = new Image("dead.png");
    static final int canvasHeight = 700;
	static final int canvasWidth = 700;

	public Whale(int playerNum) {
		this.playerNum = playerNum;
		this.alive = true;
		this.displayImg = new ImageView();
		this.displayImg.setImage(happy);
		
		switch (playerNum) {
			case 0:
				this.displayImg.setX(10); //idk just guessing for now
				this.displayImg.setY(canvasHeight / 2); 
				break;
			case 1: 
				this.displayImg.setX(canvasWidth / 2);
				this.displayImg.setY(10);
				break;
			case 2:
				this.displayImg.setX(canvasWidth - 10);
				this.displayImg.setY(canvasHeight / 2); 
				break;
			case 3:
				this.displayImg.setX(canvasWidth / 2);
				this.displayImg.setY(canvasHeight - 10);
		} 
	}

	public void kill() {
		alive = false;
		displayImg.setImage(dead);
	} 
} 
