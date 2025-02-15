package managers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import comparator.EnemyPosComparator;
import enemies.Enemy;
import game_scenes_logic.Playing;
import helpz.Constants;
import helpz.LoadSave;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import towers.Tower;

public class TowerManager {
	private Playing playing;
	
	private List<Tower> towers = new ArrayList<>();	//lista delle torri della partita
	private int towerAmount = 0;

	//towerImgs.get(towerType).get(towerTier).get(orientation).get(orientationIndex) ; arrivato a questo punto ottieni l'array che contiene l'orientazione sinistra dell'animazione
	private List<Map<Integer, Map<Integer, List<WritableImage>>>> towersImgs = new ArrayList<>(); //in base al tipo di torre selezionamo la Mappa, in base al tier selezionamo la mappa con gli l'array di immagini orientati a sinistra e destra per le animazioni   

	//l'operazione di sorting serve per far sparare le torri al nemico più avanti rispetto agli altri (altrimenti verrebbero sparati in base all' ordine di spawning)
	private int SORTING_FREQUENCY = 30;	//60 è un secondo
	private int sortTick;
	private List<Enemy> enemyList = new ArrayList<>();//arrayList contenente i nemici dell'ondata, in modo da poterli riordinare senza intaccare altri Thread e causare errori di accesso

	
	public TowerManager(Playing playing) {
		this.playing = playing;
		loadTowersImgs();//aggiusta nel playingController, metti la mappa al posto degli array
	}
	/*
	private void loadTowerImgs() {
		BufferedImage atlas = LoadSave.getFileSprite("spriteatlas.png");
		towersImgs = new ArrayList<Map<Integer, Map<Integer, List<WritableImage>>>>();
				
		for(int i = 0; i<3; i++) {
			Map<Integer, Map<Integer, List<WritableImage>>> tierMap = new HashMap<Integer, Map<Integer, List<WritableImage>>>();
			
			for(int j = 0; j<3; j ++) {
				Map<Integer, List<WritableImage>> orientationMap = new HashMap<Integer, List<WritableImage>>();
			
				for(int z = 0; z<2; z++) {
					List<WritableImage> orientedAnimationList = new ArrayList<WritableImage>();
					orientationMap.put(z, orientedAnimationList);
				}
				
				tierMap.put(i, orientationMap);
			}

			towersImgs.add(tierMap);
		}
		//!!!!!!!!!!!!!!!! LA POSIZIONE 0 NELLA MAPPA DELLE ORIENTAZIONI deve contenete l'orientazione sinistra
		//archerTower
		for(int i = 0; i < 7; i++) {
			towersImgs.get(0).get(0).get(0).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_archer_2.1_toLeft" + i + ".png"), null));
			towersImgs.get(0).get(0).get(1).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_archer_2.1_toRight" + i + ".png"), null));
		}
		for(int i = 6; i>4; i--) {
			towersImgs.get(0).get(0).get(0).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_archer_2.1_toLeft" + i + ".png"), null));
			towersImgs.get(0).get(0).get(1).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_archer_2.1_toRight" + i + ".png"), null));
		}
		
		//bombTower
		for(int i = 0; i < 7; i++) {
			towersImgs.get(1).get(0).get(0).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_bomb_1.0_toLeft" + i + ".png"), null));
			towersImgs.get(1).get(0).get(1).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_bomb_1.0_toLeft" + i + ".png"), null));
		}
		for(int i = 6; i>4; i--) {
			towersImgs.get(1).get(0).get(0).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_archer_1.0_toLeft" + i + ".png"), null));
			towersImgs.get(1).get(0).get(1).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_bomb_1.0_toLeft" + i + ".png"), null));
		}
		
		//wizardTower
		for(int i = 0; i < 7; i++) {
			towersImgs.get(2).get(0).get(0).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_bomb_1.0_toLeft" + i + ".png"), null));
			towersImgs.get(2).get(0).get(1).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_bomb_1.0_toLeft" + i + ".png"), null));
		}
		for(int i = 6; i>4; i--) {
			towersImgs.get(2).get(0).get(0).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_archer_1.0_toLeft" + i + ".png"), null));
			towersImgs.get(2).get(0).get(1).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("tower_bomb_1.0_toLeft" + i + ".png"), null));
		}
	}*/
	
