package managers;

import java.awt.geom.Point2D;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import enemies.Enemy;
import game_scenes_logic.Playing;
import helpz.Constants;
import helpz.Constants.Projectiles;
import helpz.Constants.Towers;
import helpz.LoadSave;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import objects.Projectile;
import towers.Tower;

public class ProjectileManager {
	private Playing playing;
	private List<Projectile> projectiles = new ArrayList<>(); //contiene tutti i proiettili creati durante la partita
	private List<Explosion> explosions = new ArrayList<>();//contiene tutte le esplosioni create durante la partita
	
	private List<WritableImage> proj_imgs = new ArrayList<>();
	private List<WritableImage> explo_imgs = new ArrayList<>();
	private int proj_id = 0;

	public ProjectileManager(Playing playing) {
		this.playing = playing;
		importImgs();
	}
	
	private void importImgs() {
		BufferedImage atlas = LoadSave.getFileSprite("spriteatlas.png");
		
		for (int i = 0; i < 3; i++)
			proj_imgs.add(SwingFXUtils.toFXImage(atlas.getSubimage((7 + i) * 32, 32, 32, 32), null));
		
		importExplosion(atlas);
		
	}
	
	private void importExplosion(BufferedImage atlas) {
		for (int i = 0; i < 7; i++)
			explo_imgs.add(SwingFXUtils.toFXImage(atlas.getSubimage(i * 32, 32 * 2, 32, 32), null));
	}
	
	public void newProjectile(Tower t, Enemy e) {
			int type = getProjType(t);

			//distanza della torre dal nemico per le coordinate X e Y
			int xDist = (int) (t.getX() - e.getX());
			int yDist = (int) (t.getY() - e.getY());

			float rotate = 0;

			//in caso di bombe o incantesimi, non è necessario ruotare l' immagine del proiettile (quindi rotate=0)
			if(type == Projectiles.ARROW) {
				//calcoliamo l' angolo di rotazione sfruttando l'arcotangente ( il rapporto tra il lato Y e X che formano il triangolo rettangolo (l'ipotenusa è la distanza tra il nemico e la torre, i cateti sono 'lasse X e Y))
				float arcValue = (float) Math.atan(yDist / (float) xDist);
				rotate = (float) Math.toDegrees(arcValue);

				//siccome arcotangente restituisce i valori solo tra -pigreco/2 pigreco/2, per ottenere la rotazione completa dobbiamo sommare pigreco, in modo da prendere tutti i valori possibili dell' angolo e non solo da -90 a 90
				if(xDist < 0)
					rotate += 180;
			}
			projectiles.add(new Projectile(t.getX() , t.getY() , t.getDmg(), rotate, proj_id++, type, e, t.getRange()));
	}
	
	public void update() {
		//per ogni proiettile, se il proiettile è attivo, lo muove, e se colpisce un nemico, gli assegna danno e disattiva il proiettile, se è una bomba, fa partire l' esplosione
		for (Projectile p : projectiles)
			if (p.isActive()) {
				p.move();
				if (isProjHittingEnemy(p)) {//attento, qui probabilmente stai facendo due volte danno se è una bomba (il nemico colpito più il danno da esplosione)
					p.setActive(false);
					if (p.getProjectileType() == Projectiles.BOMB) {
						explosions.add(new Explosion(new Point2D.Float( p.getEnemy().getX()+16, p.getEnemy().getY()+16/*p.getPos()*/)));
						explodeOnEnemies(p);
					}
				} else if (isProjOutsideBounds(p)) {
					//se il proiettile esce dalla finestra di gioco, lo setta come inattivo, evitando di sprecare risorse
					p.setActive(false);
					//p.getEnemy().setDrawRedBounds(false);
				}
			}
		//gestisce l' animazione delle esplosioni
		for (Explosion e : explosions)
			if (e.getIndex() < 7)
				e.update();

	}
	
	/**
	 * assegna danno nell' area in cui le bombe esplodono
	 * @param p
	 */
	private void explodeOnEnemies(Projectile p) {
		//per ogni nemico, controlla se è vivo, se si controlla che sia nel raggio di azione della bomba e gli assegna danno
		for (Enemy e : playing.getEnemyManager().getEnemies()) {
			if (e.isAlive()) {
				float radius = 40.0f;

				float xDist = Math.abs(p.getPos().x - e.getX());
				float yDist = Math.abs(p.getPos().y - e.getY());

				float realDist = (float) Math.hypot(xDist, yDist);

				if (realDist <= radius)
					e.hurt(p.getDmg());
			}
		}
	}

