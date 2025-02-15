package objects;


import java.awt.geom.Point2D;

import enemies.Enemy;
import helpz.Constants;
import helpz.Constants.Projectiles;

public class Projectile {
	
	private Point2D.Float pos, startPos; //utilizzata per salvare le coordinate in un unica variabile
	private int id, projectileType, dmg;
	private float /*xSpeed, ySpeed,*/ rotation;
	private boolean active = true;
	
	private float targetX = 0; // Coordinate x del punto in movimento
    float targetY = 0; // Coordinate y del punto in movimento
    float targetXSpeed = 0; // Velocità x del punto in movimento
    float targetYSpeed = 0;
    
    private int projNumber = 0;
    private float towerRange, vertexHeight = -240, initialVelocityY = 0f;
    private float velocityCoefficient = 0.02f;
    private float vertexTime = 1.0f; // Tempo per raggiungere il vertice
	private Enemy enemy;
	private float gravity = -0.2f;
	private int counter = 0;
	
	private static int projCounter = 0;

	
	public Projectile(float x, float y,/* float xSpeed, float ySpeed,*/ int dmg, float rotation, int id, int projectileType, Enemy e, float towerRange) {
		this.pos = new Point2D.Float (x, y);
		this.startPos = new Point2D.Float (x,y);
		
		this.dmg = dmg;
		this.id = id;
		this.projectileType = projectileType;
		this.towerRange = towerRange;
		
		this.vertexHeight += -this.towerRange; 
		this.enemy = e;
		
		//enemy.setDrawRedBounds(active);
		//targetXSpeed = Costants.Enemies.GetSpeed(enemy.getEnemyType());
		this.rotation = 90;
		this.projNumber = projCounter;
		projCounter ++;
		
	    // Calcoliamo la velocità iniziale lungo l'asse y utilizzando l'altezza del vertice
	    this.initialVelocityY = (vertexHeight/*startPos.y*/ + 0.5f * gravity * vertexTime * vertexTime) / vertexTime;
	}
	
	//FUNZIONA PURE MEGLIO!!!!!!!
	public void move() {
	    targetX = enemy.getX(); // Coordinate x del punto in movimento
	    targetY = enemy.getY(); // Coordinate y del punto in movimento
	    //System.out.println("Enemy life : " + enemy.getHealth() + "	//	Enemy futurdamage : " + enemy.getFutureDamage() + "	//	Enemy type/id : " + enemy.getEnemyType() + " - " + enemy.getID() +	"	//	Enemy pos : " + enemy.getX() + " : " + enemy.getY() + "	//////	Projectile pos : " + pos.x + " : " + pos.y);

	    // Calcoliamo la distanza dal punto target
	    float distanceX = (targetX - startPos.x);
	    float distanceY = (targetY - startPos.y);
	
		 
	    // Calcoliamo la velocità proporzionale lungo l'asse x con aumento lineare
	    float speedX = ((targetX - startPos.x) * 0.01f + velocityCoefficient * distanceX) / 120.0f * 60.0f;


	    // Calcoliamo la velocità proporzionale lungo l'asse y
	    float speedY = velocityCoefficient * initialVelocityY;
	    
    	//System.out.println("difference Y pos : " + (Math.abs(distanceY) - towerRange) + " //// Enemy yPos : " + enemy.getY() + " //// Tower yPos : " + startPos.y + " //// vertexHeight : " + vertexHeight + " //// distanceY : " + distanceY);  
	    if((Math.abs(distanceY) - towerRange) > -10)
	    	speedY *= 1.05;
	    	//speedY -= 1;
	    if((Math.abs(distanceX) - towerRange) > -10)
	    	speedX *= 1.05;
	    
	    // Aggiorniamo la posizione del proiettile
	    pos.x = 16f + startPos.x + speedX * counter ;
	    pos.y = startPos.y + speedY * counter - 0.5f * gravity * counter * counter;
	    
	    if(enemy.getX() - startPos.x <= 0) 
	    	rotation -= 2.6;
	    else 
	    	rotation += 2.6;
	    
	    counter += 1;
	    if(counter > 150) {
	    	active = false;
	    	//enemy.updateFutureDamage(-dmg);
	    }
	 
	}


	public Point2D.Float getPos() {
		return pos;
	}

	public void setPos(Point2D.Float pos) {
		this.pos = pos;
	}

	public int getId() {
		return id;
	}

	public int getProjectileType() {
		return projectileType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public int getDmg() {
		return dmg;
	}
	
	public float getRotation() {
		return rotation;
	}

	public Enemy getEnemy() {
		return enemy;
	}
}