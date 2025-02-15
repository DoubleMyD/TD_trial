package objects;

import java.util.ArrayList;
import java.util.List;

import events.Wave;
/**
 * classe di utilità per passare le informazioni del livello
 */
public class Level {
	private final String nameFile;
	private int[][] lvl;
	private PathPoint pathStart, pathEnd;
	private List<Wave> levelWaves = new ArrayList<>();
	
	/**
	 * @param nameFile	nome del file del livello
	 * @param lvl	array con il livello
	 * @param start	PathPoint start
	 * @param end	PathPoint end
	 * @param waves	Ondate del livello
	 */
	public Level(String nameFile, int[][] lvl, List<PathPoint> pathPoints, List<Wave> waves ) {
		this.nameFile = nameFile;
		this.lvl = lvl;
		this.pathStart = pathPoints.get(0);
		this.pathEnd = pathPoints.get(1);
		this.levelWaves = waves;
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

	public List<Wave> getLevelWaves() {
		return levelWaves;
	}

	public void setLevelWaves(List<Wave> levelWaves) {
		this.levelWaves = levelWaves;
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setLvl(int[][] newLvl) {
		this.lvl = newLvl;
	}
	
	public int[][] getLvl() {
		return this.lvl;
	}
}
