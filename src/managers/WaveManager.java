package managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import events.Wave;
import game_scenes_logic.Playing;

public class WaveManager {
	private Playing playing;
	private List<Wave> waves = new ArrayList<>();
	
	private int enemySpawnTickLimit = 60 * 1;//ogni quanto spawnare un nemico (60= 1 secondo(60 update per secondo))
	private int enemySpawnTick = enemySpawnTickLimit;//contatore dello spawn ( se arriva al limite, si spawna il nemico e si resetta)
	
	private int enemyIndex, waveIndex; //indice del nemico nell'ondata e indice dell'ondata
	private int waveTickLimit = 60 * 5;	//tempo tra una ondata e l'altra
	private int waveTick = 0;
	
	private boolean waveStartTimer, waveTickTimerOver; //controllano se vada inizializzato o terminato il timer
	
	public WaveManager(Playing playing) {
		this.playing = playing;
		createDefaultWaves();
	}
	
	public void update() {
		if (enemySpawnTick < enemySpawnTickLimit)
			enemySpawnTick++;
		
		if (waveStartTimer) {
			waveTick++;
			if (waveTick >= waveTickLimit) {
				waveTickTimerOver = true;
			}
		}
	}
	
	/**
	 * crea una nuova ondata, passandogli i nemici da spawnare
	 */
	private void createDefaultWaves() {
		waves.add(new Wave(new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 4, 4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4))));

		waves.add(new Wave(new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 1))));
		waves.add(new Wave(new ArrayList<Integer>(Arrays.asList(2, 0, 0, 0, 0, 0, 0, 0, 0, 1))));
	}
	
	/**
	 * recupera l'ondata successiva dall'ArrayList delle ondate, resettando il timer(waveStartTimer e waveTickTimerOver, che controllano se sono spawnati tutti i nemici e che il conto alla rovescia sia terminato)
	 */
	public void increaseWaveIndex() {
		waveIndex++;
		waveTick = 0;
		waveTickTimerOver = false;
		waveStartTimer = false;
	}
	
	/**
	 * restituisce true se il tempo tra una ondata e l'altra è terminata
	 * @return
	 */
	public boolean isWaveTimerOver() {
		return waveTickTimerOver;
	}
	
	public void startWaveTimer() {
		waveStartTimer = true;
	}
	
	/**
	 * restituisce il prossimo nemico dell'ondata che deve essere spawnato
	 * @return
	 */
	public int getNextEnemy() {
		enemySpawnTick = 0;
		return waves.get(waveIndex).getEnemyList().get(enemyIndex++);
	}
	
	/**
	 * restituisce true se il contatore dello spawn raggiunge il limite (deve essere spawnato un nuovo nemico)
	 * @return
	 */
	public boolean isTimeForNewEnemy() {
		return enemySpawnTick >= enemySpawnTickLimit;
	}
	
	/**
	 * restituisce true se ci sono altri nemici nell' ondata che devono essere spawnati
	 * @return
	 */
	public boolean isThereMoreEnemiesInWave() {
		//attenzione! enemyIndex va da 0 4(per esempio), la size della lista da 1 a 5, ma a noi va bene perchè effettuiamo il +1 nel metodo getNextEnemy, subito dopo aver detto l'indice dell' array dei nemici da cui prendere il tipo del prossimo nemico da spawnare
		return enemyIndex < waves.get(waveIndex).getEnemyList().size();
	}
	
	/**
	 * restituisce true se l' indice dell' ondata corrente è minore della dimensione dell'array delle ondate
	 */
	public boolean isThereMoreWaves() {
		//se l'indice dell' ondata e 4, la size dell'array è 5
		return waveIndex + 1 < waves.size();
	}
	
	public void resetEnemyIndex() {
		enemyIndex = 0;
	}

	public int getWaveIndex() {
		return waveIndex;
	}
	
	/**
	 * tempo rimasto per la prossima ondata
	 */
	public float getTimeLeft() {
		//il tempo tra un'ondata e l' altra e il tempo trascorso dalla fine dell'ondata attuale
		float ticksLeft = waveTickLimit - waveTick;
		//60.0f corrisponde a un secondo
		return ticksLeft / 60.0f;
	}
	
	public boolean isWaveTimerStarted(){
		return waveStartTimer;
	}
	
	/**
	 * pulisce l'array delle ondate, crea di nuovo le ondate, azzera gli indici delle ondate e dei nemici per ogni ondata, resetta il timer dell'ondata
	 */
	public void reset() {
		//waves.clear();
		//createWaves();
		enemyIndex = 0;
		waveIndex = 0;
		waveStartTimer = false;
		waveTickTimerOver = false;
		waveTick = 0;
		enemySpawnTick = enemySpawnTickLimit;
	}
	
	public void setWaves(List<Wave> waves) {
		this.waves = waves;
	}
	
	public List<Wave> getWaves() {
		return waves;
	}
}
