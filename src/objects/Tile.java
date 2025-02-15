package objects;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.WritableImage;

public class Tile {
	private List<WritableImage> sprites = new ArrayList<>();
	private int id, tileType;
	
	public Tile(List<WritableImage> sprites, int id, int tileType ) {
		this.sprites = sprites;
		this.id = id;
		this.tileType = tileType;
	}
	
	public int getTileType() {
		return tileType;
	}
	
	/**
	 * restituisce lo sprite dell' indice indicato per gestire le animazioni.
	 * Se la Tile non è animata, bisogna passare come indice 0 
	 * @param animationIndex
	 * @return
	 */
	public WritableImage getSprite(int animationIndex) {
		return sprites.get(animationIndex);
		
		/*potresti in futuro pensare di automatizzare la scelta tra Tile animata e Tile statica facendo : 
		 * 	if(sprites.size() > 1)
		 * 		return sprites.get(animationIndex);
		 * 	else
		 * 		return sprites.get(0);
		 * */
	}
	
	/**
	 * versione base per recuperare lo sprite
	 * @return
	 */
	public WritableImage getSprite() {
		return sprites.get(0);
	}
	
	/**
	 * se l' array di sprite contiene più di un immagine, viene considerata una Tile animata
	 * @return
	 */
	public boolean isAnimation() {
		return sprites.size() > 1;
	}
	
	public int getId() {
		return id;
	}
	
	
	public List<WritableImage> getSpritesList(){
		return this.sprites;
	}
}
