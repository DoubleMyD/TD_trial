package helpz;

import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Duration;

public class Utilz {
	/**
	 * Trasforma una lista di interi in un array bidimensionale
	 * @param list 
	 * @param ySize altezza della matrice (int[ySize][xSize]
	 * @param xSize larghezza della matrice
	 * @return
	 */
	public static int[][] ArrayListTo2Dint(ArrayList<Integer>list, int ySize, int xSize ){
		int[][] newArr = new int[ySize][xSize];
		int index = 0;
		
		for(int j = 0; j < newArr.length; j++) 
			for(int i = 0; i < newArr[j].length; i++) {
				newArr[j][i]= list.get(index); 
				index++;
			}

		return newArr;
	}
	
	/**
	 * Trasfroma un array bidimensionale in un array monodimensionale
	 * @param twoArr
	 * @return
	 */
	public static int[] TwoDto1DintArr(int[][] twoArr) {
		//per ottenere la lunghezza totale dell' array moltiplica larghezza * altezza
		int[] oneArr = new int[twoArr.length * twoArr[0].length];
		int index = 0;
		for (int j = 0; j < twoArr.length; j++)
			for (int i = 0; i < twoArr[j].length; i++) {
				oneArr[index] = twoArr[j][i];
				index++;

			}
		
		return oneArr;
	}
	
	/**
	 * restituisce la distanza dell'ipotenusa tra due punti (utile per dire se il nemico è nel range della torre)
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static int GetHypoDistance(float x1, float y1, float x2, float y2) {
		
		float xDiff = Math.abs(x1 - x2);
		float yDiff = Math.abs(y1 - y2);

		return (int) Math.hypot(xDiff, yDiff);
	}
	
	/**
	 * assegna al bottone l'immagine passata, adattandola alle dimensioni del bottone
	 * @param button
	 * @param image
	 */
	public static void setButtonImg(Button button, WritableImage image) {
		ImageView imageView = new ImageView();
		imageView.setImage(image);
		
		imageView.setFitHeight(button.getPrefHeight());
		imageView.setFitWidth(button.getPrefWidth());
		
		button.setGraphic(imageView);
	}
	
	/**
	 * crea un bottone con i valori passati. il parametro id non è l'id del bottone ma il suo UserData (serve come identificativo numerico)
	 * @param fxmlID la stringa che fa da id del bottone
	 * @param text il testo del bottone
	 * @param id numero identificativo
	 * @param w width
	 * @param h height
	 * @param action funzione da richiamare quando premuto
	 * @return
	 */
	public static Button createButton(String fxmlID, String text, int id, int w, int h, EventHandler<ActionEvent> action) {
		Button button = new Button();
		button.setId(fxmlID);
		button.setText(text);
		button.setUserData(id);
		button.setPrefSize(w, h);
		button.setCursor(Cursor.HAND);
		button.setOnAction(action);

		return button;
	}
	
	/**
	 * crea una finestra di input per l'utente. ATTENZIONE : Questa funzione non imposta nessun metodo per quando l'utente inserisce i dati
	 * @param headerText testo del titolo
	 * @param contentText	testo del contenuto/richiesta
	 * @param window	la finestra proprietaria di questo InputDialog
	 * @return
	 */
	public static TextInputDialog createTextInputDialog(String headerText, String contentText, Window window) {
		TextInputDialog textInputDialog = new TextInputDialog();
		textInputDialog.setHeaderText(headerText);
		textInputDialog.setContentText(contentText);
		textInputDialog.initOwner(window);
		
		return textInputDialog;
	}
	
	public static void fadeTransition(Node node) {
	    if(node.isVisible()) {
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
		fadeTransition.setFromValue(1.0);
	    fadeTransition.setToValue(0.0);
	    fadeTransition.play();
	    
	    fadeTransition.setOnFinished(e -> { node.setVisible(false); });
	    } 
	    else {
	    	node.setVisible(true);
	    	FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
		    fadeTransition.setFromValue(0.0);
		    fadeTransition.setToValue(1.0);
		    fadeTransition.play();
	    }
	}
}