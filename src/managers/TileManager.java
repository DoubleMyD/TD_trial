package managers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import helpz.Constants;
import helpz.ImgFix;
import helpz.LoadSave;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import objects.Tile;
import static helpz.Constants.Tiles.*;

public class TileManager {
	
	private Tile GRASS;	//erba
	private Tile WATER;	//acqua
	private Tile ROAD_LR, ROAD_TB;	//strada dritta
	private Tile ROAD_B_TO_R, ROAD_L_TO_B, ROAD_L_TO_T, ROAD_T_TO_R;	//strada con curve
	private Tile BL_WATER_CORNER, TL_WATER_CORNER, TR_WATER_CORNER, BR_WATER_CORNER;	//spiaggia con angolo
	private Tile T_WATER, R_WATER, B_WATER, L_WATER;	//spiaggia
	private Tile TL_ISLE, TR_ISLE, BR_ISLE, BL_ISLE;	//isolotto per completare gli angoli delle spiagge
	private Tile ALBERELLO_STANDARD, ALBERELLO_3_RAMI;	//alberi
	private Tile L_BRIDGE_STANDARD, C_BRIDGE_STANDARD, R_BRIDGE_STANDARD;	//navata sinistra, centrale e destra del ponte
	
	private BufferedImage atlas;
	
	private List<Tile> tiles = new ArrayList<>();
	
	//S=Straight, C=Corner
	public List<Tile> grass = new ArrayList<>();
	public List<Tile> water = new ArrayList<>();
	private List<Tile> roadsS = new ArrayList<>();
	private List<Tile> roadsC = new ArrayList<>();
	private List<Tile> corners = new ArrayList<>();
	private List<Tile> beaches = new ArrayList<>();
	private List<Tile> islands = new ArrayList<>();
	private List<Tile> alberelli = new ArrayList<>();
	private List<Tile> bridges = new ArrayList<>();
	
	public TileManager() {
		loadAtlas();
		createTiles();
	}
	
