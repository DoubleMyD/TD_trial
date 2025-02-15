package comparator;

import java.util.Comparator;

import enemies.Enemy;
import helpz.Constants;

public class EnemyPosComparator implements Comparator<Enemy> {

	@Override
	public int compare(Enemy enemy1, Enemy enemy2) {
	    // Compare based on the x-coordinate
	    float x1 = enemy1.getX();
	    float x2 = enemy2.getX();

	    float y1 = enemy1.getY();
	    float y2 = enemy2.getY();

	    // If the x-coordinate of both enemies is equal
	    if (Float.compare(x1, x2) == 0) {
	        // If the lastDir of the first enemy is DOWN
	        if (enemy1.getLastDir() == Constants.Direction.DOWN) {
	            return Float.compare(y2, y1); // Compare their y-coordinates in reverse order
	        } else {
	            return Float.compare(y1, y2); // Compare their y-coordinates in natural order
	        }
	    } else {
	        return Float.compare(x2, x1); // Compare their x-coordinates
	    }
	}
}