	private void loadTowersImgs() {
		//per ogni torre c'è una mappa
		for(int i = 0; i<3; i++) {
			Map<Integer, Map<Integer, List<WritableImage>>> tierMap = new HashMap<Integer, Map<Integer, List<WritableImage>>>();

			for(int j = 0; j<3; j ++) {
				Map<Integer, List<WritableImage>> orientationMap = new HashMap<Integer, List<WritableImage>>();

				for(int z = 0; z<2; z++) {
					List<WritableImage> orientedAnimationList = new ArrayList<WritableImage>();
					orientationMap.put(z, orientedAnimationList);
				}

				tierMap.put(j, orientationMap);
			}

			towersImgs.add(tierMap);
		}

		
		loadTowerTypeImgs("tower_archer_2.0", Constants.Towers.ARCHER, 1, 6);
		loadTowerTypeImgs("tower_bomb_1.0", Constants.Towers.CANNON, 1, 6);
		
		towersImgs.get(Constants.Towers.WIZARD).get(0).get(0).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("spriteatlas.png").getSubimage(6*32, 1*32, 32, 32 ), null));
		towersImgs.get(Constants.Towers.WIZARD).get(0).get(1).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite("spriteatlas.png").getSubimage(6*32, 1*32, 32, 32 ), null));

		//Da usare per quando anche il mago avrà due direzioni
		//loadTowerTypeImgs("nome_torre", Constants.Towers.WIZARD, 1, 1); 
		
	}
	
	/**
	 * salva l'animazione della torre per le orientazioni sinistra e destra. !!!!formato del nome del file deve essere per esempio: tower_towerType_numeroVersione . La funzione lo completa con : tower_towerType__1.0__1_toLeft2.png ; dove 1.0 è la versione della torre, 1 è il tier e 2 è un frame dell'animazioe
	 * @param towerFileName	nome del file immagine del nemico
	 * @param towerType	tipo di nemico, per sapere in quale mappa salvare le immagini
	 * @param animationSize	grandezza dell'animazione, per sapere quante immagini aggiungere nella lista dell'animazione
	 */
	private void loadTowerTypeImgs(String towerFileName, int towerType, int tier, int animationSize) {
		tier -= 1;//il tier nelle immagini va da 0 a 2, non da 1 a 3
		for(int i = 0; i < animationSize; i++) {
		/*
			Map<Integer, Map<Integer, List<WritableImage>>> tierMap = towersImgs.get(towerType);
			Map<Integer, List<WritableImage>> orientationMap = tierMap.get(tier);
			List<WritableImage> orientedAnimationList = orientationMap.get(0);*/
			//orientedAnimationList.add(SwingFXUtils.toFXImage(LoadSave.getFileSprite(towerFileName + /*"__" + tier +*/ "_toLeft" + (i+1) + ".png"), null));
		
			towersImgs.get(towerType).get(tier).get(0).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite(towerFileName + /*"__" + tier +*/ "_toLeft" + (i+1) + ".png"), null));
		}
		
		for(int i = 0; i < animationSize; i++) {
			towersImgs.get(towerType).get(tier).get(1).add(SwingFXUtils.toFXImage(LoadSave.getFileSprite(towerFileName + /*"__" + tier +*/ "_toRight" + (i+1) + ".png"), null));
		}
	}
	
	/**
	 * utilizza l'indice della torre passata per cercarla nell'array delle torri e rimuoverla
	 * @param displayedTower
	 */
	public void removeTower(Tower displayedTower) {
		for (int i = 0; i < towers.size(); i++)
			if (towers.get(i).getId() == displayedTower.getId()) {
				towers.remove(i);
				towerAmount--;
			}
	}
	
	/**
	 * utilizza l'indice della torre passata per cercarla nell'array delle torri e rimuoverla
	 * @param displayedTower
	 */
	public void upgradeTower(Tower displayedTower) {
		for(Tower t : towers)
			if(t.getId() == displayedTower.getId()) {
				t.upgradeTower();
				t.setTierMapImgs(towersImgs.get(t.getTowerType()).get(t.getTier()));
			}
	}
	
	public void addTower(Tower selectedTower, int xPos, int yPos) {
		int towerType = selectedTower.getTowerType();
		//System.out.println("addTower : " + towerType + " //// ");
		
		//quando aggiungi una torre, il tier è 1(ovvero la posizione zero della mappa)
		towers.add(new Tower(  towersImgs.get(towerType).get(0) ,xPos, yPos, towerAmount++, towerType));
	}

	/**
	 * per ogni torre, attacca se l' intervallo tra un attacco e l' altro è scaduto
	 */
	public void update() {	
		for (Tower t : towers) {
			t.update();
			attackEnemyIfClose(t);
		}

		sortTick++;
		if(sortTick >= SORTING_FREQUENCY) {
			sortTick=0;
			enemyList.clear();
			enemyList.addAll(playing.getEnemyManager().getEnemies());
			Collections.sort(enemyList, new EnemyPosComparator());
		}
	}

	/**
	 * controlla per ogni torre se un nemico è nel suo range e lo attacca
	 */
	private void attackEnemyIfClose(Tower t) {
		//controlla per ogni nemico prima se è vivo, se si controlla che sia in range, se si controlla che il cooldown è terminato, se si spara e resetta il cooldown	 
		for (Enemy e : enemyList) {
			if (e.isAlive()) {
				if (isEnemyInRange(t, e)) {
					//se non è partito nessun attacco e se non c'è nessun cooldown da attendere dopo un precedente attacco, può partire l'animazione dell'attacco
					if (!t.isAnimationStarted() && !t.isCooldownStarted()) {
						//if( e.getFutureDamage() < e.getMaxHealth()) {
							t.setAnimationStarted(true);
							t.setEnemyToShoot(e);
							//e.updateFutureDamage(t.getDmg());
						return;
					}
					//se c'è un animazione in corso , controlla se il nemico è ancora in range, se si spara, se no cambia il nemico finchè non ne trova uno in range
					else {
						if(t.isShooting()) {
							if(!isEnemyInRange(t, t.getEnemyToShoot())) {
								//ti giri tutti i nemici finchè non trovi uno in range e lo setti come nemico da sparare
								Iterator<Enemy> iterator = enemyList.iterator();
								while (iterator.hasNext()) {
									Enemy enemy = iterator.next();
									if (isEnemyInRange(t, enemy)) {
										t.setEnemyToShoot(enemy);
										break;
									}
								}
							}
							playing.shootEnemy(t, t.getEnemyToShoot());
							t.setShooting(false);
						}
					}
				}
			}
		}
	}
	

	/**
	 * restituisce true se un nemico è nel range della torre
	 * @param t
	 * @param e
	 * @return
	 */
	private boolean isEnemyInRange(Tower t, Enemy e) {
		int range = helpz.Utilz.GetHypoDistance(t.getX(), t.getY(), e.getX(), e.getY());
		return range < t.getRange();
	}
	
	public void draw(GraphicsContext gc) {
		for(Tower t : towers) {
			t.draw(gc);
		}
	}
	
	/**
	 * se nelle coordinate specificate c'è già una torre, restituisce null, altrimenti restituisce la torre
	 * @param x
	 * @param y
	 * @return
	 */
	public Tower getTowerAt(int x, int y) {
		for (Tower t : towers)
			if (t.getX() == x)
				if (t.getY() == y)
					return t;
		return null;
	}
	
	/**
	 * pulisce l'array delle torri e ne resetta il numero (towerAmount)
	 */
	public void reset() {
		towers.clear();
		towerAmount = 0;
	}
	
	public int getTowerAmount() {
		return towerAmount;
	}
	
	public List<Tower> getTowers(){
		return towers;
	}
	
	/**
	 * utilizzo tipo : towerImgs.get(towerTpe).get(towerTier).get(orientation)
	 * @return	una lista contente per ogni torre una mappa contente per ogni tier una mappa contente le due orientazioni delle animazioni della torre
	 */
	public List<Map<Integer, Map<Integer, List<WritableImage>>>> getTowersImgs(){
		return towersImgs;
	}
	
	
	
	
	
}