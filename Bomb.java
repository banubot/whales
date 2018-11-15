/* A class to represent the bomb which every player
 * will have a copy of and must update according to the
 * holds and position the recieve from other clients
 */
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.lang.Math;

public class Bomb {
	int explodeCounter; 
	boolean exploded; 
	ImageView displayImg; 

	static Image bomb = new Image("bomb.png");
	static Image explode = new Image("explode.png");
	static final int MAX = 100; //max units til explode

	public Bomb() {
		this.explodeCounter = (int) Math.random() * MAX;
		this.displayImg = new ImageView();
		this.displayImg.setImage(bomb);
		this.exploded = false; 
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
		exploded = true;
	} 
} 
