import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Ship extends Sprite implements Aspects {
	
	public int health;
	public int killCount;
	
	public BufferedImage bufImg;
	public BufferedImage finalBuf;
	
	Timer shipTimer;
	
	//Velocity/acceleration components of ship
	double vx, vy, axp, axn, ayp, ayn, acc;
	int maxSpeed = 4;
	
	//Tracks which keys are down
	public boolean up, down, left, right;
	
	
	public Ship() {
		
		ImageIcon ii = new ImageIcon(getClass().getResource("/images/Ship.png"));
		
		image = ii.getImage();
		
		//Finally got this image to be included in the package
		try {
			bufImg = ImageIO.read(Ship.class.getResourceAsStream("/images/Ship.png"));
		}
		catch (IOException e) {}
		
		finalBuf = new BufferedImage(
			    bufImg.getWidth(), bufImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
		image = Transparency.makeColorTransparent(image, Color.WHITE);
		
		width = image.getWidth(null);
		height = image.getHeight(null);
		
		health = 5;
		
		//Set initial velocity/acceleration values
		vx = 0;
		vy = 0;
		axp = 0;
		axn = 0;
		ayp = 0;
		ayn = 0;
		acc = .15;
		
		resetPos();
		
		shipTimer = new Timer();
		shipTimer.scheduleAtFixedRate(new shipTask(), 0, 20);
	}
	
	class shipTask extends TimerTask {
		
		public void run() {
			
			move();
		}
	}
	
	public void move() {
		
		//Set acceleration based on which key is pressed
		if (up) {
			ayn = acc;
		}
		else ayn = 0;

		
		if (down) {
			ayp = acc;
		}
		else ayp = 0;

		
		if (left) {
			axn = acc;
		}
		else axn = 0;

		
		if (right) {
			axp = acc;
		}
		else axp = 0;

		vx += axp - axn;
		vy += ayp - ayn;
		
		//Keep the ship from exceeding maximum speed
		if (vx > maxSpeed) vx = maxSpeed;
		if (vx < -maxSpeed) vx = -maxSpeed;
		if (vy > maxSpeed) vy = maxSpeed;
		if (vy < -maxSpeed) vy = -maxSpeed;
		
		//Calculate new x and y based on kinematic equations (Yay physics!)
		x += vx + (0.5) * (axp - axn);
		y += vy + (0.5) * (ayp - ayn);
		
		//Keep ship from going off screen
		if (x >= Aspects.WIDTH - 37) {
			x = Aspects.WIDTH - 37;
			vx = 0;
		}
		
		if (x < 2) {
			x = 2;
			vx = 0;
		}
		
		if (y >= Aspects.HEIGHT - 37) {
			y = Aspects.HEIGHT - 37;
			vy = 0;
		}
		
		if (y < 2) {
			y = 2;
			vy = 0;
		}
	}
	
	public void slow(double amount) {
		
		vx *= amount;
		vy *= amount;
	}
	
	public void explode() {
		
		Board.explosionManager.addExplosion(x - (Board.explosionManager.width - getWidth()) / 2 + 15,
				y - (Board.explosionManager.width - getHeight()) / 2 + 15);
	}
	
	//Sets {up, down, left, right} booleans based on keypresses
	public void keyPressed(KeyEvent e) {
		
		switch (e.getKeyCode()) {
		
		case KeyEvent.VK_UP:	
			up = true;
			down = false;
			break;
			
		case KeyEvent.VK_DOWN:
			down = true;
			up = false;
			break;
			
		case KeyEvent.VK_LEFT:
			left = true;
			right = false;
			break;
			
		case KeyEvent.VK_RIGHT:
			right = true;
			left = false;
			break;
		}
	}
	
	//Sets {up, down, left, right} booleans based on keyreleases
	public void keyReleased(KeyEvent e) {
		
		switch (e.getKeyCode()) {
		
		case KeyEvent.VK_UP:	
			up = false;
			break;
			
		case KeyEvent.VK_DOWN:
			down = false;
			break;
			
		case KeyEvent.VK_LEFT:
			left = false;
			break;
			
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		}
	}
	
	//Initial ship position
	public void resetPos() {
		
		x = 355;
		y = 370;
	}
}