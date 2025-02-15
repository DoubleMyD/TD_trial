package scenes_controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import application.Main;
import game.Game;
import game_scenes_logic.CanvasManager;
import game_scenes_logic.Editing;
import helpz.Constants;
import helpz.Utilz;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import objects.Tile;

public class EditingController implements Initializable {
	private Main main;
	private Stage stage;
	private Scene scene;
	
	private Game game;
	private Editing editing;
	
	private @FXML Canvas gameCanvas;
	private @FXML AnchorPane mainAnchorPane, gameCanvasAnchorPane, wavePane;//anchorPane principale della scena; anchorPane per mostrare le ondate
	private @FXML HBox tilesButtonHBox, enemiesButtonHBox;//HBox per i bottoni delle Tile; HBox per i bottoni per i nemici
	private @FXML GridPane wavesGridPane;//tabella per mostrare per ogni ondata il numero di nemici
	
	private Map<Integer, List<Tile>> tiles = new HashMap<>(); //una mappa che per ogni tipo di tile fa corrispondere la lista con la corrsipondente immagine ruotata
	
	private Map<Integer, WritableImage> tilesImgs = new HashMap<>(); //per ogni tipo di tile è associata una immagine
	private Map<Integer, WritableImage> enemiesImgs = new HashMap<>(); //per ogni tipo di nemico è associata una immagine
	private List<WritableImage> pathPointImgs = new ArrayList<>();
	
	private List<Button> tilesButton = new ArrayList<>();
	private List<Button> enemiesButton = new ArrayList<>();
	private List<Button> pathPointButton = new ArrayList<>();
	private List<Button> wavesButton = new ArrayList<>();
	private Button addWaveButton;
	
	private int currentRotationIndex, selectedButtonId; //the index for rotate the sprite, the id of the selectedButton
	private int currentWaveIndex; //l'indice della ondata selezionata (dove si vogliono aggiungere i nemici)
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Insets padding = new Insets(10.0); // Use appropriate values for top, right, bottom, and left padding
		wavePane.setPadding(padding);
		
