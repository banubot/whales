/* This is where the art happens <3
 */

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import java.io.*;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ChoiceBox;

public class GUI extends Application implements Runnable {
	//I made this global so the whales/bomb can be 
	//added to it later
	static AnchorPane pane = new AnchorPane();

	public void run() {
		launch();
	}

	public void start(Stage stage) {
		stage.setTitle("Something about whales...");
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
		Text status = new Text("Player 3 held the bomb for 3 seconds!");
		status.setFont(font);
		status.setLayoutX(5);
		status.setLayoutY(495);

		//TODO set event handlers 
		//button
		Button go = new Button("go!");
		go.setFont(font);
		go.setStyle("-fx-background-color: #FFFFFF;");
		go.setLayoutX(400);
		go.setLayoutY(550);
		
		//hold slider
		Text slideTxt = new Text("hold");
		Slider slide = new Slider(1, 10, 1);
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
		ChoiceBox<String> throwTo = new ChoiceBox<>();
		throwTo.setStyle("-fx-background-color: #FFFFFF;");
		throwTo.getItems().addAll("Player 1", "Player 3", "Player 4");
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
		stage.show();
	}
}