	/**
	 * restituisce true se il proiettile colpisce un nemico
	 * @param p
	 * @return
	 */
	private boolean isProjHittingEnemy(Projectile p) {
		//per ogni nemico, controlla se le coordinate del proiettile combaciano con le sue, se si assegna danno
		for (Enemy e : playing.getEnemyManager().getEnemies()) {
			if (e.getBounds().contains(p.getPos()) && e.getID() == p.getEnemy().getID()) {
				e.hurt(p.getDmg());
				if(p.getProjectileType() == Projectiles.CHAINS)
					e.slow();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * controlla che il proiettile sia all' interno della finestra, in modo da non sprecare risorse all'infinito se esce da quest'ultima (anche se non è nella finestra tutti i calcoli e i controlli vengono ancora effettuati su quei proiettili, perchè sono considerati ancora attivi)
	 * @param p
	 * @return
	 */
	private boolean isProjOutsideBounds(Projectile p) {
		if (p.getPos().x >= 0)
			if (p.getPos().x <= Constants.LVL_LENGTH * Constants.TILE_DIMENSION)
				if (p.getPos().y >= 0)
					if (p.getPos().y <= Constants.LVL_ROWS * Constants.TILE_DIMENSION)
						return false;
		return true;
	}
	
	public void draw(GraphicsContext gc) {
	    for (Projectile p : projectiles) {
	        if (p.isActive()) {
	            if (p.getProjectileType() == Projectiles.ARROW) {
	                // Sposta l'intera parte grafica (GraphicsContext) centrando con il proiettile in modo da poter ruotare il proiettile nel punto corretto (come col pivot point)
	                gc.save(); // Salva la trasformazione corrente
	                gc.translate(p.getPos().x, p.getPos().y);
	                //gc.rotate(Math.toRadians(p.getRotation())); //non funge convertendo in 
	                gc.rotate(p.getRotation());

	                // Il centro del proiettile viene spostato nell'angolo in alto a sinistra (x = -16 e y = -16)
	                gc.drawImage(proj_imgs.get(p.getProjectileType()), -16, -16);
	                // Riporta il contesto grafico allo stato di partenza dopo aver mosso il proiettile
	                gc.restore(); // Ripristina la trasformazione corrente
	            } else {
	                // I -16 alla X e Y servono per centrare l'immagine nell'angolo in alto a sinistra, facendo spawnare il proiettile nel punto corretto
	                gc.drawImage(proj_imgs.get(p.getProjectileType()), p.getPos().x - 16, p.getPos().y - 16);
	            }
	        }
	    }
	    drawExplosions(gc);
	}
	
	/**
	 * disegna l'animazione dell' esplosione nel punto corretto(in cui esplode)
	 * @param g2d
	 */
	private void drawExplosions(GraphicsContext gc) {
	    for (Explosion e : explosions) {
	        if (e.getIndex() < 7) {
	            // I -16 alla X e Y servono a centrare l'origine dell'esplosione
	            gc.drawImage(explo_imgs.get(e.getIndex()), e.getPos().x - 16, e.getPos().y - 16);
	        }
	    }
	}
	
	/**
	 * restituisce il tipo di proiettile in base al tipo di torre
	 */
	private int getProjType(Tower t) {
		switch (t.getTowerType()) {
		case Towers.ARCHER:
			return Projectiles.ARROW;
		case Towers.CANNON:
			return Projectiles.BOMB;
		case Towers.WIZARD:
			return Projectiles.CHAINS;
		}
		return 0;
	}
	
	private class Explosion {

		private Point2D.Float pos;
		private int exploTick, exploIndex;//exploTick gestisce la velocità dell' animazione, exloIndex l' immagine da disegnare in quell'istante dell' animazione

		public Explosion(Point2D.Float pos) {
			this.pos = pos;
		}

		public void update() {
			exploTick++;
			//ogni volta che tick=7 azzera il counter e cambia l'immagine dell' animazione con quella successiva
			if (exploTick >= 7) {
				exploTick = 0;
				exploIndex++;
			}
		}

		public int getIndex() {
			return exploIndex;
		}

		public Point2D.Float getPos() {
			return pos;
		}
	}
	
	/**
	 * resetta l'array dei proiettili e delle esplosioni, così come l'indice dei proiettili
	 */
	public void reset() {
		projectiles.clear();
		explosions.clear();

		proj_id = 0;
	}
	
	public List<Projectile> getProjectileList(){
		return this.projectiles;
	}
	
}
