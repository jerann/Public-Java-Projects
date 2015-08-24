import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

public class MissileManager {
	
	ArrayList<Missile> missiles;
	Timer missileTimer;
	
	boolean isValid;
	int validCount;
	
	public MissileManager() {
		
		missiles = new ArrayList<Missile>();
		
		//isValid allows for firing rate limit
		isValid = true;
		validCount = 0;
		
		missileTimer = new Timer();
		missileTimer.scheduleAtFixedRate(new missileTask(), 0, 20);
	}
	
	class missileTask extends TimerTask {
		
		public void run() {
			
			for (int i = 0; i < missiles.size(); ++i) {
				
				missiles.get(i).move(i);
			}
			
			//Firing rate limit
			if (!isValid) ++validCount;
			if (validCount == 20) {
				isValid = true;
				validCount = 0;
			}
		}
	}
	
	//Create new missile
	public void addMissile(int shipX, int shipY, int mouseX, int mouseY) {
		
		if (isValid) {
			missiles.add(new Missile(shipX, shipY, mouseX, mouseY));
			isValid = false;
		}
	}
	
	//Draw all missiles
	public void draw(Graphics2D g2d) {
		
		for (int i = 0; i < missiles.size(); ++i) {
			g2d.drawImage(missiles.get(i).getImage(), missiles.get(i).x,
					missiles.get(i).y, null);
		}
	}

	//Individual Missile
	public class Missile extends Sprite {
		
		public double dx, dy;
		
		//Create missile at ship coordinates
		public Missile(int shipX, int shipY, int mouseX, int mouseY) {
			
			x = shipX;
			y = shipY;
			int speedsq = 25;
			
			//Math to calculate direction of missile, blah blah blah...
			dy = (mouseY - shipY);
			dx = (mouseX - shipX);
			
			while (Math.sqrt(dx * dx + dy  * dy) < speedsq) { 
				
				dy *= 1.1;
				dx *= 1.1;
			}
			
			while (Math.sqrt(dx * dx + dy  * dy) > speedsq) { 
				
				dy *= .9;
				dx *= .9;
			}
			
			//Set missile image
			ImageIcon ii = new ImageIcon(getClass().getResource("/images/Missile.png"));
			image = Transparency.makeColorTransparent(ii.getImage(), Color.WHITE);
			
			width = image.getWidth(null);
		}
		
		//Update x and y position of missile
		public void move(int index) {
			
			x += dx;
			y += dy;
			
			if ((x < -getWidth()) || (x > Aspects.WIDTH)) missiles.remove(index);
			else if ((y < -getHeight() || (y > Aspects.HEIGHT))) missiles.remove(index);
		}
	}
}