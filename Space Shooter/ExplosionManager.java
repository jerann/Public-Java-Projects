import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;


public class ExplosionManager {
	
	ArrayList<Explosion> explosions;
	int width = 50;
	
	Timer explosionTimer;
	
	public ExplosionManager() {
		
		explosions = new ArrayList<Explosion>();
		
		explosionTimer = new Timer();
		explosionTimer.scheduleAtFixedRate(new explosionTask(), 0, 20);
	}
	
	//Creates new explosion
	public void addExplosion(int x, int y) {
		
		explosions.add(new Explosion(x, y));
	}
	
	//Draws all explosions
	public void draw(Graphics2D g2d) {
		for (int i = 0; i < explosions.size(); ++i) {
			
			g2d.drawImage(explosions.get(i).getImage(),explosions.get(i).getX(),
					explosions.get(i).getY(), null);
		}
	}
	
	class explosionTask extends TimerTask {
		
		public void run() {
			
			for (int i = 0; i < explosions.size(); ++i) {
				
				//30 just corresponds to the length of the GIF (30 * 20ms)
				if (explosions.get(i).explosionTimerCount >= 30) {
					explosions.remove(i);
				}
				else {
					++explosions.get(i).explosionTimerCount;
				}
			}
		}
	}

	//Individual explosion
	public class Explosion extends Sprite {
	
		int explosionTimerCount;
		int indexPos;
		
		int width;
		
		private Explosion(int x, int y) {
			
			ImageIcon ii = new ImageIcon(getClass().getResource("/images/expl.gif"));
			image = ii.getImage();
			image = Transparency.makeColorTransparent(image, Color.BLACK);
			width = image.getWidth(null);
			
			this.x = x;
			this.y = y;
			
			explosionTimerCount = 0;
		}
	}
}