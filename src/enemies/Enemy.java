package enemies;

import helpz.Constants;

import helpz.Constants.Direction;
import helpz.Constants.Enemies;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import managers.EnemyManager;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Enemy {
	private EnemyManager enemyManager;
	private float x, y;//essendo floats abbiamo una migliore precisione nel muovere lo sprite (possiamo muoverlo di 0.0001 pixel e non per forza da 1 in poi)
	private Rectangle bounds;	//è usato per controllare se il proiettile colpisce o meno il nemico
	
	private Map<Integer, List<WritableImage>> enemyImgs = new HashMap<>();//mappa con posizione zero arrayList di immagini orientate verso sinistra, posizione 1 orientate verso destra
	private int animationTick, animationIndex, maxAnimationIndex;
	private final int ANIMATION_SPEED = 10;
	
	private int health, maxHealth;
	private int ID, enemyType;
	
	
	private int lastDir; //serve per il pathfinding
	private int lastXDir; //serve per orientare le immagini
	private boolean alive = true;
	
	private final int slowTickLimit = 120;//in questo modo per modificare il tempo di rallentamento, dobbiamo cambiare solo questo valore
	private int slowTick = slowTickLimit;//Ricorda! 60 update() per secondo => 60*2 sono due secondi
	
	public Enemy(Map<Integer, List<WritableImage>> enemyImgs, float x, float y, int ID, int enemyType, EnemyManager enemyManager) {
		this.enemyImgs = Map.copyOf(enemyImgs);
		
		this.x = x;
		this.y = y;
		this.ID = ID;
		this.enemyType = enemyType;
		this.enemyManager = enemyManager;
		bounds = new Rectangle((int) x, (int) y, 32, 32);
		lastDir = -1;//in questo modo spawnano nella posizione corretta(tutte le costanti Directions (in helpz) sono diverse da -1)
		lastXDir = Constants.Direction.LEFT;
		setStartHealth();
	}
	
	private void setStartHealth() {
		health = Enemies.GetStartHealth(enemyType);
		maxHealth = health;
	}
	
	public void update() {
		updateTick();
	}
	
	/**
	 * controlla FPS delle animazioni e ciclo delle immagini per animare
	 */
	private void updateTick() {
		animationTick++;
		if(animationTick >= ANIMATION_SPEED) {
			animationTick = 0;
			animationIndex++;
			//le immagini che compongono l' animazione sono 4
			if (animationIndex >= maxAnimationIndex)
				animationIndex = 0;
		}
	}
	
	public void draw(GraphicsContext gc) {
		gc.drawImage(enemyImgs.get(leftOrRight()).get(animationIndex), x, y);
	}
	
	/**
	 * restituisce l' intero corrispondente all' indice della mappa che contiene l'animazione orientata nel verso corretto ( 0 sinistra, 1 destra)
	 * @return
	 */
	private int leftOrRight() {
		if(lastXDir == Direction.LEFT)
			return 0;
		else 
			return 1;
	}
	/**
	 * riduce la vita del nemico del danno indicato. Se la vita <= 0, il nemico non è più vivo(alive = false) e assegna l'importo della moneta di gioco assegnato a quel tipo di nemico
	 * @param dmg
	 */
	public void hurt(int dmg) {
		this.health -= dmg;
		
		if(health <= 0) {
			alive = false;
			enemyManager.rewardPlayer(enemyType);
			
		}
	}
	
	/**
	 * utile per uccidere i nemici, quando per esempio raggiungono la fine
	 */
	public void kill() {
		// Is for killing enemy, when it reaches the end.
		//inserire rewardPlayer() ?
		alive = false;
		health = 0;
	}
	
	/**
	 * assegna a slowTick=0, facendo partire il conto dello slow
	 */
	public void slow() {
		slowTick = 0;
	}
	
	/**
	 * muove l'immaggine lavorando sulle coordinate X e Y. (LEFT => x -= speed / DOWN => y += speed)
	 * @param speed
	 * @param dir
	 */
	public void move(float speed, int dir) {
		lastDir = dir;
		//se il nemico è rallentato, riduci la velocità
		if (slowTick < slowTickLimit) {
			slowTick++;
			speed *= 0.5f;
		}
		
		switch (dir) {
		case Direction.LEFT:
			this.x -= speed;
			lastXDir = Direction.LEFT;
			break;
		case Direction.UP:
			this.y -= speed;
			break;
		case Direction.RIGHT:
			this.x += speed;
			lastXDir = Direction.RIGHT;
			break;
		case Direction.DOWN:
			this.y += speed;
			break;
		}
		
		updateHitbox();
	}
	
	/**
	 * il danno viene gestito dal rettangolo che contiene il nemico, non dalla posizione dello sprite
	 */
	private void updateHitbox() {
		bounds.x = (int) x;
		bounds.y = (int) y;
	}
	
	/**
	 * Non usare questa per muovere il nemico
	 * @param x
	 * @param y
	 */
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * restituisce la vita attuale/la vita massima (serve per la barra della vita) ep.21 Combat part 2
	 * @return
	 */
	public float getHealthBarFloat() {
		return health / (float) maxHealth;
	}
	

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public int getHealth() {
		return health;
	}

	public int getID() {
		return ID;
	}

	public int getEnemyType() {
		return enemyType;
	}
	
	public int getLastDir() {
		return lastDir;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public boolean isSlowed() {
		return slowTick < slowTickLimit;
	}
}
