package helpz;

public class Constants {
	public static final int WAVE_SEPARATOR = -3;//-2 e -1 sono dei PathPoint
	public static final int TILE_DIMENSION = 32;
	public static final int LVL_ROWS = 20; //la Y della matrice
	public static final int LVL_COLUMNS = 48; //la X della matrice
	public static final int LVL_LENGTH = LVL_ROWS * LVL_COLUMNS; // la lunghezza dell'array unidimensionale, contenente l'id di ogni Tile
	
	
	public static class ScenesName {
		public static final int MENU = 0;
		public static final int PLAYING = 1;
		public static final int EDITING = 2;
		public static final int SETTINGS = 3;
	}

	public static class ScenesPath {
		//public static final String packageName = "/scenes_controller2";

		public static final String MENU = "Menu.fxml";
		public static final String PLAYING = "Playing.fxml";
		public static final String EDITING = "Editing.fxml";
		public static final String SETTINGS = "Settings.fxml";
	}
	
	public static class Projectiles {
		public static final int ARROW = 0;
		public static final int CHAINS = 1;
		public static final int BOMB = 2;
		
		public static float GetSpeed(int type) {
			switch (type) {
			case ARROW:
				return 8f;
			case BOMB:
				return 4f;
			case CHAINS:
				return 6f;
			}
			return 0f;
		}
	}
	
	public static class Towers {
		public static final float UPGRADE_COST = 1.5f;	//parametro da moltiplicare al costo della torre per ottenere il costo completo della torre
		
		public static final int ARCHER = 0;
		public static final int CANNON = 1;
		public static final int WIZARD = 2;
	
		public static int GetTowerCost(int towerType) {
			switch (towerType) {
			case CANNON:
				return 65;
			case ARCHER:
				return 30;
			case WIZARD:
				return 45;
			}
			return 0;
		}
		
		public static String GetName(int towerType) {
			switch (towerType) {
			case CANNON:
				return "Cannon";
			case ARCHER:
				return "Archer";
			case WIZARD:
				return "Wizard";
			}
			return "";
		}
		
		public static int GetStartDmg(int towerType) {
			switch (towerType) {
			case CANNON:
				return 50;
			case ARCHER:
				return 5;
			case WIZARD:
				return 0;
			}

			return 0;
		}

		public static float GetDefaultRange(int towerType) {
			switch (towerType) {
			case CANNON:
				return 100;
			case ARCHER:
				return 100;
			case WIZARD:
				return 100;
			}

			return 0;
		}

		public static int GetDefaultCooldown(int towerType) {
			switch (towerType) {
			case CANNON:
				return 90;
			case ARCHER://durata dell' animazione in tick / 3
				return 80;
			case WIZARD:
				return 40;
			}

			return 0;
		}
		
		public static int GetDefaultAnimationSpeed(int towerType) {
			switch (towerType) {
			case CANNON:
				return 10;
			case ARCHER://durata dell' animazione in tick / 3
				return 6;
			case WIZARD:
				return 40;
			}

			return 0;
		}
	}

	public static class Enemies{
		public static final int ORC = 0;
		public static final int BAT = 1;
		public static final int KNIGHT = 2;
		public static final int WOLF = 3;
		public static final int BASKET = 4;
		
		public static int GetReward(int enemyType) {
			switch (enemyType) {
			case ORC:
				return 5;
			case BAT:
				return 5;
			case KNIGHT:
				return 25;
			case WOLF:
				return 10;
			case BASKET:
				return 5;
			}
			return 0;
		}
		
		public static float GetSpeed(int enemyType) {
			switch (enemyType) {
			case ORC:
				return 0.5f;
			case BAT:
				return 0.65f;
			case KNIGHT:
				return 0.3f;
			case WOLF:
				return 0.75f;
			case BASKET:
				return 0.5f;
			}
			return 0;
		}
		
		public static int GetStartHealth(int enemyType) {
			switch (enemyType) {
			case ORC:
				return 100;
			case BAT:
				return 60;
			case KNIGHT:
				return 250;
			case WOLF:
				return 85;
			case BASKET:
				return 150;
			}
			return 0;
		}
	}
	
	public static class Direction {
		public static final int LEFT = 0;
		public static final int UP = 1;
		public static final int RIGHT = 2;
		public static final int DOWN = 3;
	}

	public static class Tiles{
		public static final int WATER_TILE = 0;
		public static final int GRASS_TILE = 1;
		public static final int ROAD_TILE = 2;
		public static final int ALBERELLO_TILE = 3;
		public static final int BRIDGE_TILE = 4;
	}

	/**
	 * id delle Tile del PathPoints
	 */
	public static class PathPoints {
		public static final int PATHPOINT_START_ID = -1;
		public static final int PATHPOINT_END_ID= -2;

		public static final int PATHPOINT_START_FILE_X_POSITION = LVL_LENGTH ;
		public static final int PATHPOINT_START_FILE_Y_POSITION = LVL_LENGTH + 1;
		public static final int PATHPOINT_END_FILE_X_POSITION = LVL_LENGTH + 2;
		public static final int PATHPOINT_END_FILE_Y_POSITION = LVL_LENGTH + 3;
	}
}
