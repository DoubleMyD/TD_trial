package scenes_controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.Main;
import game.Game;
import game_scenes_logic.CanvasManager;
import game_scenes_logic.Playing;
import helpz.Utilz;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import towers.Tower;

public class PlayingController {
	private Main main;
	private Stage stage;
	private Game game;
	private Playing playing;
	
	private @FXML AnchorPane mainAnchorPane, gameCanvasAnchorPane, guiAnchorPane, toolBar;//gli anchorPane per le vite e l'oro; la toolbar in basso
	
	private @FXML VBox menuVBox;
	private @FXML HBox towersButtonHBox;
	
	private @FXML ImageView selectedTowerImg;
	
	private @FXML ImageView heartImg, goldImg, waveImg;//iimmagini della vita, dell'oro e dell'ondata da affiancare alle scritte
	private @FXML Text livesText, goldText, waveText; //wave Text è l'indice dell'ondata corrente

	private @FXML Button pauseButton;
	
	/**
	 * towerImgs.get(towerType).get(towerTier).get(orientation) ; arrivato a questo punto ottieni l'array che contiene l'orientazione sinistra dell'animazione
	 */
	private List<Map<Integer, Map<Integer, List<WritableImage>>>> towersImgs = new ArrayList<>();
	private List<Button> towersButton = new ArrayList<>();
	
	public void initPlayingScene() {
		this.playing = game.getPlaying();
		
		initTowersImgs();
		initTowersButton();
				
		towersButtonHBox.getChildren().addAll(towersButton);
	}
	
	public void updatePlayingController() {
		goldText.setText(String.valueOf(playing.getGold()));
		waveText.setText(String.valueOf(playing.getWaveManager().getWaveIndex()) + " / " + String.valueOf(playing.getWaveManager().getWaves().size()));
	}
	
	@FXML
	public void handlerMouseEvent(MouseEvent event) {
			gameCanvasAnchorPane.setOnMouseClicked(e -> placeOrSelectTower(e));
			gameCanvasAnchorPane.setOnMouseMoved(e -> updateSelectedTowerImg(e));
	}
	
	
	@FXML
	public void togglePause() {
		playing.setGamePaused(!playing.isGamePaused());
		String text;
		
		if(playing.isGamePaused())
			text = "UNPAUSE";
		else
			text = "PAUSE";
		
		pauseButton.setText(text);
	}
	
	@FXML
	public void restartLevel() {
		playing.resetEverything();
	}
	
	@FXML
	public void switchToMenuScene() {
		main.switchToMenuScene();
		removeGameCanvas();
	}
	
	@FXML
	public void saveLevel() {
		
	}
	
	/**
	 * aggiunge in sovraImpressione la scena di GameOver
	 */
	public void addGameOverStage() {
		main.addGameOverStage();
	}
	
	private void placeOrSelectTower(MouseEvent event) {
		if(playing.getSelectedTower() == null) {
			playing.selectTower();
		}
		else {
			playing.placeTower();
			selectedTowerImg.setVisible(false);
		}
	}
	
	private void updateSelectedTowerImg(MouseEvent event) {
		if (playing.getSelectedTower() == null)
			selectedTowerImg.setVisible(false);
		else {
			selectedTowerImg.toFront();
			//selectedTowerImg.setLayoutX(game.getGameScene().getMouseX());
			//selectedTowerImg.setLayoutY(game.getGameScene().getMouseY());
	
			selectedTowerImg.setLayoutX(game.getPlaying().getTryX());
			selectedTowerImg.setLayoutY(game.getPlaying().getTryY());
	
			selectedTowerImg.setVisible(true);
		}
	}
	/**
	 * se l'oro è sufficiente, passa a playing una nuova torre corrispondente al tier 1 del tipo di selezionata
	 * @param event
	 */
	private void setSelectedTower(Event event) {
		int buttonId = (int) ((Button) event.getSource()).getUserData();
		
		if(!playing.isGoldEnoughForTower(buttonId))
			return;
		
		playing.setSelectedTower( new Tower(towersImgs.get(buttonId).get(0), 0, 0, -1,  buttonId));
		selectedTowerImg.setImage(towersImgs.get(buttonId).get(0).get(0).get(0) );
	}
	
	private void initTowersButton() {
		for(int i=0; i<towersImgs.size(); i++) {
			Button b = Utilz.createButton(null, null, i, 64, 64, event -> setSelectedTower(event));
			Utilz.setButtonImg(b,towersImgs.get(i).get(0).get(0).get(0));
			towersButton.add(b);
		}
	}
	
	private void initTowersImgs() {
		List<Map<Integer, Map<Integer, List<WritableImage>>>> imgs = playing.getTowerManager().getTowersImgs();
		
		for(int i=0; i<imgs.size(); i++) {
			towersImgs.add(imgs.get(i));
		}
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
	
	public void addGameCanvas() {
		gameCanvasAnchorPane.getChildren().add(CanvasManager.getInstance().getGameCanvas());
	}
	
	public void removeGameCanvas() {
		gameCanvasAnchorPane.getChildren().remove(CanvasManager.getInstance().getGameCanvas());
	}
	
	
	
	
}
