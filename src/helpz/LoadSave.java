package helpz;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import events.Wave;
import objects.PathPoint;

public class LoadSave {
	
	/**
	 * Restituisce l' immagine salvata nel file indicato
	 * @param nameFile nome completo del file (col percorso relativo dalla cartella res 
	 * @return
	 * 
	 * Esempio di input : "NomeFile.png"
	 */
	public static BufferedImage getFileSprite(String nameFile) {
		BufferedImage img = null;
		InputStream is = LoadSave.class.getClassLoader().getResourceAsStream(nameFile);
		
		try {
			img = ImageIO.read(is);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return img;
	}
	
	/**
	 * Se il livello non esiste già, ne crea uno nuovo 
	 * @param name
	 * @param idArr
	 * @param start
	 * @param end
	 * @param enemyWaves
	 */
	public static void CreateLevel(String name, int[] idArr, PathPoint start, PathPoint end, List<Wave> enemyWaves) {
		File newLevel = new File("res/" + name + ".txt");
		
		//se il file già esiste evita di sovrascriverlo e crearne uno nuovo
		if(newLevel.exists()) {
			System.out.println("Crate Level File: " + name + " already exists!");
			return;
		} else {
			try {
				newLevel.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			WriteToFile(newLevel, idArr, start, end, enemyWaves);
		}
	}
	
	/**
	 * Scrive il livello sul file indicato
	 * @param f 
	 * @param idArr Array contenente l'id della Tile per ogni posizione
	 */
	private static void WriteToFile (File f, int[] idArr, PathPoint start, PathPoint end, List<Wave> enemyWaves) {
		try {
			PrintWriter pw = new PrintWriter(f);
			for(Integer i : idArr)
				pw.println(i);
			//alla fine del file salava le posizioni della START e END position del percorso
			pw.println(start.getxCord());
			pw.println(start.getyCord());
			pw.println(end.getxCord());
			pw.println(end.getyCord());
			
			List<Integer> enemy = new ArrayList<>();
			for(Wave wave : enemyWaves) {
				enemy = wave.getEnemyList();
				for(Integer i : enemy) {
					pw.println(i);
				}
				pw.println(Constants.WAVE_SEPARATOR);//serve a capire in fase di recupero dove finisce l'ondata
			}
			pw.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Salva il livello col nome indicato
	 * @param name nome del livello
	 * @param idArr Array contente l'id della Tile per ogni posizione
	 */
	public static void SaveLevel(String name, int[][] idArr, PathPoint start, PathPoint end, List<Wave> enemyWaves) {
		File levelFile = new File("res/" + name + ".txt");
		
		//se il file esiste scrive il livello sul file
		if(levelFile.exists()) {
			WriteToFile(levelFile, Utilz.TwoDto1DintArr(idArr), start, end, enemyWaves);
		} else {
			System.out.println("File: " + name + " does not exists! ");
			return;
		}
	}
	
	/**
	 * Restituisce un ArrayList contente il livello (l'id della Tile per ogni posizione)
	 * @param file File da leggere
	 * @return
	 */
	private static ArrayList<Integer> ReadFromFile(File file){
		ArrayList<Integer>list = new ArrayList<>();
		
		try {
			Scanner sc = new Scanner(file);
			
			while(sc.hasNextLine()) {
				//siccome nextLine() restituisce una stringa, facciamo il cast a un intero
				list.add(Integer.parseInt(sc.nextLine()));
			}
			
			sc.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
	

	public static ArrayList<PathPoint> GetLevelPathPoints(String name){
		File lvlFile = new File("res/" + name + ".txt");

		if (lvlFile.exists()) {
			ArrayList<Integer> list = ReadFromFile(lvlFile);
			ArrayList<PathPoint> points = new ArrayList<>();
			//posizioni predefinite dell'array in cui salviamo i PathPoint
		
			points.add(new PathPoint(list.get(Constants.PathPoints.PATHPOINT_START_FILE_X_POSITION), list.get(Constants.PathPoints.PATHPOINT_START_FILE_Y_POSITION)));
			points.add(new PathPoint(list.get(Constants.PathPoints.PATHPOINT_END_FILE_X_POSITION), list.get(Constants.PathPoints.PATHPOINT_END_FILE_Y_POSITION)));

			return points;

		} else {
			System.out.println("File: " + name + " does not exists! (GetLevelPathPoints) ");
			return null;
		}
	}
	

	public static List<Wave> GetLevelWaves(String name){
		File lvlFile = new File("res/" + name + ".txt"); //nome del file da cui prendere le ondate

		if (lvlFile.exists()) {
			List<Integer> list = ReadFromFile(lvlFile); //lista completa di tutti gli interi salvati
			List<Integer> wavesList = new ArrayList<Integer>(list.subList(Constants.PathPoints.PATHPOINT_END_FILE_Y_POSITION + 1, list.size())); //lista contenente solo la parte sulle ondate/sull'id dei nemici
			List<Wave> waves = new ArrayList<>();	//lista contenente le singole ondate

			if(wavesList.size() > 0) {	//controlla che il livello abbia delle ondate
				int i = 0;

				while(i < wavesList.size()) {	//finchè l'indice non raggiunge la fine della lista 
					List<Integer> singleWave = new ArrayList<>(); //istanzia una nuova lista per salvare la singola ondata

					while(wavesList.get(i)!=Constants.WAVE_SEPARATOR && i < wavesList.size() ) {//fin quando non raggiunge la fine dell'ondata o la fine del file, aggiunge l'id del nemico corrispondente all'ondata
						singleWave.add(wavesList.get(i));
						i++;
					}
					//se ha trovato almeno un nemico, aggiunge l'ondata alla lista delle ondate
					if(singleWave.size() > 0)
						waves.add(new Wave(singleWave));
					i++;
				}
			}
			else {
				//deafult wave
				waves.add(new Wave(new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 4, 4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4))));
				waves.add(new Wave(new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 1))));
				waves.add(new Wave(new ArrayList<Integer>(Arrays.asList(2, 0, 0, 0, 0, 0, 0, 0, 0, 1))));
			}
			return waves;

		} else {
			System.out.println("File: " + name + " does not exists! (GetLevelPathPoints) ");
			return null;
		}
	}
	
	/**
	 * Restituisce l'Array bidimensionale che contiene il livello
	 * @param name Nome del livello/file
	 * @return
	 */
	public static int[][] GetLevelData(String name){
		File lvlFile = new File("res/" + name + ".txt");
		
		if(lvlFile.exists()) {
			ArrayList<Integer>list = ReadFromFile(lvlFile); 
			//le dimensioni dell array bidimensionale sono predefinite(da noi)
			return Utilz.ArrayListTo2Dint(list, Constants.LVL_ROWS, Constants.LVL_COLUMNS);//20,48
		} else {
			System.out.println("File: " + name + " does not exists! (GetLevelData)");
			return null;
		}
	}
	
	public static int NumberOfLevel() {
		int i = 1;
		File lvlFile = new File("res/level_" + i + ".txt");
		
		while(lvlFile.exists()) {
			i++;
			lvlFile = new File("res/level_" + i + ".txt");
		}

		return i-1;//nell' ultimo ciclo, prima di controllare se esiste o meno il file, ha comunque incrementato l'indice, quindi quando esce dal while ha un indice extra che dobbiamo eliminare
	}
}
