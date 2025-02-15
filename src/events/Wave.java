package events;

import java.util.ArrayList;
import java.util.List;

public class Wave {
	private List<Integer> enemyList;

	public Wave(List<Integer> enemyList) {
		this.enemyList = enemyList;
	}

	public List<Integer> getEnemyList() {
		return enemyList;
	}

}