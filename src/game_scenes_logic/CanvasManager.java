package game_scenes_logic;

import java.util.ArrayList;
import javafx.scene.paint.Color;

import java.util.List;

import events.Wave;
import game.Game;
import helpz.Constants;
import helpz.LoadSave;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import objects.PathPoint;

public class CanvasManager {
	private static CanvasManager instance;
	
	//Canvas e GraphicContext permettono di gestire il disegno del gioco
	protected Canvas gameCanvas;
	protected GraphicsContext gc;
	
	//sezione che gestisce le animazioni presenti in qualunque parte del gioco (acqua ed alberi si trovano sia in editing che in playing)
	protected Game game;
	
	
	private CanvasManager() {
		
	}
	
	public static CanvasManager getInstance() {
		if(instance == null) {
			instance = new CanvasManager();
		}
		
		return instance;
	}
	
	public void initialize(Game game, Canvas canvas) {
		this.gameCanvas = canvas;
        this.gc = gameCanvas.getGraphicsContext2D();
        setMouseEventHandlerForGameCanvas(gameCanvas);
        this.game = game;
	}

	public void setMouseEventHandlerForGameCanvas(Canvas gameCanvas) {
		gameCanvas.setOnMouseMoved(event -> updateMousePosition(event));
		gameCanvas.setOnMouseClicked(event -> updateMousePosition(event));
	}
	
	private void updateMousePosition(MouseEvent event) {
		double x = event.getX();
		double y = event.getY();

		game.getPlaying().setTryX( ((int) x/64) * 64);
		game.getPlaying().setTryY( ((int) y/64) * 64);

		//504 / 32 = 12.56, essendo int il quoziente è 12(non 12.56) e 12*32 ci dà la posizione della Tile
		game.getGameScene().setMouseX( ((int)x / Constants.TILE_DIMENSION) * Constants.TILE_DIMENSION);
		game.getGameScene().setMouseY( ((int)y / Constants.TILE_DIMENSION) * Constants.TILE_DIMENSION);
	}
	
	public void updateCanvas() {
		gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
		game.getRender().render(gc);
	}
	
	public Canvas getGameCanvas() {
		return gameCanvas;
	}
}
