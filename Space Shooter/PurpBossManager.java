import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

public class PurpBossManager {
	
	//Contains each PurpBoss
	ArrayList<PurpBoss> purpBosses;
	public ArrayList<PurpBoss.PBBul> PBBuls;
	
	public PurpBossManager() {
		
		purpBosses = new ArrayList<PurpBoss>();
	}
	
	//Creates new PurpBoss
	public void addPurpBoss() {
		
		purpBosses.add(new PurpBoss());
	}
	
	//Draws each PurpBoss and bullet
	public void draw(Graphics2D g2d) {
		
		for (int i = 0; i < purpBosses.size(); ++i) {
			g2d.drawImage(purpBosses.get(i).getImage(), purpBosses.get(i).getX(),
					purpBosses.get(i).getY(), null);
			
			for (int j = 0; j < PBBuls.size(); ++j) {
				g2d.drawImage(PBBuls.get(j).getImage(), PBBuls.get(j).getX(),
						PBBuls.get(j).getY(), null);
			}
		}
	}
	
	//Individual PurpBoss, containing bullets
	public class PurpBoss extends Sprite {
		
		public int PBHealth;
		private Timer PBTimer;
		private Timer scaredTimer;
		
		private int shootCount;
		private int points;
		
		public PurpBoss() {
			
			++Board.monstersOnBoard;
			
			PBBuls = new ArrayList<PBBul>();
			
			//Set image
			ImageIcon ii = new ImageIcon(getClass().getResource("/images/purpBoss.gif"));
			this.image = ii.getImage();
			this.image = Transparency.makeColorTransparent(this.image, Color.WHITE);
			
			newCoords();
			
			shootCount = 0;
			points = 50;
			PBHealth = 20;
			
			Random random = new Random();
			dx = random.nextInt(3) + 1;
			dy = 4 - dx;
			
			if (random.nextBoolean()) dx *= -1;
			if (random.nextBoolean()) dy *= -1;
			
			PBTimer = new Timer();
			PBTimer.scheduleAtFixedRate(new PBTimerTask(), 0, 20);
			
			scaredTimer = new Timer();
		}
		
		//Runs each tick of timer
		class PBTimerTask extends TimerTask {
			
			public void run() {
				
				//If PurpBoss is alive
				if (PBHealth > 0) {
				
					move();
					updateBuls();
					checkCollision();
					
					if (shootCount < 150) ++shootCount;
					else {
						PBBuls.add(new PBBul(x + 25, y + 25, 0, 2));
						PBBuls.add(new PBBul(x + 25, y + 25, 0, -2));
						PBBuls.add(new PBBul(x + 25, y + 25, 2, 0));
						PBBuls.add(new PBBul(x + 25, y + 25, -2, 0));
						shootCount = 0;
					}
				}
				
				//If PurpBoss is dead
				else {
					Board.score += points;
					Board.pointHoverManager.addPointHover(x - 15, y, points);
					PBTimer.cancel();
					int currentIndex = purpBosses.size() - 1;
					purpBosses.get(currentIndex).explode();
					purpBosses.remove(currentIndex);
					--Board.monstersOnBoard;
				}
			}
		}
		
		//Runs when PurpBoss is shot
		class scaredTimerTask extends TimerTask {
			
			public void run() {
				
				//Temporarily change image
				ImageIcon ii = new ImageIcon(getClass().getResource("/images/purpBoss.gif"));
				image = ii.getImage();
				image = Transparency.makeColorTransparent(image, Color.WHITE);
			}
		}
		
		private void checkCollision() {
			
			//PurpBoss hits ship
			if (getBounds().intersects(Board.ship.getBounds())) {
				--Board.ship.health;
				Board.ship.explode();
				newCoords();
				Board.ship.slow(0.4);
			}
			
			//PurpBoss hits missile
			for (int i = 0; i < Board.missileManager.missiles.size(); ++i) {
				
				if (getBounds().intersects(Board.missileManager.missiles.get(i).getBounds())) {
					ImageIcon ii = new ImageIcon(getClass().getResource("/images/pb4.png"));
					image = ii.getImage();
					image = Transparency.makeColorTransparent(image, Color.WHITE);
					
					scaredTimer.schedule(new scaredTimerTask(), 200);
					--PBHealth;
					Board.missileManager.missiles.remove(i);
				}
			}
		}
		
		//Updates position
		private void move() {
			
			if ((x <= 0) || (x >= Aspects.WIDTH - this.image.getWidth(null)))
					dx *= -1;
			
			if ((y <= 0) || (y >=Aspects.HEIGHT - this.image.getHeight(null)))
					dy *= -1;
			
			x += dx;
			y += dy;
		}
		
		private void updateBuls() {
			
			for (int i = 0; i < PBBuls.size(); ++i) {
				
				PBBuls.get(i).setX((int) (PBBuls.get(i).getX() + PBBuls.get(i).dx));
				PBBuls.get(i).setY((int) (PBBuls.get(i).getY() + PBBuls.get(i).dy));
				
				//PBBul goes offscreen
				if ((PBBuls.get(i).getX() <= -20) || (PBBuls.get(i).getX() >= Aspects.WIDTH))
					PBBuls.remove(i);
				else if ((PBBuls.get(i).getY() <= -20) || (PBBuls.get(i).getY() >= Aspects.HEIGHT)) {
					PBBuls.remove(i);
				}
				//PBBul hits ship
				else if (PBBuls.get(i).getBounds().intersects(Board.ship.getBounds())) {
					PBBuls.get(i).explode();
					PBBuls.remove(i);
					--Board.ship.health;
					Board.ship.explode();
					Board.ship.slow(0.1);
				}
				//PBBul hits missile
				else {
					for (int j = 0; j < Board.missileManager.missiles.size(); ++j) {
						if (PBBuls.get(i).getBounds().intersects(Board.missileManager.missiles.get(j).getBounds())) {
							Board.missileManager.missiles.remove(j);
							PBBuls.get(i).explode();
							PBBuls.remove(i);
						}
					}
				}
				
			}
		}
		
		//Teleports PurpBoss to new location if hit by ship
		private void newCoords() {
			
			if (Board.ship.getX() < Aspects.WIDTH / 2) setX(150);
			else setX(600);
			
			if (Board.ship.getY() < Aspects.HEIGHT / 2) setY(600);
			else setY(150);
		}
		
		//Creates new explosion
		public void explode() {
			
			Board.explosionManager.addExplosion(getX() - (Board.explosionManager.width - getWidth()) / 2,
					getY() - (Board.explosionManager.width - getHeight()) / 2);
		}
		
		//Individual PurpBoss Bullet class
		public class PBBul extends Sprite {
			
			public PBBul(int x, int y, double dx, double dy) {
				
				//Set image
				ImageIcon iibul = new ImageIcon(getClass().getResource("/images/pbbul.gif"));
				this.image = iibul.getImage();
				this.image = Transparency.makeColorTransparent(this.image, Color.WHITE);
				
				this.x = x;
				this.y = y;
				this.dx = dx;
				this.dy = dy;
			}
			
			//Create explosion
			public void explode() {
				
				Board.explosionManager.addExplosion(getX() - (Board.explosionManager.width - getWidth())
						/ 2 + 10, getY() - (Board.explosionManager.width - getHeight()) / 2 + 10);
			}
		}
	}
}