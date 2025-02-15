package scenes_controller;

import application.Main;
import game.Game;
import game.GameStates;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class GameOverController {
	private Stage stage;
	private Scene scene;
	private Main main;
	
	private Game game;
	
	private @FXML AnchorPane mainAnchorPane;
	
	public void initGameOverScene() {
		mainAnchorPane.setBackground(Background.fill(Paint.valueOf("rgba(255,0,0,0.5)")));
	}
	
	@FXML
	private void restartLevel() {
		GameStates.SetGameState(GameStates.PLAYING);
		game.getPlaying().resetEverything();
		main.getGameOverStage().close();
	}
	@FXML
	private void switchToMenuScene() {
		main.getGameOverStage().close();
		main.switchToMenuScene();
	}
	
	public void setMain(Main main) {
		this.main = main;
	}


	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setGame(Game game) {
		this.game = game;
	}
}
