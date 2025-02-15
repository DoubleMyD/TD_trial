package game_scenes_logic;

import java.util.ArrayList;


import enemies.Enemy;
import game.Game;
import helpz.Constants;
import helpz.Constants.Tiles;
import helpz.LoadSave;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import managers.EnemyManager;
import managers.ProjectileManager;
import managers.TowerManager;
import managers.WaveManager;
import objects.Level;
import objects.PathPoint;
import scenes_controller.PlayingController;
import towers.Tower;

public class Playing implements GameSceneMethods {
	private static final int addGoldTime = 60 * 3; //intervallo di tempo dopo il quale aggiungere l'oro
	private static final int addGoldAmount = 1; //quantità di oro da aggiungere ogni tot secondi

	private static final int GOLD_START = 500;	//l'oro all' inizio di ogni partita
	private static final int LIVES_START = 1; 	//le vite all'inizio di ogni partita
	
	private Game game;
	private GameScene gameScene;
	private Level level;
	private PlayingController playingController;
	
	private int[][]lvl;
	//private int mouseX, mouseY;
	
	private EnemyManager enemyManager;
	private TowerManager towerManager;
	private ProjectileManager projManager;
	private WaveManager waveManager;
	
	private PathPoint pathStart, pathEnd;
	private Tower selectedTower;
	private Tower displayedTower;
	
	private int goldTick;
	private boolean gamePaused;
	
	private int gold = GOLD_START;
	private int lives = LIVES_START;
	
	private int tryY, tryX;
	
	public Playing(Game game) {
		this.game = game;
		this.gameScene = game.getGameScene();
		
		loadDefaultLevel();
		
		enemyManager = new EnemyManager(this);
		towerManager = new TowerManager(this);
		projManager = new ProjectileManager(this);
		waveManager = new WaveManager(this);
	}
	
	@Override
	public void render(GraphicsContext gc) {
		drawLevel(gc);
		
		drawHighlight(gc);
		drawDisplayedTower(gc);
		
		enemyManager.draw(gc);
		towerManager.draw(gc);
		projManager.draw(gc);
	}
	/*
	private void drawHighlight(GraphicsContext gc) {
		gc.setStroke(Color.WHITE);
		gc.strokeRect(gameScene.getMouseX(), gameScene.getMouseY(), 64, 64);
		
	}*/
	
	private void drawHighlight(GraphicsContext gc) {
		gc.setStroke(Color.WHITE);
		gc.strokeRect(tryX, tryY, 64, 64);
		
	}
	
	private void drawDisplayedTower(GraphicsContext gc) {
		if (displayedTower != null) {
			drawDisplayedTowerRange(gc);
		}
	}
	
	private void drawDisplayedTowerRange(GraphicsContext gc) {
		gc.setFill(Color.WHITE);
		//il cerchio viene calcolato dall' angolo in alto a sinistra (X e Y), quindi viene centrato rispetto alla torre
		//allinea il cerchio con il centro del nemico
		//prima fa partire il cerchio dalla metà della torre(16 è la metà della grandezza(32) della torre) e poi lo accentra spostandolo indietro della metà del diametro del cerchio (quindi del range/raggio)
		gc.strokeOval(displayedTower.getX() + 32 - (int) (displayedTower.getRange() * 2) / 2, displayedTower.getY() + 32 - (int) (displayedTower.getRange() * 2) / 2, 
					(int) displayedTower.getRange() * 2, (int) displayedTower.getRange() * 2);
	}

