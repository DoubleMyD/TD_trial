package towers;

import java.awt.geom.Point2D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enemies.Enemy;
import helpz.Constants;
import helpz.Constants.Towers;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

public class Tower {
	private int TOWER_ANIMATION_SPEED = 10; //velocità dell' animazione (può essere modificato per permettere di animare con ease-in / ease-out)
	private int animationTick, animationIndex;	//contatori per la velocità dell' animazione (quando raggiunge un certo numero di tick, si passa all'immagine successiva)
	private int maxAnimationIndex;	//utile per settare automaticamente da quante immagini è composta l'animazione
	private Point2D.Float drawPos;	//dato che le immagini sono disegnate partendo dall' angolo in alto a sinistra, sono spostate dal loro centro effettivo. In questo modo separiamo il disegno e la posizione
	
	private Map<Integer, List<WritableImage>> towerImgs = new HashMap<>();	//Mappa contenente nella posizione 0 l'immagine orientata verso sinistra, in posizione 1 l'immagine orientata verso destra
	
	private int x, y, id, towerType, dmg, tier;	//posizione, id, tipo di torre, il danno della torre, il tier
	private float range; //range della torre
	private int cdTick;	//contatore per il cooldown della torre
	private float cooldown;	// tempo di ricarica della torre tra un proiettile e l' altro

	private boolean startAnimation;	//se true, inizia l'animazione, terminata l'animazione viene impostata su false e l'animazione è conclusa
	private boolean shoot;	// unico frame in cui la torre può sparare (altrimenti sparerebbe più volte di quanto previsto)
	private boolean startCooldown;	//dopo aver sparato parte il cooldwon tra un proiettile e l'altro
	
	private Enemy enemyToShoot;	//nemico da sparare
	
	/**
	 * Gli attributi x e y sono la posizione centrale della torre (da cui far partire i calcoli di range ecc.) e non dell' immagine, che invece è spostata verso l'alto e a sinistra della Tile_Dimension per centrare l'angolo in alto a sinistra della mappa
	 * @param imgs Mappa con le immagini orientate a sinistra (Integer = 0) e a destra (Integer = 1)
	 * @param x	Posizione x centrale della torre
	 * @param y Posizione y centrale della torre
	 * @param id	Id della torre
	 * @param towerType	Tipo della torre
	 * @param projectileManager	Manager dei proiettili per consentire alla torre di sparare
	 */
	public Tower(Map<Integer, List<WritableImage>> imgs, int x, int y, int id, int towerType ) {
		this.towerImgs = Map.copyOf(imgs);
		this.maxAnimationIndex = imgs.get(0).size();
		this.x = x;
		this.y = y;
		this.drawPos = new Point2D.Float(this.x /*- Constants.TILE_DIMENSION/2*/, y /*this.y - Constants.TILE_DIMENSION/2*/);
		
		this.id = id;
		this.towerType = towerType;
		this.tier = 1;
		setDefaultDmg();
		setDefaultRange();
		setDefaultCooldown();
		
		TOWER_ANIMATION_SPEED = Towers.GetDefaultAnimationSpeed(towerType);
		cooldown += TOWER_ANIMATION_SPEED ;
	}

	public void update(){
		if(startCooldown) {
			updateCooldownTick();
		}
		else {
			if(startAnimation) {
				updateAnimationTick();
			}
		}
	}

	public void updateAnimationTick() {
		animationTick++;
		System.out.println(animationTick);
		if(animationTick >= TOWER_ANIMATION_SPEED) {

			animationTick = 0;
			animationIndex++;
			if(animationIndex == 5) {
				shoot = true;
				//projectileManager.newProjectile(this, enemyToShoot);
			}

			if(animationIndex == 1)// in questo modo per l' animazione 0,1,2 il tickLimit è 75, per 3,4,5 è 45
				TOWER_ANIMATION_SPEED = 20;
			else if(animationIndex == 3){
				TOWER_ANIMATION_SPEED = 10;
			}

			if(animationIndex >= maxAnimationIndex) {
				animationIndex = 0;
				startAnimation = false;
				cdTick = 0;
				startCooldown = true;
			}
		}
		if(animationTick >= 1)
			shoot = false;
	}


	public void updateCooldownTick() {
		cdTick++;
		if(cdTick >= cooldown) {
			startCooldown = false;
			cdTick=0;
		}
	}
	
	
	/**
	 * modifica le stats della torre per i vari tier/upgrade, e aggiorna il tier
	 */
	public void upgradeTower() {
		this.tier++;

		switch (towerType) {
		case Towers.ARCHER:
			dmg += 2;
			range += 20;
			cooldown -= 5;
			break;
		case Towers.CANNON:
			dmg += 5;
			range += 20;
			cooldown -= 15;
			break;
		case Towers.WIZARD:
			range += 20;
			cooldown -= 10;
			break;
		}
	}
	
	public void draw(GraphicsContext gc) {
		if(startAnimation) {
			gc.drawImage(towerImgs.get(leftOrRight()).get(animationIndex), drawPos.x, drawPos.y);
		}
		else {
			gc.drawImage(towerImgs.get(0).get(0), drawPos.x, drawPos.y);
		}

	}
	
	/**
	 * restituisce l' intero corrispondente all' indice della mappa che contiene l'animazione orientata nel verso corretto
	 * @return
	 */
	private int leftOrRight() {
		if((enemyToShoot.getX() - x) > 0)
			return 0;
		else
			return 1;
	}
	
	private void setDefaultCooldown() {
		cooldown = Towers.GetDefaultCooldown(towerType);

	}

	private void setDefaultRange() {
		range = Towers.GetDefaultRange(towerType);

	}

	private void setDefaultDmg() {
		dmg = Towers.GetStartDmg(towerType);

	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTowerType() {
		return towerType;
	}

	public void setTowerType(int towerType) {
		this.towerType = towerType;
	}

	public int getDmg() {
		return dmg;
	}

	public float getRange() {
		return range;
	}
	
	public Boolean isAnimationStarted() {
		return this.startAnimation;
	}
	
	public void setAnimationStarted(Boolean x) {
		this.startAnimation = x;
	}
	
	public Boolean isCooldownStarted() {
		return this.startCooldown;
	}
	
	public void setEnemyToShoot(Enemy enemy) {
		this.enemyToShoot = enemy;
	}
	
	public Enemy getEnemyToShoot() {
		return enemyToShoot;
	}
	
	public void setShooting(Boolean x) {
		this.shoot = x;
	}
	
	public Boolean isShooting() {
		return shoot;
	}
	
	public void setTierMapImgs(Map<Integer, List<WritableImage>> tierImgs) {
		this.towerImgs = tierImgs;
	}
	
	public int getTier() {
		return tier;
	}
	
	
	
	
	
	
	
	
	
}