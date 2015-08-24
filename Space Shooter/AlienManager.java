import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
 
public class AlienManager {
	
	ArrayList<Alien> aliens;
	
	Timer alienTimer;
	
	public AlienManager() {
		
		aliens = new ArrayList<Alien>();
		
		alienTimer = new Timer();
		alienTimer.scheduleAtFixedRate(new AlienTask(), 0, 20);
	}
	
	public void addAliens(int num) {
		
		//Basically, I don't want aliens to be created right next to each other
		//so I made a set of points that each one can be randomly made at
		ArrayList<Point> alienPointList = new ArrayList<Point>();
		
		int y = -40;
		for (int x = -40; x <= Aspects.WIDTH; x += 40) {
			if ((x < 0) || (x >= Aspects.WIDTH)) {
				for (y = 0; y <= Aspects.HEIGHT; y += 40) {
					
					alienPointList.add(new Point(x, y));
				}
			}
			alienPointList.add(new Point(x, -40));
		}
		
		for (int i = 0; i < num; ++i) {
			
			Alien al = new Alien();
			
			aliens.add(al);
			
			//Get random point from alienPointList
			Random randomPoint = new Random();
			int pointIndex = randomPoint.nextInt(aliens.size() - 0 + 1) + 0;
			al.setCoords(alienPointList.get(pointIndex));
			
			//Don't allow another alien to be made at that same point
			alienPointList.remove(pointIndex);
		}
	}
	
	class AlienTask extends TimerTask {
		
		public void run() {
			
			for (int i = 0; i < aliens.size(); ++i) {
				
				Alien al = aliens.get(i);
				
				al.move();
				al.checkCollision(i);
			}
			
		}
	}
	
	public void draw(Graphics2D g2d) {
		
		for (int i = 0; i < aliens.size(); ++i) {
			
			g2d.drawImage(aliens.get(i).getImage(), aliens.get(i).getX(),
					aliens.get(i).getY(), null);
		}
	}

	public class Alien extends Sprite implements Aspects {
		
		public boolean isBouncing = false;
		
		private int points;
		
		public Alien() {
			
			++Board.monstersOnBoard;
			
			//Get alien image
			ImageIcon ii = new ImageIcon(getClass().getResource("/images/Alien.gif"));
			image = ii.getImage();
			image = Transparency.makeColorTransparent(image, Color.WHITE);
			
			points = 10;
			
			//Set random speed 
			Random random = new Random();
			dx = 1.5 + (4 - 1.5) * random.nextDouble();
			dy = 5 - dx;
			
			//Not sure how to get random *negative* doubles, so I did this
			if (random.nextBoolean()) dx *= -1;
			if (random.nextBoolean()) dy *= -1;
		}
		
		//Updates x and y positions
		public void move() {
			
			x += dx;
			y += dy;
			
			//Will probably change, as it can result in buggy behavior
			if ((x > Aspects.WIDTH) && (dx > 0)) x = -36;
			if ((y > Aspects.HEIGHT) && (dy > 0)) y = -36;
			if ((x < -35) && (dx < 0)) x = Aspects.WIDTH;
			if ((y < -35) && (dy < 0)) y = Aspects.HEIGHT;
		}
		
		public void checkCollision(int index) {
			
			//Alien hits ship
			if (getBounds().intersects(Board.ship.getBounds())) {
				aliens.remove(index);
				--Board.monstersOnBoard;
				--Board.ship.health;
				Board.ship.explode();
				explode();
				Board.ship.slow(0.4);
			}
			
			//Alien hits missile
			for (int i = 0; i < Board.missileManager.missiles.size(); ++i) {
				MissileManager.Missile mis = Board.missileManager.missiles.get(i);
				
				if (getBounds().intersects(mis.getBounds())) {
					aliens.remove(index);
					--Board.monstersOnBoard;
					Board.missileManager.missiles.remove(i);
					Board.score += points;
					Board.pointHoverManager.addPointHover(x - getWidth() / 2 - 5, y, points);
					explode();
				}
			}
		}
		
		public void explode() {
			
			Board.explosionManager.addExplosion(getX() - (Board.explosionManager.width - 
					getWidth()) / 2 + 10, getY() - (Board.explosionManager.width - getHeight()) / 2 + 10);
		}
		
		//Sets x and y based on 'point'
		public void setCoords(Point point) {
			x = point.x;
			y = point.y;
		}
	}
}