	private void createTiles() {
		int id = 0;
		grass.add(GRASS = new Tile(Arrays.asList((SwingFXUtils.toFXImage(LoadSave.getFileSprite("grass_8.0.png"), null))), id++, GRASS_TILE));
		//tiles.add(GRASS = new Tile(Arrays.asList((SwingFXUtils.toFXImage(LoadSave.getFileSprite("grass_8.0.png"), null))), id++, GRASS_TILE));
		
		/*
		List<WritableImage> watersTemp = new ArrayList<WritableImage>();
		for(int i = 0; i < 4; i++) {
			watersTemp.add(getSprite(i,0));
		}
		tiles.add(WATER = new Tile(watersTemp, id++, WATER_TILE));
		*/
		
		water.add(WATER = new Tile(Arrays.asList( getSprite(0,0), getSprite(1,0), getSprite(2,0), getSprite(3,0)), id++, WATER_TILE));
		//tiles.add(WATER = new Tile(Arrays.asList(getSprite(0,0), getSprite(1,0), getSprite(2,0), getSprite(3,0)), id++, WATER_TILE));
		
		roadsS.add(ROAD_LR = new Tile(Arrays.asList(getSprite(8, 0)), id++, ROAD_TILE));
		roadsS.add(ROAD_TB = new Tile(ImgFix.getRotImg(Arrays.asList(getSprite(8, 0)), 90), id++, ROAD_TILE));
	
		roadsC.add(ROAD_B_TO_R = new Tile(Arrays.asList(getSprite(7, 0)), id++, ROAD_TILE));
		roadsC.add(ROAD_L_TO_B = new Tile(ImgFix.getRotImg(Arrays.asList(getSprite(7, 0)), 90), id++, ROAD_TILE));
		roadsC.add(ROAD_L_TO_T = new Tile(ImgFix.getRotImg(Arrays.asList(getSprite(7, 0)), 180), id++, ROAD_TILE));
		roadsC.add(ROAD_T_TO_R = new Tile(ImgFix.getRotImg(Arrays.asList(getSprite(7, 0)), 270), id++, ROAD_TILE));
		
		corners.add(BL_WATER_CORNER = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(5, 0), 0), id++, WATER_TILE));
		corners.add(TL_WATER_CORNER = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(5, 0), 90), id++, WATER_TILE));
		corners.add(TR_WATER_CORNER = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(5, 0), 180), id++, WATER_TILE));
		corners.add(BR_WATER_CORNER = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(5, 0), 270), id++, WATER_TILE));
		
		beaches.add(T_WATER = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(6, 0), 0), id++, WATER_TILE));
		beaches.add(R_WATER = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(6, 0), 90), id++, WATER_TILE));
		beaches.add(B_WATER = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(6, 0), 180), id++, WATER_TILE));
		beaches.add(L_WATER = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(6, 0), 270), id++, WATER_TILE));
		
		islands.add(TL_ISLE = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(4, 0), 0), id++, WATER_TILE));
		islands.add(TR_ISLE = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(4, 0), 90), id++, WATER_TILE));
		islands.add(BR_ISLE = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(4, 0), 180), id++, WATER_TILE));
		islands.add(BL_ISLE = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), getSprite(4, 0), 270), id++, WATER_TILE));
		
		alberelli.add(ALBERELLO_STANDARD = new Tile(ImgFix.getBuildAniSprites(LoadSave.getFileSprite("albero1_animation_6_2.0.png"), GRASS.getSprite(), 6), id++, ALBERELLO_TILE));
		alberelli.add(ALBERELLO_3_RAMI = new Tile(ImgFix.getBuildAniSprites(LoadSave.getFileSprite("alberelli.png").getSubimage(32, 0, 32, 32), GRASS.getSprite(), 1), id++, ALBERELLO_TILE));
		
		//Bridge horizontal orientation
		bridges.add(L_BRIDGE_STANDARD = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), SwingFXUtils.toFXImage(LoadSave.getFileSprite("bridge_road_3.2.png").getSubimage(0, 0, 32, 32), null), 0),  id++, Constants.Tiles.ROAD_TILE));
		bridges.add(C_BRIDGE_STANDARD = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), SwingFXUtils.toFXImage(LoadSave.getFileSprite("bridge_road_3.2.png").getSubimage(32, 0, 32, 32), null), 0),  id++, Constants.Tiles.ROAD_TILE));
		bridges.add(R_BRIDGE_STANDARD = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), SwingFXUtils.toFXImage(LoadSave.getFileSprite("bridge_road_3.2.png").getSubimage(64, 0, 32, 32), null), 0),  id++, Constants.Tiles.ROAD_TILE));
		//Bridge Vertical orientation
		bridges.add(L_BRIDGE_STANDARD = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), SwingFXUtils.toFXImage(LoadSave.getFileSprite("bridge_road_3.2.png").getSubimage(0, 0, 32, 32), null), 90),  id++, Constants.Tiles.ROAD_TILE));
		bridges.add(L_BRIDGE_STANDARD = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), SwingFXUtils.toFXImage(LoadSave.getFileSprite("bridge_road_3.2.png").getSubimage(32, 0, 32, 32), null), 90),  id++, Constants.Tiles.ROAD_TILE));
		bridges.add(L_BRIDGE_STANDARD = new Tile(ImgFix.getBuildRotImg(WATER.getSpritesList(), SwingFXUtils.toFXImage(LoadSave.getFileSprite("bridge_road_3.2.png").getSubimage(64, 0, 32, 32), null), 90),  id++, Constants.Tiles.ROAD_TILE));
		
		tiles.addAll(grass);
		tiles.addAll(water);
		tiles.addAll(roadsS);
		tiles.addAll(roadsC);
		tiles.addAll(corners);
		tiles.addAll(beaches);
		tiles.addAll(islands);
		tiles.addAll(alberelli);
		tiles.addAll(bridges);
		
		
	}
	
	private void loadAtlas() {
		atlas = LoadSave.getFileSprite("spriteatlas.png");
	}
	
	public Tile getTile(int id) {
		return tiles.get(id);
	}
	
	/**
	 * restituisce lo sprite nell'indice(id) dell' array specificato
	 * @param id
	 * @return
	 */
	public WritableImage getSprite(int id) {
		return tiles.get(id).getSprite();
	}
	
	/**
	 * restituisce l'immgine nella posizione indicata per gestire le animazioni 
	 * @param id
	 * @param animationIndex
	 * @return
	 */
	public WritableImage getAnimationSprite(int id, int animationIndex) {
		return tiles.get(id).getSprite(animationIndex);
	}
	
	
	
	/**
	 * ricava una sottoImmagine dallo sprite completo
	 * @param xCord
	 * @param yCord
	 * @return
	 */
	public WritableImage getSprite (int xCord, int yCord) {
		return SwingFXUtils.toFXImage(atlas.getSubimage(xCord * Constants.TILE_DIMENSION, yCord * Constants.TILE_DIMENSION, Constants.TILE_DIMENSION, Constants.TILE_DIMENSION), null);
	}
	
	/**
	 * restituisce true se la tile nell' indice indicato è animata
	 * @param spriteID
	 * @return
	 */
	public boolean isSpriteAnimation(int spriteID) {
		return tiles.get(spriteID).isAnimation();
	}
	
	public List<Tile> getGrass() {
		return grass;
	}
	
	public List<Tile> getWater() {
		return water;
	}
	
	public List<Tile> getRoadsS() {
		return roadsS;
	}

	public List<Tile> getRoadsC() {
		return roadsC;
	}

	public List<Tile> getCorners() {
		return corners;
	}

	public List<Tile> getBeaches() {
		return beaches;
	}

	public List<Tile> getIslands() {
		return islands;
	}
	
	public List<Tile> getAlberelli() {
		return alberelli;
	}
	
	public List<Tile> getBridges() {
		return bridges;
	}
	
	
	
	
	
}