		this.wavePane.setBackground(Background.fill(Paint.valueOf("rgba(255,0,0,0.3)")));

	}
	
	public void initEditingScene() {
		this.editing = game.getEditor();
		
		this.initTilesMap();
		
		this.initMapTilesImgs();
		this.initMapEnemiesImgs();
		this.initPathPointImgs();
		
		this.initTilesButton();
		this.initEnemiesButton();
		this.initPathPointButton();
		
		tilesButtonHBox.getChildren().addAll(tilesButton);
		tilesButtonHBox.getChildren().addAll(pathPointButton);
		
		enemiesButtonHBox.getChildren().addAll(enemiesButton);
		
		//this.initWaveGridPane();
	}
	
	@FXML
	public void handleKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.R) {
            if(editing.getSelectedTile() != null)
            	rotateSprite();
        }
	}
	
	@FXML
	public void setGameCanvasAnchorPaneMouseEventHandlers() {
	    	gameCanvasAnchorPane.setOnMouseClicked(event -> changeTile(event.getX(), event.getY()));
	    	gameCanvasAnchorPane.setOnMouseDragged(event -> changeTile(event.getX(), event.getY()));	
	}
	
	@FXML
	public void saveWave() {
		editing.saveWave(currentWaveIndex, editing.getLevelWaves().get(currentWaveIndex).getEnemyList());
	}
	
	@FXML
	public void saveOrCreateLevel() {
		TextInputDialog dialog = Utilz.createTextInputDialog(null, "Number of the Level: ", stage);
		
		dialog.showAndWait().ifPresent(result -> {
			String levelNumber = result.trim(); // Ottieni il testo inserito, rimuovendo gli spazi iniziali e finali

			if (!levelNumber.isEmpty()) {
				File newLevel = new File("res/level_" + levelNumber + ".txt");

				if(newLevel.exists()) {
					editing.saveLevel("level_" + levelNumber);
				} else {
					editing.createLevel("level_" + levelNumber);
				}
			} else {
				// Gestione dell'input vuoto o annullato
				// Qui puoi mostrare un messaggio di errore o gestire il caso in modo diverso
			}
		});
	}
	
	@FXML
	public void switchToMenuScene() {
		main.switchToMenuScene();
		//this.mainAnchorPane.getChildren().remove(game.getGameScene().getGameCanvas());
	}
	
	/**
	 * passa alla classe editing l'id della tile selezionata (l'id corrisponde non al tipo ma proprio all'immagine, quindi ruotata o meno)
	 * @param event
	 */
	private void setSelectedTile(Event event) {
		if(wavePane.isVisible())
    		Utilz.fadeTransition(wavePane);
		
		Button button = ((Button) event.getSource());
		selectedButtonId = (int) button.getUserData();
		
		currentRotationIndex=0;
		
		//8 perchè i PathPoint iniziano da lì, e noi vogliamo controllare che siano tile adesso
		if(selectedButtonId < tilesButton.size()) {
			editing.setSelectedTile(tiles.get(selectedButtonId).get(0));
		}
		else if(selectedButtonId == 8)	//pathPoint start è selezionata
			editing.setSelectedTile(new Tile(Arrays.asList(pathPointImgs.get(0)), Constants.PathPoints.PATHPOINT_START_ID, Constants.PathPoints.PATHPOINT_START_ID));
		else //pathPoint end è selezionata
			editing.setSelectedTile(new Tile(Arrays.asList(pathPointImgs.get(1)), Constants.PathPoints.PATHPOINT_END_ID, Constants.PathPoints.PATHPOINT_END_ID));
	}
	
	/**
     * quando si clicca nell' area del AnchorPane(a cui viene attacato il canvas), si richiama questo metodo
     * @param event
     */
    private void changeTile(double x, double y) {
    	if(wavePane.isVisible())
    		Utilz.fadeTransition(wavePane);
    	
    	//System.out.println(editing.getSelectedTile().getTileType());
    	if(editing.getSelectedTile()!= null) {
    		//504 / 32 = 12.56, essendo int il quoziente è 12(non 12.56) e 12*32 ci dà la posizione della Tile
    		int mouseX = ((int)x / 32) * 32;
    		int mouseY = ((int)y / 32) * 32;
    		editing.changeTile(mouseX, mouseY);
    	}
    }
    
	/**
	 * itera attraverso la mappa delle tile per selezionare di volta in volta l'id della tile con l' immagine ruotata
	 * @param tileType
	 */
	private void rotateSprite() {
		currentRotationIndex++;
		if(currentRotationIndex >= tiles.get(selectedButtonId).size())
			currentRotationIndex = 0;
		
		System.out.println("Rotation index : " + currentRotationIndex + " //// sizeArray : " + tiles.get(selectedButtonId).size());
		editing.setSelectedTile(tiles.get(selectedButtonId).get(currentRotationIndex));
	}
	
	/**
	 * aggiunge un nemico alla lsita di nemici della ondata corrente
	 * @param event
	 */
	private void addEnemyToWave(Event event) {
		if(!wavePane.isVisible()) {
    		Utilz.fadeTransition(wavePane);
		}
		else {
			Button enemyButton = (Button) event.getSource();
			int enemyType = (Integer) enemyButton.getUserData();
			
			editing.getLevelWaves().get(currentWaveIndex).getEnemyList().add(enemyType);
	
			updateWaveGridPane(enemyType);
		}
	}
	
	/**
	 * crea ed inizializza una nuova riga contentente un bottone per scegliere l'ondata e le informazioni sul numero di nemici
	 */
	private void addNewWave() {
		editing.addNewWave();
		currentWaveIndex++;

		wavesButton.add(Utilz.createButton("waveButton" + wavesButton.size(), "WAVE " + wavesButton.size(), wavesButton.size(), 100, 20, event-> waveToModify( (int) ((Button) event.getSource()).getUserData() )));

		int rowIndex = wavesGridPane.getRowCount();
		wavesGridPane.addRow(rowIndex, wavesButton.get(wavesButton.size()-1));
        
		HBox waveHBox = new HBox();
		waveHBox.setId("waveHBox" + (wavesButton.size()));
		
		for(int j=0; j<enemiesButton.size(); j++) {
			ImageView image = new ImageView();
			image.setId("enemyImage" + j);
			image.setImage(enemiesImgs.get(j));

			Text text = new Text("x " + Collections.frequency(game.getEditor().getLevelWaves().get(currentWaveIndex).getEnemyList(), j ));
			text.setId("enemyCount" + j);

			waveHBox.getChildren().addAll(image, text);
		}
		wavesGridPane.add(waveHBox, 1, rowIndex);
	}
	
	/**
	 * Per l'ondata selezionata, aggiorni il testo corrispondente al tipo di nemico
	 * @param rowIndex
	 */
	private void updateWaveGridPane(int columnIndex) {
		for(Node gridNnode : wavesGridPane.getChildren()) {
			
			if(gridNnode.getId().equals("waveHBox" + (currentWaveIndex+1))) {
				HBox waveHBox = (HBox) gridNnode;

				for(Node textNode : waveHBox.getChildren()) {					
					if(textNode.getId().equals("enemyCount" + columnIndex)) {
						Text text = (Text) textNode;
						text.setText("x " + Collections.frequency(game.getEditor().getLevelWaves().get(currentWaveIndex).getEnemyList(), columnIndex ));
					}
				}
			}
		}
	}
	
	/**
	 * setta l'ondata da modificare e ne gestisce il feedBack
	 * @param index l'indice dell'ondata da modificare
	 */
	private void waveToModify(int index) {
		this.setWaveIndex(index);
		highlightRow(index+1, 2, Color.RED);
	}
	
	private void highlightRow(int rowIndex, double borderWidth, Color color) {
        BorderStroke borderStroke = new BorderStroke( color.desaturate(),  BorderStrokeStyle.DASHED, new CornerRadii(5)  , new BorderWidths(borderWidth));
        Border border = new Border(borderStroke);
        
        for(Node gridNnode : wavesGridPane.getChildren()) {
        	if(gridNnode instanceof HBox) {
        		HBox waveHBox = (HBox) gridNnode;
        		if(waveHBox.getId().equals("waveHBox" + (rowIndex))) {  			
        			waveHBox.setPadding(new Insets(10));
        			waveHBox.setBorder(border);
        		}
        		else {
        			waveHBox.setPadding(Insets.EMPTY);
        			waveHBox.setBorder(null);
        		}
        	}
        }
	}
	private void initTilesButton() {
		for(int i=0; i<tilesImgs.size(); i++) {
			tilesButton.add(Utilz.createButton(null, null, i, 64, 64, event -> setSelectedTile(event)));
		}
		
		Iterator<Button> iterator = tilesButton.iterator();
		while(iterator.hasNext()) {
			Button button = iterator.next();
			Utilz.setButtonImg(button, tilesImgs.get(button.getUserData()));
		}
	}
	
	private void initEnemiesButton() {
		for(int i=0; i<enemiesImgs.size(); i++) {
			enemiesButton.add(Utilz.createButton(""+ i, null, i, 64, 64, event -> addEnemyToWave(event)));
		}
		
		Iterator<Button> iterator = enemiesButton.iterator();
		while(iterator.hasNext()) {
			Button button = iterator.next();
			Utilz.setButtonImg(button, enemiesImgs.get(button.getUserData()));
		}
	}
	
	private void initPathPointButton() {
		pathPointButton.add(Utilz.createButton(null, null, 0, 64, 64, event -> setSelectedTile(event)));
		pathPointButton.add(Utilz.createButton(null, null, 1, 64, 64, event -> setSelectedTile(event)));
	}
	
	/**
	 * va richiamata al caricamento della scena (quando viene impostato il livello, allora sappiamo come inizializzare il waveGridPane)
	 */
	public void initWaveGridPane() {
		wavesButton.clear();
		wavesGridPane.getChildren().clear();
		
		addWaveButton = Utilz.createButton("addWaveButton", "NEW WAVE", 0, 100, 10, event -> addNewWave());
		wavesGridPane.add(addWaveButton , 0, 0);
		
		for(int i=0; i < game.getEditor().getLevelWaves().size(); i++) {
			wavesButton.add(Utilz.createButton("waveButton" + i, "WAVE " + i, i, 100, 20, event-> waveToModify( (int) ((Button) event.getSource()).getUserData() )));
			
			wavesGridPane.add(wavesButton.get(i), 0, i+1);
		}
		
		// i<wavesGridPane.getRowCount()-1 . Il -1 è per togliere dal conto l'addWaveButton
		for(int i=0; i<wavesGridPane.getRowCount()-1; i++) {
			HBox waveHBox = new HBox();
			waveHBox.setId("waveHBox" + (i+1));
			
			for(int j=0; j<enemiesButton.size(); j++) {
				ImageView image = new ImageView();
				image.setId("enemyImage" + j);
				image.setImage(enemiesImgs.get(j));
				
				Text text = new Text("x " + Collections.frequency(game.getEditor().getLevelWaves().get(i).getEnemyList(), j ));
				text.setId("enemyCount" + j);
				
				waveHBox.getChildren().addAll(image, text);
			}
			wavesGridPane.add(waveHBox, 1, i+1);
		}
		
		//addWaveButton = Utilz.createButton("addWaveButton", "NEW WAVE", 0, 100, 10, event -> addNewWave());
		//wavesGridPane.add(addWaveButton , 0, wavesGridPane.getRowCount()+1);
	}
	
	private void initTilesMap() {
		tiles.put(0, game.getTileManager().getGrass());
		tiles.put(1, game.getTileManager().getWater());
		tiles.put(2, game.getTileManager().getRoadsS());
		tiles.put(3, game.getTileManager().getRoadsC());
		tiles.put(4, game.getTileManager().getCorners());
		tiles.put(5, game.getTileManager().getIslands());
		tiles.put(6, game.getTileManager().getAlberelli());
		tiles.put(7, game.getTileManager().getBridges());	
	}
	
	private void initMapTilesImgs() {
		for(int i=0; i<8; i++)
			tilesImgs.put(i, tiles.get(i).get(0).getSprite());

		tilesImgs.put(8, game.getEditor().getPathStartImg());
		tilesImgs.put(9, game.getEditor().getPathEndImg());
	}
		
	private void initMapEnemiesImgs() {
		List<Map<Integer, List<WritableImage>>>enemiesAllImgs = game.getPlaying().getEnemyManager().getEnemiesImgs();
		
		enemiesImgs.put(0, enemiesAllImgs.get(0).get(0).get(0));
		enemiesImgs.put(1, enemiesAllImgs.get(1).get(0).get(0));
		enemiesImgs.put(2, enemiesAllImgs.get(2).get(0).get(0));
		enemiesImgs.put(3, enemiesAllImgs.get(3).get(0).get(0));
		enemiesImgs.put(4, enemiesAllImgs.get(4).get(0).get(0));

	}
	
	private void initPathPointImgs(){
		pathPointImgs.add(game.getEditor().getPathStartImg());
		pathPointImgs.add(game.getEditor().getPathEndImg());
	}
	
	private void setWaveIndex(int waveIndex) {
		this.currentWaveIndex = waveIndex;
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public void addGameCanvas() {
		gameCanvasAnchorPane.getChildren().add(CanvasManager.getInstance().getGameCanvas());
	}
	
	public void removeGameCanvas() {
		gameCanvasAnchorPane.getChildren().remove(CanvasManager.getInstance().getGameCanvas());
	}

}