	@Override
	public void update() {
		if(!gamePaused) {

			waveManager.update();
			
			updateGold();
			
			if(isAllEnemiesDead()) {
				if(waveManager.isThereMoreWaves()) {
					waveManager.startWaveTimer();
					
					if(waveManager.isWaveTimerOver()) {
						waveManager.increaseWaveIndex();
						enemyManager.getEnemies().clear();
						waveManager.resetEnemyIndex();
					}
				}
			}
			
			if (isTimeForNewEnemy()) {
				enemyManager.spawnEnemy(waveManager.getNextEnemy());
			}

			enemyManager.update();
			
			towerManager.update();
			projManager.update();
			
			Platform.runLater(() -> playingController.updatePlayingController());
		}
		/*
		playingController.update();	//qui ci metti il gold text ed altre cose da aggiornare in continuazione
		
		//mettilo nel playingController
		if (lives <= 0) {
			GameStates.SetGameState(GameStates.GAME_OVER);
			Platform.runLater(() -> playingController.addGameOverStage());
		}
		System.out.println("vita persa");
		*/
		//playingController.updatePlayingController();
	}
	
	private void updateGold() {
		goldTick++;
		if( (goldTick % addGoldTime) == 0) {
			gold += addGoldAmount;
		}
	}
	
	private boolean isAllEnemiesDead() {
		//controllo per sapere se devono ancora essere spawnati ttui i nemici dell' ondata in corso
		if (waveManager.isThereMoreEnemiesInWave())
			return false;

		for (Enemy e : enemyManager.getEnemies())
			if (e.isAlive())
				return false;

		return true;
	}
	
	/**
	 * restituisce true se il contatore/timer dell'ondata ha raggiunto il limite e ci sono nuovi nemici da spawnare
	 * @return
	 */
	private boolean isTimeForNewEnemy() {
		if (waveManager.isTimeForNewEnemy()) {
			if (waveManager.isThereMoreEnemiesInWave())
				return true;
		}

		return false;
	}
	
