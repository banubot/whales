/* A class to represent the bomb which every player
 * will have a copy of and must update according to the
 * holds and position the recieve from other clients
 */
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bomb {
	int explodeCounter; 
	ImageView displayImg; 

	static Image bomb = new Image("bomb.png");
	static Image explode = new Image("explode.png");
	static final int MAX = 50; //max units til explode

	public Bomb() {
		this.displayImg = new ImageView();
		this.displayImg.setImage(bomb);
	}
	
	public void hold(int holdTime) {
		explodeCounter -= holdTime;
	}	

	public boolean isExploded() {
		return explodeCounter <= 0;
	}

	public void move(int x, int y) {
		displayImg.setX(x);
		displayImg.setY(y);
	}

	public void explode() {
		displayImg.setImage(explode);
	}
	
	public void reset() {
		displayImg.setImage(bomb);
	}
} 
