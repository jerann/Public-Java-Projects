import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Board extends JPanel implements Aspects {
	
	//Objects needed for the game
	WaveEditor waveEditor;
	static Ship ship;
	static AlienManager alienManager;
	static MissileManager missileManager;
	static ExplosionManager explosionManager;
	static PointHoverManager pointHoverManager;
	static PurpBossManager purpBossManager;
	
	static int wave;
	static int score;
	static int monstersOnBoard;
	
	//Needed for board;
	int textTimeCounter;
	boolean waveComplete;
	
	//Background images - will add more for different levels
	private Image mmenuBack;
	private Image ingameBack;
	
	//Misc sprites/objects
	Heart heart;
	Timer timer;
	
	//Needed for detecting mouse motion
	public Point mousePos;
	private double currentAngle;
	
	//Current state of the game
	enum State {
		MMENU, INGAME, GAMEOVER
	}
	static State currentState;
	
	
	public Board() {
		
		//Listen to mouse clicks and motion
		addMouseListener(new mouseAdapter());
		addMouseMotionListener(new mouseMotionAdapter());
		
		//Listen to keyboard
		addKeyListener(new keyAdapter());
		
		//JPanel settings
		setFocusable(true);
		setDoubleBuffered(true);
		setPreferredSize(new Dimension(Aspects.WIDTH, Aspects.HEIGHT));
		
		//Begin at main menu
		currentState = State.MMENU;
	}
	
	public void gameInit() {
		
		//Initialize board variables
		wave = 1;
		score = 0;
		monstersOnBoard = 0;
		
		textTimeCounter = 0;
		waveComplete = true;
		
		//Initialize game objects
		waveEditor = new WaveEditor();
		ship = new Ship();
		heart = new Heart();
		missileManager = new MissileManager();
		explosionManager = new ExplosionManager();
		pointHoverManager = new PointHoverManager();
		alienManager = new AlienManager();
		purpBossManager = new PurpBossManager();
		
		//Begin event timer
		timer = new Timer();
		timer.scheduleAtFixedRate(new ScheduleTask(), 100, 20);
	}
	
	public void paint(Graphics g) {
		
		//Set up rendering for smooth edges
		Graphics2D g2d = (Graphics2D) g.create();
		
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		switch (currentState) {
		
		case MMENU:
			
			//Draw main menu background
			ImageIcon iback = new ImageIcon(getClass().getResource("/images/mmenu.png"));
			mmenuBack = iback.getImage();
			g2d.drawImage(mmenuBack, 0, 0, this);
			break;
		
		
		case INGAME:
			
			//Draw Background - will change depending on level (eventually)
			ImageIcon ii = new ImageIcon(getClass().getResource("/images/back1.png"));
			ingameBack = ii.getImage();
			g2d.drawImage(ingameBack, 0, 0, this);
			
			//Draw Score
			String strScore = "Score: " + score;
			Font dispFont = new Font("Helvetica", Font.BOLD, 24);
			FontMetrics dispMetr = getFontMetrics(dispFont);
			g2d.setFont(dispFont);
			g2d.setColor(Color.WHITE);
			g2d.drawString(strScore, (Aspects.WIDTH - 
					dispMetr.stringWidth(strScore)) / 2, 30);
			
			//Draw Health
			for (int i = 0; i < ship.health; ++i) {
				g2d.drawImage(heart.getImage(), Aspects.WIDTH - 40 * (i + 1), 5, this);
			}
			
			//Calculate ship angle
			currentAngle = (Math.atan((mousePos.getY() - ship.getY() - 17)/
					(mousePos.getX() - ship.getX() - 17)));
			
			if (mousePos.getX() < ship.getX() + 17) {
				currentAngle += Math.PI;
			}
			
			//Rotate ship image
			AffineTransform tx = new AffineTransform();
			tx.translate(ship.getX(), ship.getY());
			tx.rotate(currentAngle + Math.PI/2, ship.finalBuf.getWidth() / 2, ship.finalBuf.getHeight() / 2);
			
			//Draw ship, missiles, explosions, pointHovers
			g2d.drawImage(ship.getImage(), tx, this);
			missileManager.draw(g2d);
			explosionManager.draw(g2d);
			pointHoverManager.draw(g2d);
			
			//Draw Monsters
			if (!waveComplete) {
				alienManager.draw(g2d);
				purpBossManager.draw(g2d);
			}
			
			//Draw wave count
			else {
				String strWave = "Wave " + wave;
				g2d.setFont(dispFont);
				g2d.drawString(strWave, (Aspects.WIDTH - dispMetr.stringWidth(strWave)) / 2,
						Aspects.HEIGHT / 2);
			}
		
			break;
			
		case GAMEOVER:
			
			//Draw game over background
			ii = new ImageIcon(getClass().getResource("/images/back1.png"));
			ingameBack = ii.getImage();
			g2d.drawImage(ingameBack, 0, 0, this);
			
			//Finish the explosions and point drawings
			explosionManager.draw(g2d);
			pointHoverManager.draw(g2d);
			
			//Displays final score and game over message
			String gmOverScore = "Final Score: " + score;
			String strOver = "Game Over";
			String strResMsg = "Click anywhere to play again";
			
			Font dispOverFont = new Font("Helvetica", Font.BOLD, 24);
			FontMetrics dispOverMetr = getFontMetrics(dispOverFont);
			g2d.setFont(dispOverFont);
			g2d.setColor(Color.WHITE);
			g2d.drawString(gmOverScore, (Aspects.WIDTH - dispOverMetr.stringWidth(gmOverScore))
					/ 2, Aspects.HEIGHT / 2 - 100);
			g2d.drawString(strOver, (Aspects.WIDTH - dispOverMetr.stringWidth(strOver))
					/ 2, Aspects.HEIGHT / 2);
			g2d.drawString(strResMsg, (Aspects.WIDTH - dispOverMetr.stringWidth(strResMsg))
					/ 2, Aspects.HEIGHT / 2 + 100);
			break;
		}
	}
	
	//Runs at every tick of the timer
	class ScheduleTask extends TimerTask {
		
		public void run() {
			
			//Add to wave text counter
			if (waveComplete) {
				++textTimeCounter;
			}
			//Check wave status
			else {
				if (monstersOnBoard == 0) {
					++wave;
					waveComplete = true;
				}
			}
			
			//Create new wave
			if (textTimeCounter >= 100) {
				waveComplete = false;
				textTimeCounter = 0;
				
				waveEditor.createWave(wave);
			}
			
			//Check if dead
			if (ship.health < 1) currentState = State.GAMEOVER;
			
			//Redraw Screen
			repaint();
		}
	}
	
	//Sends key press information to Ship
	private class keyAdapter extends KeyAdapter {
		
		public void keyPressed(KeyEvent e) {
			ship.keyPressed(e);
		}
		
		public void keyReleased(KeyEvent e) {
			ship.keyReleased(e);
		}
	}
	
	//Gets mouse motion
	public class mouseMotionAdapter implements MouseMotionListener {
		
		public mouseMotionAdapter() {}
		
		public void mouseMoved(MouseEvent e) {
			
			mousePos = e.getPoint();
		}
		
		public void mouseDragged(MouseEvent e) {}
	}
	
	
	//Gets mouse clicks
	public class mouseAdapter implements MouseListener {
		
		public mouseAdapter() {
			
			mousePos = new Point(0,0);
		}
		
		public void mouseClicked(MouseEvent e) {}
		
		public void mouseEntered(MouseEvent e) {}
		
		public void mouseExited(MouseEvent e) {}
		
		public void mousePressed(MouseEvent e) {
			
			switch (currentState) {
			
				case MMENU:
					currentState = State.INGAME;
					gameInit();
					repaint();
					break;
				
				case INGAME:
		
					missileManager.addMissile((int) (ship.getX() + 17 + 17 * Math.cos(currentAngle)), 
							(int) (ship.getY() + 17 + 17 * Math.sin(currentAngle)), mousePos.x, mousePos.y);
					break;
					
				case GAMEOVER:
					currentState = State.INGAME;
					timer.cancel();
					gameInit();
					break;
			}
		}
		
		public void mouseReleased(MouseEvent e) {}
	}
}
