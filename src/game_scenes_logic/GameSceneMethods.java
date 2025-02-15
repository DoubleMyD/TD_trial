package game_scenes_logic;

import javafx.scene.canvas.GraphicsContext;

public interface GameSceneMethods {
	
	public void render(GraphicsContext gc);
	
	public void update();

	public void drawLevel(GraphicsContext gc);
	

}
