import java.util.*;

public class SmthAboutWhales {
	public static void main(String[] args) {
		GUI gui = new GUI();
		(new Thread(gui)).start();
		//Give time for GUI to be initialized before whales spawn on it
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}

		for (int i = 0; i < 4; i++) {
			Whale player = new Whale(i);
			GUI.pane.getChildren().add(player.displayImg);
		}
		Bomb bomb = new Bomb();
		GUI.pane.getChildren().add(bomb.displayImg);
		bomb.move(80, 200); //just whatever
	}
}
