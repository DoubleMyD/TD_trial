package game;

public enum GameStates {
	
	PLAYING, MENU, SETTINGS, EDITING, GAME_OVER;

	//stato con cui si avvia la classe, e quindi il gioco
	public static GameStates gameState = MENU;
	
	public static void SetGameState(GameStates state) {
		gameState = state;
	}
}

