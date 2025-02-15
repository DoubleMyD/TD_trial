package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enemies.Enemy;
import game_scenes_logic.Playing;
import helpz.Constants;
import helpz.Constants.Direction;
import helpz.Constants.Enemies;
import helpz.Constants.Tiles;
import helpz.LoadSave;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import objects.PathPoint;

public class EnemyManager {
	private Playing playing;
	
	//enemiesImgs.get(enemyType).get(enemyOrientation).get(animationIndex)
	private List<Map<Integer, List<WritableImage>>> enemiesImgs = new ArrayList<>();	//lista che per ogni nemico contiene una mappa in cui in posizione 0 c'è l'animazione orientata verso sinistra, in posizione 1 orientata verso destra
	
	private List<Enemy> enemies = new ArrayList<>();	//Lista dei nemici nel livello
		
	private int HPbarWidth = 20;
	private WritableImage slowEffect;	//immagine per l'effetto dello slow
	private static int enemyNumberId = 0;	//numero di nemici generati
	
	public EnemyManager(Playing playing) {
		this.playing = playing;
		
		loadEffectImg();
		loadEnemiesImgs();
	}
	
	/**
	 * carica l' immagine dell'effetto di rallentamento dall'atlas
	 */
	private void loadEffectImg() {
		slowEffect = SwingFXUtils.toFXImage(LoadSave.getFileSprite("spriteatlas.png").getSubimage(32 * 9, 32 * 2, 32, 32), null);
	}
	
	private void loadEnemiesImgs() {
		//per ogni nemico c'è una mappa
		for(int i = 0; i<5; i++) {
			Map<Integer, List<WritableImage>> orientationMap = new HashMap<Integer, List<WritableImage>>();
			for(int j = 0; j < 2; j++) {
				List<WritableImage> orientedAnimationList = new ArrayList<WritableImage>();
				orientationMap.put(j, orientedAnimationList);
			}
			enemiesImgs.add(orientationMap);
		}
		
		loadEnemyTypeImgs("basket_enemy", Constants.Enemies.BASKET, 4);
		loadEnemyTypeImgs("orc_original", Constants.Enemies.ORC, 1);
		loadEnemyTypeImgs("knight_original", Constants.Enemies.KNIGHT, 1);
		loadEnemyTypeImgs("bat_original", Constants.Enemies.BAT, 1);
		loadEnemyTypeImgs("wolf_original", Constants.Enemies.WOLF, 1);
	}
	
