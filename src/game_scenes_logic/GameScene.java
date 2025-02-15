package game_scenes_logic;

import java.util.List;

import events.Wave;
import game.Game;
import helpz.LoadSave;
import javafx.scene.image.WritableImage;
import objects.PathPoint;

public class GameScene {
	//Canvas e GraphicContext permettono di gestire il disegno del gioco
		boolean drawSelect = false;	//capisci cosa fa
		private int mouseX, mouseY;	//posizione del mouse per gestire input ecc.
		
		//protected PathPoint pathStart, pathEnd;	//ogni livello ha una posizione di start e di end per i nemici
		
		//sezione che gestisce le animazioni presenti in qualunque parte del gioco (acqua ed alberi si trovano sia in editing che in playing)
		protected Game game;
		protected int waterAnimationIndex, otherAnimationIndex, maxWaterAnimationIndex = 4, maxOtherAnimationIndex = 6;
		private final int WATER_ANIMATION_SPEED = 25;
		private final int OTHER_ANIMATION_SPEED = 100;
		private int waterTick, otherTick;
		
		public GameScene(Game game) {
	        this.game = game;
		}
		
		public void saveLevel(String name, int[][] lvl, PathPoint pathStartToSave, PathPoint pathEndToSave, List<Wave> waves) {
			if(name == null)
				name = "default_level";
			LoadSave.SaveLevel(name, lvl, pathStartToSave, pathEndToSave, waves);
		}
		
		public void updateTick() {
			waterTick++;

			if(waterTick >= WATER_ANIMATION_SPEED) {
				waterTick = 0;
				waterAnimationIndex++;
				//le immagini che compongono l' animazione sono 10
				if (waterAnimationIndex >= maxWaterAnimationIndex)
					waterAnimationIndex = 0;
			}
			
			otherTick++;
			if(otherTick >= OTHER_ANIMATION_SPEED) {
				otherTick = 0;
				otherAnimationIndex++;

				if(otherAnimationIndex >= maxOtherAnimationIndex)
					otherAnimationIndex = 0;
			}
		}
		
		/**
		 * recupera la Tile dal TileManager tramite l'id
		 * @param spriteID
		 * @return
		 */
		public WritableImage getSprite(int spriteID) {
			return game.getTileManager().getSprite(spriteID);
		}
		
		/**
		 * recupera la Tile dal TileManager tramite l'id e in modo progressivo in modo da formare l' animazione
		 * @param spriteID ID della Tile
		 * @param animationIndex Progressivo per l'animazione
		 * @return
		 */
		public WritableImage getSprite(int spriteID, int animationIndex) {	
			return game.getTileManager().getAnimationSprite(spriteID,animationIndex);
		}
		
		public int getWaterAnimationIndex() {
			return waterAnimationIndex;
		}
		
		public int getOtherAnimationIndex() {
			return otherAnimationIndex;
		}
		
		public int getMouseX() {
			return mouseX;
		}
		
		public int getMouseY() {
			return mouseY;
		}
		
		public void setMouseX(int x) {
			this.mouseX = x;
		}
		
		public void setMouseY(int y) {
			this.mouseY = y;
		}
		
}
