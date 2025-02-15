package game;

import javafx.scene.canvas.GraphicsContext;


public class Render {

	private Game game;

	public Render(Game game) {
		this.game = game;

	}

	public void render(GraphicsContext gc) {
		switch (GameStates.gameState) {
			case PLAYING:
				game.getPlaying().render(gc);
				break;
			case EDITING:
				game.getEditor().render(gc);
				break;
			case GAME_OVER:
				game.getPlaying().render(gc);
				break;
			default:
				break;
			
		}

	}

}