	/**
	 * salva l'animazione del nemico per le orientazioni sinistra e destra. !!!!formato del nome del file deve essere per esempio: enemy_enemyType_numeroVersione . La funzione lo completa con : enemy_enemyType_1.0_toLeft2.png ; dove 1.0 è la versione della torre e 2 è un frame dell'animazioe
	 * @param enemyFileName	nome del file immagine del nemico
	 * @param enemyType	tipo di nemico, per sapere in quale mappa salvare le immagini
	 * @param animationSize	grandezza dell'animazione, per sapere quante immagini aggiungere nella lista dell'animazione
	 */
	private void loadEnemyTypeImgs(String enemyFileName, int enemyType, int animationSize) {
		for(int i = 0; i < animationSize; i++) {
			enemiesImgs.get(enemyType).get(0).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite(enemyFileName + "_toLeft" + (i+1) + ".png"), null));
		}
		//TEMPORANEO!!! FINCHE' NON IMPLEMENTI LA DIREZIONE DELLE IMMAGINI PER I NEMICI
		for(int i = 0; i < animationSize; i++) {
			enemiesImgs.get(enemyType).get(1).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite(enemyFileName + "_toLeft" + (i+1) + ".png"), null));
		}
		
		/* da usare quando implementerai le animazioni per i nemici
		for(int i = 0; i < animationSize; i++) {
			enemiesImgs.get(enemyType).get(1).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite(enemyFileName + "_toRight" + i + ".png"), null));
		}*/
	}
	
	/**
	 * si occupa di aggiornare la posizione dei nemici e gestire le ondate
	 */
	public void update() {
		for(Enemy e: enemies) {
			if(e.isAlive()) {
				e.update();
				updateEnemyMove(e);
			}
		}
	}
	
	/**
	 * se la Tile controllata è valida(ROAD) muove il nemico in quella Tile.
	 * Se la tile non è valida, cambia direzione e muove il nemico
	 * @param e
	 * @return
	 */
	public void updateEnemyMove(Enemy e) {
		//se ti trovi al pirmo movimento(appena avvii il gioco), imposta una nuova direzione
		if (e.getLastDir() == -1)
			setNewDirectionAndMove(e);
		
		//System.out.println("Enemy PRE-UPDATE pos : " + e.getX() + " : " + e.getY());

		int newX = (int) (e.getX() + getSpeedAndWidth(e.getLastDir(), e.getEnemyType()));
		int newY = (int) (e.getY() + getSpeedAndHeight(e.getLastDir(), e.getEnemyType()));
		
		//System.out.println("Enemy POST-Update pos : " + newX + " : " + newY);
		if (isAtEnd(e)) {
			e.kill();
			playing.removeOneLife();
		}
		else {
			//se le nuove coordinate calcolate sono valide muove il nemico
			if (getTileType(newX, newY) == Tiles.ROAD_TILE) {
				e.move(Enemies.GetSpeed(e.getEnemyType()), e.getLastDir());
				//System.out.println("Enemy PRE-POST_UPDATE pos : " + e.getX() + " : " + e.getY());

			}/* else if (isAtEnd(e)) {
				e.kill();
				playing.removeOneLife();
			} */else {
				//cerca una nuova tile valida e ci muove il nemico
				setNewDirectionAndMove(e);
				//System.out.println("Enemy PRE-DIrections////UPDATE pos : " + e.getX() + " : " + e.getY());
			}
		}
	}
	
	/**
	 * cambia  direzione (se l' ultima è RIGHT, la nuova sarà per forza UP o DOWN) e muove il nemico nella nuova direzione
	 * @param e
	 */
	private void setNewDirectionAndMove(Enemy e) {
		int dir = e.getLastDir();

		int xCord = (int) (e.getX() / 32);
		int yCord = (int) (e.getY() / 32);
		//allinea le coordinate del nemico con quella della tile sottostante
		fixEnemyOffsetTile(e, dir, xCord, yCord);

		//se ha raggiunto la fine, non effettua nessun movimento
		if(isAtEnd(e))
			return;

		//se l' ultima posizione è LEFT o RIGHT, vuol dire che dobbiamo controllare UP e DOWN per cmabiarla
		if(dir== Direction.LEFT || dir == Direction.RIGHT) {
			//newY raprresenta la nuova POSSIBILE posizione per la Y
			int newY = (int)(e.getY() + getSpeedAndHeight(Direction.UP, e.getEnemyType()));
			
			//controlla se la tile nella posizione (X, newY) è valida per muoversi
			if(getTileType((int) e.getX(), newY) == Tiles.ROAD_TILE) {
				e.move(Enemies.GetSpeed(e.getEnemyType()), Direction.UP);
			}else {
				e.move(Enemies.GetSpeed(e.getEnemyType()), Direction.DOWN);

			}
		} else {
			//newX raprresenta la nuova POSSIBILE posizione per la X
			int newX = (int) (e.getX() + getSpeedAndWidth(Direction.RIGHT, e.getEnemyType()));
			//controlla se la tile nella posizione (newX, Y) è valida per muoversi
			if(getTileType(newX, (int) e.getY()) == Tiles.ROAD_TILE) {
				e.move(Enemies.GetSpeed(e.getEnemyType()), Direction.RIGHT);
			}
			else {
				e.move(Enemies.GetSpeed(e.getEnemyType()), Direction.LEFT);
			}
		}
	}
	
	/**
	 * siccome il controllo della nuova tile viene effettuato prima che il nemico sia entrato completamente nella tile attuale, il controllo della tile successiva viene effettuato sulla tile precedente.
	 * Aggiustando la posizione del nemico con quelle delle tile ( 3.19 diventa 3, allinendosi con la posizione delle tile del livello) il controllo viene effettuato nella tile corretta
	 * se non hai capito guarda l' immagine "Spiegazione pathfinding (fixEnemyOffsetFile)"
	 */
	private void fixEnemyOffsetTile(Enemy e, int dir, int xCord, int yCord) {
		switch (dir) {
		case Direction.RIGHT:
			if (xCord < Constants.LVL_LENGTH-1)
				xCord++;
			break;
		case Direction.DOWN:
			if (yCord < Constants.LVL_ROWS-1)
				yCord++;
			break;
		}
		
		e.setPos(xCord * 32, yCord * 32);
	}
	
	/**
	 * se X e Y del nemico corrispondono a quelle dell' END (PathPoint), restituisce true
	 * @param e
	 * @return
	 */
	private boolean isAtEnd(Enemy e) {
		System.out.println(e.getX() );
		
		if (e.getX() == playing.getPathEnd().getxCord() * Constants.TILE_DIMENSION)
			if (e.getY() == playing.getPathEnd().getyCord() * Constants.TILE_DIMENSION)
				return true;
		return false;
	}
	
	private int getTileType(int x, int y) {
		return playing.getTileType(x, y);
	}
	
	/**
	 * Viene usata per il pathFinding, per calcolare la nuova ipotetica posizione del nemico
	 * aggiorna la velocità per UP e DOWN, e per DOWN aggiunge la dimensione dell' immagine(speed+32) per facilitare l' allineamento del nemico con la tile del livello(fixEnemyOffsetTile()).
	 * Il punto di origine della posizione del nemico è l'angolo in alto a sinistra (ecco perchè il +32)
	 * @param dir
	 * @return
	 */
	private float getSpeedAndHeight(int dir, int enemyType) {
		if(dir == Direction.UP)
			return -Enemies.GetSpeed(enemyType);
		else if(dir == Direction.DOWN)
			return Enemies.GetSpeed(enemyType) + Constants.TILE_DIMENSION;
		
		return 0;
	}
	
	/**
	 * Viene usata per il pathFinding, per calcolare la nuova ipotetica posizione del nemico
	 * aggiorna la velocità per RIGHT e LEFT, e per RIGHT aggiunge la dimensione dell' immagine(speed+32) per facilitare l' allineamento del nemico con la tile del livello(fixEnemyOffsetTile())
	 * Il punto di origine della posizione del nemico è l'angolo in alto a sinistra (ecco perchè il +32)
	 * @param dir
	 * @return
	 */
	private float getSpeedAndWidth(int dir, int enemyType) {
		if(dir == Direction.LEFT)
			return -Enemies.GetSpeed(enemyType);
		else if (dir == Direction.RIGHT)
			return Enemies.GetSpeed(enemyType) + Constants.TILE_DIMENSION;
		
		return 0;
	}
	
	public void spawnEnemy(int nextEnemy) {
		addEnemy(nextEnemy);
	}
	
	/**
	 * aggiunge un nemico del tipo specificato nella posizione di partenza (indicata dallo START (PathPoint))
	 * @param enemyType
	 */
	public void addEnemy(int enemyType) {
		int x = playing.getPathStart().getxCord() * Constants.TILE_DIMENSION;
		int y = playing.getPathEnd().getyCord() * Constants.TILE_DIMENSION;

		enemies.add(new Enemy(enemiesImgs.get(enemyType) ,x,y, enemyNumberId, enemyType, this));
		enemyNumberId++;
	}
	
	/**
	 * disegna ogni nemico ( se vivo )
	 * @param g
	 */
	public void draw(GraphicsContext gc) {				
		for (Enemy e : enemies) {
			if (e.isAlive()) {
				e.draw(gc);
				drawHealthBar(e, gc);
				drawEffects(e, gc);
				
			}
		}
	}
	
	/**
	 * disegna l'effetto del rallentamento solo sui nemici rallentati
	 * @param e
	 * @param g
	 */
	private void drawEffects(Enemy e, GraphicsContext gc) {
		if (e.isSlowed())
			gc.drawImage(slowEffect, (int) e.getX(), (int) e.getY());
	}
	
	/**
	 * disegna la barra della vita allineandola con la posizione del nemico corrispondente
	 * @param e
	 * @param g
	 */
	private void drawHealthBar(Enemy e, GraphicsContext gc) {
		gc.setFill(Color.RED);
		//allinea la barra della vita con il centro del nemico. Prima fa partire la barra dalla metà del nemico(16 è la metà della grandezza(32) del nemico) e poi la accentra spostandola indietro della sua metà
		gc.fillRect((int) e.getX() + 16 - (getNewBarWidth(e) / 2), (int) e.getY() - 10, getNewBarWidth(e), 3);
	}

	/**
	 * moltiplicando la grandezza iniziale (default) per il rapporto tra la vita attuale e quella massima si ottiene la grandezza della barra.
	 * (100/100 * 20 = 20, ovvero la barra priena,  50/100 * 20 = 10, ovvero metà della vita/lunghezza della barra)
	 * @param e
	 * @return
	 */
	private int getNewBarWidth(Enemy e) {
		return (int) (HPbarWidth * e.getHealthBarFloat());
	}
	
	public void rewardPlayer(int enemyType) {
		playing.rewardPlayer(enemyType);
	}
	
	/**
	 * pulisce l' array dei nemici
	 */
	public void reset() {
		enemies.clear();
	}
	
	public List<Enemy> getEnemies(){
		return enemies;
	}
	/**
	 * esempio di utilizzo : enemiesImgs.get(enemyType).get(enemyOrientation).get(animationIndex)
	 * @return
	 */
	public List<Map<Integer, List<WritableImage>>> getEnemiesImgs(){
		return enemiesImgs;
	}
	/*
	public PathPoint getPathStart() {
		return pathStart;
	}
	public void setPathStart(PathPoint start) {
		this.pathStart = start;
	}
	public PathPoint getPathEnd() {
		return pathEnd;
	}
	public void setPathEnd(PathPoint end) {
		this.pathEnd = end;
	}*/
	
}
