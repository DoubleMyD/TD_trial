package game_scenes_logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import events.Wave;
import game.Game;
import helpz.Constants;
import helpz.LoadSave;
import helpz.Utilz;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import objects.Level;
import objects.PathPoint;
import objects.Tile;

public class Editing implements GameSceneMethods{
	private GameScene gameScene;
	private Game game;
	private Level level;
	
	private PathPoint pathStart, pathEnd;
	private WritableImage pathStartImg, pathEndImg;
	
	private int[][]lvl;
	private Tile selectedTile;
	private int lastTileX, lastTileY, lastTileId;
	private boolean drawSelect;
	
	private List<Integer> enemyWave = new ArrayList<>();
	private List<Wave> levelWaves = new ArrayList<>();
	private List<WritableImage> enemyImgs = new ArrayList<>();
	
	public Editing(Game game) {
		this.game = game;
		this.gameScene = game.getGameScene();
		
		initPathImgs();
		loadDefaultLevel();
		initEnemyImgs();
	}
	
	
	
	public void changeTile(int x, int y) {
		if(selectedTile != null) {
			int tileX = x / Constants.TILE_DIMENSION;
			int tileY = y / Constants.TILE_DIMENSION;
			
			if((selectedTile.getId() != Constants.PathPoints.PATHPOINT_END_ID) && (selectedTile.getId() != Constants.PathPoints.PATHPOINT_START_ID)) {
				if(lastTileX == tileX && lastTileY == tileY && lastTileId == selectedTile.getId())
					return;
				
				lastTileX = tileX;
				lastTileY = tileY;
				lastTileId = selectedTile.getId();
				//il livello è una matrice con l' id della tile da disegnare in ogni posizione, quindi basta cmabiare l' id per modificare la Tile disegnata in quella posizione
				//gameScreen.setLvlPoint(selectedTile.getId(), tileY, tileX);
				lvl[tileY][tileX] = selectedTile.getId();
			}
			else {
				int id = lvl[tileY][tileX];
				
				if (game.getTileManager().getTile(id).getTileType() == Constants.Tiles.ROAD_TILE) {
					 if (selectedTile.getId() == Constants.PathPoints.PATHPOINT_START_ID) {
						pathStart = new PathPoint(tileX, tileY);
					 } else {
						 pathEnd = new PathPoint(tileX, tileY);
					 }
				 }
			}
		}
	}

	@Override
	public void render(GraphicsContext gc) {
		drawLevel(gc);
		drawSelectedTile(gc); //lo sposti nell'editing Controller
	}
	
	@Override
	public void update() {
		gameScene.updateTick();
	}
	
	
	public boolean saveLevel(String name) {
		if(!name.equals("default_level")) {
			gameScene.saveLevel(name, lvl, pathStart, pathEnd, levelWaves);
			return true;
		}
		return false;
	}
	
	public void createLevel(String name) {
		LoadSave.CreateLevel(name, Utilz.TwoDto1DintArr(lvl), pathStart, pathEnd, levelWaves);
	}
	
	public void addNewWave() {
		levelWaves.add(new Wave(new ArrayList<Integer>()));
	}
	
	public void saveWave(int waveIndex, List<Integer> enemyList) {
		this.levelWaves.get(waveIndex).getEnemyList().clear();
		this.levelWaves.get(waveIndex).getEnemyList().addAll(enemyList);
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
	
	public void drawPathPoints(GraphicsContext gc) {
		if (pathStart != null)
			gc.drawImage(pathStartImg, pathStart.getxCord() * Constants.TILE_DIMENSION, pathStart.getyCord() * Constants.TILE_DIMENSION, Constants.TILE_DIMENSION, Constants.TILE_DIMENSION);

		if (pathEnd != null)
			gc.drawImage(pathEndImg, pathEnd.getxCord() * Constants.TILE_DIMENSION, pathEnd.getyCord() * Constants.TILE_DIMENSION, Constants.TILE_DIMENSION, Constants.TILE_DIMENSION);

	}
	
	/**
	 * //se abbiamo selezionato una Tile e drawSelect è true, disegnamo quella Tile accanto al cursore del mouse
	 * @param g
	 */
	private void drawSelectedTile(GraphicsContext gc) {
		if (selectedTile != null && drawSelect) {
			gc.drawImage(selectedTile.getSprite(), gameScene.getMouseX(), gameScene.getMouseY(), 32, 32);
		}
	}
	
	private void initPathImgs() {
		pathStartImg = SwingFXUtils.toFXImage(LoadSave.getFileSprite("spriteatlas.png").getSubimage(7 * 32, 2 * 32, 32, 32), null);
		pathEndImg = SwingFXUtils.toFXImage(LoadSave.getFileSprite("spriteatlas.png").getSubimage(8 * 32, 2 * 32, 32, 32), null);

	}
	
	public void loadDefaultLevel() {
		lvl = LoadSave.GetLevelData("default_level");
		ArrayList<PathPoint> points = LoadSave.GetLevelPathPoints("default_level");
		pathStart = points.get(0);
		pathEnd = points.get(1);
	}
	
	private void initEnemyImgs() {
		List<Map<Integer, List<WritableImage>>> temp = game.getPlaying().getEnemyManager().getEnemiesImgs();
		
		for(int i=0; i<5; i++) {			
			enemyImgs.add(temp.get(i).get(0).get(0));
		}
	}
	
	/**
	 * assegna la Tile selzionata e abilita la possibilità di disegnare la tile selezionata
	 * @param tile
	 */
	public void setSelectedTile(Tile tile) {
		this.selectedTile = tile;
		drawSelect = true;
	}
	
	public Tile getSelectedTile() {
		return selectedTile;
	}

	public void setLvl(int[][] lvl) {
		this.lvl = lvl;
	}
	/*
	public List<Integer> getEnemyWave(){
		return this.enemyWave;
	}*/
	
	public void setLevelWaves(List<Wave> levelWaves) {
		this.levelWaves = levelWaves;
	}
	
	public List<Wave> getLevelWaves() {
		return levelWaves;
	}

	public PathPoint getPathStart() {
		return pathStart;
	}

	public void setPathStart(PathPoint pathStart) {
		this.pathStart = pathStart;
	}

	public PathPoint getPathEnd() {
		return pathEnd;
	}

	public void setPathEnd(PathPoint pathEnd) {
		this.pathEnd = pathEnd;
	}
	
	public WritableImage getPathStartImg() {
		return this.pathStartImg;
	}
	
	public WritableImage getPathEndImg() {
		return this.pathEndImg;
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
		this.levelWaves = new ArrayList<>(level.getLevelWaves());
		
	}



}
