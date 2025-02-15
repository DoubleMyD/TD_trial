package scenes_controller;

import java.util.ArrayList;
import java.util.List;

import application.Main;
import game.Game;
import game.GameStates;
import game_scenes_logic.CanvasManager;
import helpz.Constants;
import helpz.LoadSave;
import helpz.Utilz;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import objects.Level;

public class LevelChoiceController {
	private Stage stage;
	private Game game;
	private Main main;
	private Scene scene;
	private EditingController editingController;
	private int ownerScene; //serve per capire se dobbiamo caricare il livello per il gioco o per l'editor
	
	private @FXML GridPane levelGridPane;
	private final int MAX_BUTTONS_PER_ROW = 5;
	private static int lvlCount = LoadSave.NumberOfLevel();
	
	private List<Button> lvlButtons = new ArrayList<>();
	private Button newLevelButton, defaultLevelButton;
	
	private @FXML Button menuButton;
	
	private int colIndex = 0, rowIndex = 0; //indici del gridPane
	
	public void initLevelChoiceScene(Main main, Stage stage, Game game) {
		this.main = main;
		this.stage = stage;
		this.game = game;
		this.editingController = main.getEditingLoader().getController();

		initGridPane();
	}
	
	public void createLevel() {
		lvlButtons.add(createButtonLvl(lvlCount+1));
		
		levelGridPane.add(lvlButtons.get(lvlButtons.size()-1), colIndex+1, rowIndex);
		colIndex++;
		if(colIndex >= MAX_BUTTONS_PER_ROW) {
			colIndex = 0;
			rowIndex++;
		}
		
		lvlCount++;
	}
	
	private void initGridPane() {
		newLevelButton = Utilz.createButton(null, "New Level", 0, 100, 100, event -> createLevel());
		levelGridPane.add(newLevelButton, 0, 0);
		colIndex++;
		
		defaultLevelButton = Utilz.createButton("deafultLevelButton", "Default Level", -1, 150, 150, event -> setLevel(event));
		levelGridPane.add(defaultLevelButton, colIndex, rowIndex);
		colIndex++;
				
		for(int i = 0; i < lvlCount; i++) {
			lvlButtons.add(createButtonLvl(i+1));
			
			levelGridPane.add(lvlButtons.get(i), colIndex, rowIndex);
			colIndex++;
			if(colIndex >= MAX_BUTTONS_PER_ROW) {
				colIndex = 0;
				rowIndex++;
			}
		}
	}
	
	/**
	 * si occupa di caricare il livello dal file e settare la scena desiderata con quel livello
	 * @param event
	 */
	private void setLevel(Event event) {
		Button button = (Button) event.getSource();
		int	levelNumber = Integer.parseInt(button.getUserData().toString());
		String	levelName = "level_" + levelNumber;
		
		if(levelNumber == -1)//id assegnato al defaultLevelButton
			levelName = new String("default_level");
		
		
		Level level = new Level(levelName, LoadSave.GetLevelData(levelName), LoadSave.GetLevelPathPoints(levelName), LoadSave.GetLevelWaves(levelName));
		Scene newScene;
		
		if(ownerScene == Constants.ScenesName.PLAYING) {
			game.getPlaying().setLevel(level);
			newScene = main.getPlayingScene();
			GameStates.SetGameState(GameStates.PLAYING);
			((PlayingController) main.getPlayingLoader().getController()).addGameCanvas();
		}
		else {
			game.getEditor().setLevel(level);
			newScene = main.getEditingScene();
			GameStates.SetGameState(GameStates.EDITING);
			editingController.addGameCanvas();
			editingController.initWaveGridPane();
		}
		
		stage.setScene(newScene);
		stage.setFullScreen(true);
		
		CanvasManager.getInstance().getGameCanvas().setVisible(true);
		
		stage.show();
	}
	
	@FXML
	private void switchToMenuScene() {
		main.switchToMenuScene();
	}
	
	public void setNewLevelButtonVisible(Boolean x) {
		newLevelButton.setVisible(x);
	}
	
	private Button createButtonLvl(int levelNumber) {
		Button button= new Button();
		button.setUserData(levelNumber);
		button.setText("Level " + levelNumber);
		button.setPrefSize(150, 150);
		button.setOnAction(event -> setLevel(event));
				
		return button;
	}
	
	/**
	 * @param ownerScene La scena a cui il livello scelto sarà passato (playing o editing)
	 */
	public void setOwnerScene(int ownerScene) {
		this.ownerScene = ownerScene;
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
