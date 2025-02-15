package game;

import java.util.ArrayList;


import java.util.Arrays;

import javax.swing.JFrame;

import events.Wave;
import game_scenes_logic.CanvasManager;
import game_scenes_logic.Editing;
import game_scenes_logic.GameScene;
import game_scenes_logic.Playing;
import helpz.Constants;
import helpz.LoadSave;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import managers.TileManager;
import objects.PathPoint;

public class Game {
	private Render render;
	private Editing editing;
	private TileManager tileManager;
	private Playing playing;

	private final double FPS_SET = 120.0;	//imposta gli FPS iniziali
	private final double UPS_SET = 60.0;	//imposta gli update iniziali
	private Thread gameThread;
	private volatile boolean isRunning = false;
	
	private Canvas gameCanvas;
	//private GameScreen gameScreen;
	private GameScene gameScene;
	
	public Game(Canvas canvas) {
		this.gameCanvas = canvas;
		createDefaultLevel("default_level");
		initClasses();
	}
	
	private void initClasses() {
		CanvasManager.getInstance().initialize(this, gameCanvas);
		render = new Render(this);
		tileManager = new TileManager();
		gameScene = new GameScene(this);
		playing = new Playing(this);	//editing nell'inizializzazione recupera le immagini dall'enemyManager
		editing = new Editing(this);	
	}

	private void updateGame() {
		switch (GameStates.gameState) {
		case MENU:
			break;
		case PLAYING:
			playing.update();
			break;
		case EDITING:
			editing.update();
		case SETTINGS:
			break;
		default:
			break;
		}
	}
	
	public void createDefaultLevel(String name) {
		int[] arr = new int[Constants.LVL_LENGTH];
		for (int i = 0; i < arr.length; i++)
			arr[i] = 0;
		ArrayList<Wave> waves = new ArrayList<Wave>();
		waves.add(new Wave(new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 4, 4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4))));
		LoadSave.CreateLevel(name, arr, new PathPoint(0,0), new PathPoint(0,0), waves );

	}
	
	public void startGameLoop() {
		isRunning = true;
        gameThread = new Thread(() -> {
            double timePerFrame = 1000000000.0 / FPS_SET;
            double timePerUpdate = 1000000000.0 / UPS_SET;
            long lastFrame = System.nanoTime();
            long lastUpdate = System.nanoTime();
            long lastTimeCheck = System.currentTimeMillis();
            int frames = 0;
            int updates = 0;
            long now;

            while (isRunning) {
                now = System.nanoTime();

                if (now - lastFrame >= timePerFrame) {
                	Platform.runLater(() -> CanvasManager.getInstance().updateCanvas());
                    lastFrame = now;
                    frames++;
                }

                if (now - lastUpdate >= timePerUpdate) {
                    updateGame();
                    lastUpdate = now;
                    updates++;
                }

                if (System.currentTimeMillis() - lastTimeCheck >= 1000) {
                    System.out.println("FPS: " + frames + " | UPS: " + updates);
                    frames = 0;
                    updates = 0;
                    lastTimeCheck = System.currentTimeMillis();
                }
            }
        });
        gameThread.start();
	}
	
	public void stopGameLoop() {
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	public TileManager getTileManager() {
		return tileManager;
	}
	
   	public GameScene getGameScene() {
   		return gameScene;
   	}
   	
   	public Thread getGameThread() {
   		return gameThread;
   	}
   	
   	public Render getRender() {
		return render;
	}
   	
   	public Editing getEditor() {
   		return editing;
   	}
   	
   	public Playing getPlaying() {
   		return playing;
   	}
   	
}