	/**
	 * usato nel pathfinding per sapere il tipo di tile nella casella successiva 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getTileType(int x, int y) {
		//try {
			int xCord = x / Constants.TILE_DIMENSION;	
			int yCord = y / Constants.TILE_DIMENSION;
			//se esce fuori dallo schermo(array del livello), considera la Tile come WATER e quindi non ci si può camminare sopra
			if(xCord < 0 || xCord > Constants.LVL_LENGTH-1)//old value = 19
				return 0;
			if(yCord < 0 || yCord > Constants.LVL_ROWS-1)//old value = 19
				return 0;

			int id = lvl[y / Constants.TILE_DIMENSION][x / Constants.TILE_DIMENSION];
			return game.getTileManager().getTile(id).getTileType();
	/*	}catch(ArrayIndexOutOfBoundsException e) {
			Platform.runLater(() -> CanvasManager.getInstance().updateCanvas());
			Platform.runLater(() -> resetEverything());
			return 0;
		}*/
	}
	/*
	public void placeTower() {
		//prova a piazzare la torre, controllando sia posizionata su una GRASS
		if (isTileGrass(gameScene.getMouseX(), gameScene.getMouseY())) {
			//se non c'è nessuna torre nella tile selezionata, la aggiunge/crea
			if(getTowerAt(gameScene.getMouseX(), gameScene.getMouseY()) == null) {
				towerManager.addTower(selectedTower, gameScene.getMouseX(), gameScene.getMouseY());
				//rimuove il quantitativo di oro corrispondenete al tipo di torre appena piazzata
				payForTower(selectedTower.getTowerType());
				
				//in questo modo l'ultima torre piazzata è quella di cui disegnare le info
				displayTower(towerManager.getTowers().get(towerManager.getTowerAmount()-1));
				selectedTower = null;
			}
		}

	}*/
	
	public void placeTower() {
		//prova a piazzare la torre, controllando sia posizionata su una GRASS
		if (isTileGrass(tryX, tryY)) {
			//se non c'è nessuna torre nella tile selezionata, la aggiunge/crea
			if(getTowerAt(tryX, tryY) == null) {
				towerManager.addTower(selectedTower, tryX, tryY);
				//rimuove il quantitativo di oro corrispondenete al tipo di torre appena piazzata
				payForTower(selectedTower.getTowerType());
				
				//in questo modo l'ultima torre piazzata è quella di cui disegnare le info
				displayTower(towerManager.getTowers().get(towerManager.getTowerAmount()-1));
				selectedTower = null;
			}
		}

	}
	
	/**
	 * seleziona una torre. Se nessuna torre è presente nella torre indicata, disattiva la visualizzazione delle info della torre (displaytower)
	 * @return true se è stata selezionata una torre, false altrimenti
	 */
	public Boolean selectTower() {	
		//se non è selezionata nessuna torre (quelle "creare", controlla che esista una torre in quella Tile
		Tower t = getTowerAt(gameScene.getMouseX(),  gameScene.getMouseY());
		
		//non effetuiamo controlli sul fatto che esista o meno la torre perchè la disegnamo solo se displayedTower != null, quindi se getTowerAt() non trova nessuna torre restituirà null, e di conseguenza non verrà disegnata alcuna torre
		if(t != null) {
			displayTower(t);
			//infoPane.setVisible(true);
			return true;
		}
		else {
			displayedTower = null;
			return false;
		}
	}

	/**
	 * esegue l'upgrade della torre. Se la torre non è al tier massimo restituisce true, se non può più essere upgradeata restituisce false
	 * @return
	 */
	public Boolean upgradeTower() {
		towerManager.upgradeTower(displayedTower);
		gold -= getUpgradeAmount();
		
		if(displayedTower.getTier() >= 3) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * vende la torre selezionata, restituendo al giocatore l'oro corrispondente
	 */
	public void sellTowerClicked() {
		towerManager.removeTower(displayedTower);
		gold += Constants.Towers.GetTowerCost(displayedTower.getTowerType() / 2);
		
		int upgradeCost = (displayedTower.getTier() - 1) * getUpgradeAmount();
		upgradeCost *= 0.5f;
		gold += upgradeCost;
		
		displayedTower = null;
	}
	
	/**
	 * Restituisce il costo della torre moltiplicato per l'upgradeCost
	 * @return
	 */
	private int getUpgradeAmount() {
		return (int) (helpz.Constants.Towers.GetTowerCost(displayedTower.getTowerType()) * Constants.Towers.UPGRADE_COST);
	}
	
	private void displayTower(Tower t) {
		displayedTower = t;	//se la torre passata è null, anche la displayedTower sarà null
	}
	
	/**
	 * se nelle coordinate specificate c'è già una torre, restituisce null, altrimenti restituisce la torre
	 */
	private Tower getTowerAt(int x, int y) {
		return towerManager.getTowerAt(x, y);
	}
	
	private boolean isTileGrass(int x, int y) {
		int id = lvl[y / 32][x / 32];
		int tileType = game.getTileManager().getTile(id).getTileType();
		return tileType == Tiles.GRASS_TILE;
	}
	
	public void shootEnemy(Tower t, Enemy e) {
		projManager.newProjectile(t, e);
	}
	
	public void payForTower(int towerType) {
		this.gold -= helpz.Constants.Towers.GetTowerCost(towerType);
	}
	
	public boolean isGoldEnoughForTower(int towerType) {
		return gold >= helpz.Constants.Towers.GetTowerCost(towerType);
	}
	
	/**
	 * per ogni nemico ucciso aumenta l'oro posseduto del quantitativo corrispondente al tipo di nemico appena ucciso
	 * @param enemyType
	 */
	public void rewardPlayer(int enemyType) {
		gold += helpz.Constants.Enemies.GetReward(enemyType);
	}
	
	public void removeOneLife() {
		lives--;
		if(lives <= 0)
			playingController.addGameOverStage();
	}
	
	/**
	 * resetta tutte le variabili per poter ricominciare una partita da zero
	 */
	public void resetEverything() {
		displayedTower = null;
		lives = LIVES_START;
		gold = GOLD_START;
		// managers
		enemyManager.reset();
		towerManager.reset();
		projManager.reset();
		waveManager.reset();

		gameScene.setMouseX(0);
		gameScene.setMouseY(0);
		
		//azzera la torre selzionata, il counter del gold, e fa partire il livello non in pausa
		selectedTower = null;
		goldTick = 0;
		gamePaused = false;
	}
	
	@Override
	public void drawLevel(GraphicsContext gc) {
		for(int y = 0; y < lvl.length; y++) {
			for (int x = 0; x < lvl[y].length; x++) {
				int id = lvl[y][x];
				
				if(game.getTileManager().isSpriteAnimation(id)) {
					int tileType = game.getTileManager().getTile(id).getTileType();
					
					//se la tile è animata, controlla se è una WaterTile o una RoadTile (l'unica RoadTile animata è il ponte che contiene l'acqua)
					if( tileType != Constants.Tiles.WATER_TILE && tileType != Constants.Tiles.ROAD_TILE) {
						gc.drawImage(gameScene.getSprite(id, gameScene.getOtherAnimationIndex()), x * Constants.TILE_DIMENSION, y * Constants.TILE_DIMENSION);
					}
					else {
						gc.drawImage(gameScene.getSprite(id, gameScene.getWaterAnimationIndex()), x * Constants.TILE_DIMENSION, y * Constants.TILE_DIMENSION);
					}	
				}
				else {
					gc.drawImage(gameScene.getSprite(id), x * Constants.TILE_DIMENSION, y * Constants.TILE_DIMENSION);
				}
			}
		}
	}
	
	public void loadDefaultLevel() {
		lvl = LoadSave.GetLevelData("default_level");
		ArrayList<PathPoint> points = LoadSave.GetLevelPathPoints("default_level");
		pathStart = points.get(0);
		pathEnd = points.get(1);
	}

	public void setLvl(int[][] lvl) {
		this.lvl = lvl;
	}
	
	public void setSelectedTower (Tower selectedTower) {
		this.selectedTower = selectedTower;
	}
	
	public Tower getSelectedTower() {
		return selectedTower;
	}
	
	public void setGamePaused(boolean gamePaused) {
		this.gamePaused = gamePaused;
	}
	
	public TowerManager getTowerManager() {
		return towerManager;
	}
	
	public EnemyManager getEnemyManager() {
		return enemyManager;
	}
	
	public WaveManager getWaveManager() {
		return waveManager;
	}
	
	public ProjectileManager getProjectileManager() {
		return this.projManager;
	}
	
	public boolean isGamePaused() {
		return gamePaused;
	}
	
	public int getGold() {
		return gold;
	}

	public PathPoint getPathStart() {
		return pathStart;
	}

	public void setStart(PathPoint start) {
		this.pathStart = start;
	}

	public PathPoint getPathEnd() {
		return pathEnd;
	}

	public void setEnd(PathPoint end) {
		this.pathEnd = end;
	}
	
	/**
	 * imposta le informazioni del livello (matrice del livello, PathPoints, ondate)
	 * @param level
	 */
	public void setLevel(Level level) {
		this.level = level;
		
		this.lvl = level.getLvl();	//in questo modo non rallentiamo il gioco (non dovrà in ogni frame andare a recuperare l'array da un altro oggetto/classe
		this.pathStart = level.getPathStart();
		this.pathEnd = level.getPathEnd();
		this.waveManager.setWaves(level.getLevelWaves());
	}
	
	public Level getLevel() {
		return this.level;
	}
	
	public Boolean isLivesOver() {
		return lives <= 0;
	}
	
	public void setPlayingController(PlayingController playingController) {
		this.playingController = playingController;
	}

	public void setTryX(int i) {
		this.tryX = i;
	}

	public void setTryY(int i) {
		this.tryY = i;
	}
	
	public int getTryX() {
		return tryX;
	}

	public int getTryY() {
		return tryY;
	}
	
	
	
}
