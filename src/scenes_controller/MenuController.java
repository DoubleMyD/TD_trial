package scenes_controller;

import application.Main;
import game.Game;
import helpz.Constants;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {
	private Stage stage;
	private Game game;
	private Main main;
	private Scene scene;
	
	private LevelChoiceController levelChoiceController;
	
	private @FXML Button playingButton, editingButton, settingsButton, quitButton;
	
	@FXML
	public void switchScene(ActionEvent event) {
		String buttonId = ((Button) event.getSource()).getId();
		
		
		if(buttonId.equals(playingButton.getId())) {
			scene = switchToLevelChoiceScene(Constants.ScenesName.PLAYING);
		}
		
		else if(buttonId.equals(editingButton.getId())) {
			
			scene = switchToLevelChoiceScene(Constants.ScenesName.EDITING);
		}
		
		else if(buttonId.equals(settingsButton.getId())) {
			
			scene = main.getSettingsScene();
		}
		
		else {
			closeApplication();
		}
		
		stage.hide();
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.show();
	}
	
	/**
	 * richiama la scena LevelChoice impostandola per la scelta tra i livelli in base all'editing e al playing
	 * @param ownerScene
	 */
	private Scene switchToLevelChoiceScene(int ownerScene) {
		levelChoiceController.setOwnerScene(ownerScene);
		if(ownerScene == Constants.ScenesName.EDITING)
			levelChoiceController.setNewLevelButtonVisible(true);
		else
			levelChoiceController.setNewLevelButtonVisible(false);
		return main.getLevelChoiceScene();
	}
	
	public void closeApplication() {
    	game.stopGameLoop();
    	Platform.exit();
    }
	
	public void setLevelChoiceController(LevelChoiceController levelChoiceController) {
    	this.levelChoiceController = levelChoiceController;
    }
	
	public void setGame(Game game) {
    	this.game = game;
    }
    
    public void setMain(Main main) {
    	this.main = main;
    }
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
	
	
	
	
}
