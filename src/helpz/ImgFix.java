package helpz;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImgFix {
	/**
	 * Crea una nuova immagine, ruotando l' immagine in input dei gradi desiderati
	 * @param img
	 * @param rotAngle
	 * @return
	 */
	public static List<WritableImage> getRotImg(List<WritableImage> imgs, int rotAngle) {
		int w = (int) imgs.get(0).getWidth();
		int h = (int) imgs.get(0).getHeight();
		
		List<WritableImage> list = new ArrayList<WritableImage>();
		BufferedImage imageBuffered = SwingFXUtils.fromFXImage(imgs.get(0), null);
		
		//crea un immagine "vuota", senza nessun disegno al suo interno
		BufferedImage newImg = new BufferedImage(w,  h, imageBuffered.getType());
		//createGraphics() permette di disegnare sull ' immagine originale sfruttando la classe Graphics2D
		Graphics2D g2d = newImg.createGraphics();
		
		//tutte le successive operazioni riguardanti g2d terranno conto di questa rotazione (anche se mettessimo 3 draw() di fila, tutte e 3 subiranno la rotazione, non solo la prima)
		//(angolo di rotazione, coordinate(X,Y) del centro di rotazione)
		g2d.rotate(Math.toRadians(rotAngle), w/2, h/2);
		//disegna l'immagine passata in input ma ruotata di rotAngle
		g2d.drawImage(imageBuffered,  0,  0,  null);
		//dopo aver usato Graphics2D rilasciamo la risorsa
		g2d.dispose();
		
		list.add(SwingFXUtils.toFXImage(newImg, null));

		return list;
	}
	
	/**
	 * per ogni immagine della lista, crea una nuova immagine con questa come base e sovrapprosta la seconda immagine
	 * @param imgs lista con immagini per lo sfondo
	 * @param secondImage immagine da sovrapprorre a tutte le altre
	 * @param rotAngle
	 * @return
	 */
	public static List<WritableImage> getBuildRotImg(List<WritableImage> imgs, WritableImage secondImage, int rotAngle) {
		int w = (int) imgs.get(0).getWidth();
		int h = (int) imgs.get(0).getHeight();

		List<WritableImage> list = new ArrayList<WritableImage>();
		BufferedImage secondImageBuffered = SwingFXUtils.fromFXImage(secondImage, null);
		
		for(int i = 0; i < imgs.size(); i++) {
			BufferedImage tempImg = SwingFXUtils.fromFXImage(imgs.get(i), null);
			
			BufferedImage newImg = new BufferedImage(w, h, tempImg.getType());
			Graphics2D g2d = newImg.createGraphics();
			
			g2d.drawImage(tempImg,  0, 0, null);
			g2d.rotate(Math.toRadians(rotAngle), w / 2, h / 2);
			g2d.drawImage(secondImageBuffered, 0, 0, null);
			g2d.dispose();
			
			list.add(SwingFXUtils.toFXImage(newImg, null));
		}
		
		return list;
	}
	
	/**
	 * Restituisce una lista conenente le immagini dell' animazione passata come primo parametro avente come sfondo l'immagine passata come secondo parametro
	 * @param mainSprite //immagine principale da cui ricavare le sotto immagini che compongono l'animazione
	 * @param backgroundImage	//immagine che fa da sfondo all' animazione
	 * @param imageAmount	//numero di immagini che compongono l'animazione
	 * @return
	 */
	public static List<WritableImage> getBuildAniSprites(BufferedImage mainSprite, WritableImage backgroundImage, int imageAmount) {
		List<WritableImage> list = new ArrayList<>();
		
		for(int i = 0; i < imageAmount; i++) {
			list.add(getBuildRotImg(Arrays.asList(backgroundImage), SwingFXUtils.toFXImage(mainSprite.getSubimage(i * Constants.TILE_DIMENSION, 0, Constants.TILE_DIMENSION, Constants.TILE_DIMENSION), null), 0).get(0)); 
		}
		
		return list;
	}
}
