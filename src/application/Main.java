package application;
	
import game.Game;
import game.GameStates;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import scenes_controller.EditingController;
import scenes_controller.GameOverController;
import scenes_controller.LevelChoiceController;
import scenes_controller.MenuController;
import scenes_controller.PlayingController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


public class Main extends Application {
	private static Canvas gameCanvas;
	private Stage stage, gameOverStage;
	//private StackPane gameOverPane;
	
	private FXMLLoader playingLoader, editingLoader, menuLoader, gameOverLoader, levelChoiceLoader;
	private Parent playingRoot, editingRoot, menuRoot, gameOverRoot, levelChoiceRoot;
	
	private Game game;
	private Scene menuScene, editingScene, settingsScene, playingScene, gameOverScene, levelChoiceScene;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		gameCanvas = new Canvas(1920, 1080-440);
		gameCanvas.setVisible(true);
		this.game = new Game(gameCanvas);
	
		this.stage = primaryStage;
		
		try {
			initSceneLoader();
			
			initMenuScene();
			initEditingScene();
			initPlayingScene();
			initGameOverScene();
			initLevelChoiceScene();
			
			game.getPlaying().setPlayingController(playingLoader.getController());
			
			// Aggiungi fogli di stile
			menuScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setScene(menuScene);
			primaryStage.setTitle("Applicazione JavaFX");
			primaryStage.setFullScreen(true);
			primaryStage.show();
			
			gameOverStage = new Stage();
			initGameOverStage();

			game.startGameLoop();
			// Imposta un'azione per gestire la chiusura dell'applicazione
			primaryStage.setOnCloseRequest(event -> {
				// Ferma il ciclo di gioco prima di chiudere l'applicazione
				game.stopGameLoop();
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initSceneLoader() throws Exception {
		menuLoader = new FXMLLoader(getClass().getResource("/scenes_controller/Menu.fxml"));
		menuRoot = menuLoader.load();
		
		editingLoader = new FXMLLoader(getClass().getResource("/scenes_controller/Editing.fxml"));
		editingRoot = editingLoader.load();
		
		playingLoader = new FXMLLoader(getClass().getResource("/scenes_controller/Playing.fxml"));
		playingRoot = playingLoader.load();
		
		gameOverLoader = new FXMLLoader(getClass().getResource("/scenes_controller/GameOver.fxml"));
		gameOverRoot = gameOverLoader.load();
		
		levelChoiceLoader = new FXMLLoader(getClass().getResource("/scenes_controller/LevelChoice.fxml"));
		levelChoiceRoot = levelChoiceLoader.load();
	}

	private void initMenuScene() throws Exception{
		// Ottieni il controller associato al file FXML principale
		MenuController menuController = menuLoader.getController();
		menuController.setMain(this);
		menuController.setGame(game);
		menuController.setStage(stage);
		
		menuController.setLevelChoiceController(levelChoiceLoader.getController());
		// Crea la scena principale
		menuScene = new Scene(menuRoot);
	}
	
	private void initEditingScene() throws Exception {
		EditingController editingController = editingLoader.getController();
		//editingController.setGameCanvas(gameCanvas);
		editingController.setGame(game);
		editingController.initEditingScene();
		editingController.setMain(this);
		editingController.setStage(stage);
		
		editingScene = new Scene(editingRoot);
	}
	
	private void initPlayingScene() throws Exception{
		PlayingController playingController = playingLoader.getController();
		//playingController.setGameCanvas(gameCanvas);
		playingController.setGame(game);
		//playingController.setGameScene(game.getGameScene());
		//playingController.setTileManager(game.getTileManager());
		
		playingController.setMain(this);
		playingController.setStage(stage);
		
		//playingController.setPlaying(game.getPlaying());
		playingController.initPlayingScene();
		
		playingScene = new Scene(playingRoot);
	}
	
	private void initGameOverScene() throws Exception {
		GameOverController gameOverController = gameOverLoader.getController();
		
		gameOverController.setGame(game);
		gameOverController.setMain(this);
		gameOverController.setStage(stage);
		gameOverController.initGameOverScene();
			
		gameOverScene = new Scene(gameOverRoot);
        gameOverScene.setFill(Color.TRANSPARENT); // Permette di vedere lo stage sottostante
	}
	
	private void initGameOverStage() throws Exception{
		gameOverStage.initStyle(StageStyle.TRANSPARENT);
		gameOverStage.setScene(gameOverScene);
		gameOverStage.initOwner(stage);
		gameOverStage.initModality(Modality.APPLICATION_MODAL);
	}
	
	private void initLevelChoiceScene() throws Exception {
		LevelChoiceController levelChoiceController = levelChoiceLoader.getController();
	
		levelChoiceController.initLevelChoiceScene(this, stage, game);

		levelChoiceScene = new Scene(levelChoiceRoot);
	}
	
	/**
	 * cambia la scena attuale con il menu
	 */
	public void switchToMenuScene() {
		stage.setScene(menuScene);
		//game.getGameScene().getGameCanvas().setVisible(false);
		
		GameStates.SetGameState(GameStates.MENU);
		stage.setFullScreen(true);
		stage.show();
	}
	
	// Metodo per impostare il canvas
    public static void setGameCanvas(Canvas canvas) {
        gameCanvas = canvas;
    }

    // Metodo per ottenere il canvas
    public static Canvas getGameCanvas() {
        return gameCanvas;
    }
    
    public void removeCanvasFromAnchorPane(AnchorPane anchorPane) {
        // Rimuovi il Canvas dall'AnchorPane
        anchorPane.getChildren().remove(gameCanvas);
    }
    
    public Scene getGameOverScene() {
    	return gameOverScene;
    }
    
    public Scene getMenuScene() {
    	return menuScene;
    }
    
    public Scene getEditingScene() {
    	return editingScene;
    }
    
    public Scene getSettingsScene() {
    	return settingsScene;
    }
    
    public Scene getPlayingScene() {
    	return playingScene;
    }
    
    public Scene getLevelChoiceScene() {
    	return levelChoiceScene;
    }

	public FXMLLoader getPlayingLoader() {
		return playingLoader;
	}

	public FXMLLoader getEditingLoader() {
		return editingLoader;
	}

	public FXMLLoader getMenuLoader() {
		return menuLoader;
	}
	
	public void gameCanvasVisible(Boolean x) {
		gameCanvas.setVisible(x);
	}
	
	public void addGameOverStage() {
		gameOverStage.show();
	}
	
	public void removeGameOverStage() {
		gameOverStage.hide();
	}

	public Stage getGameOverStage() {
		return gameOverStage;
	}
    
    
}
