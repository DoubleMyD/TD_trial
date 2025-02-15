package objects;

public class PathPoint {
	private int xCord, yCord;
	
	/**
	 * Gestisce la fine e l' inizio del percorso dei nemici 
	 * @param xCord
	 * @param yCord
	 */
	public PathPoint(int xCord, int yCord) {
		this.xCord = xCord;
		this.yCord = yCord;
	}

	public int getxCord() {
		return xCord;
	}

	public void setxCord(int xCord) {
		this.xCord = xCord;
	}

	public int getyCord() {
		return yCord;
	}

	public void setyCord(int yCord) {
		this.yCord = yCord;
	}

}
