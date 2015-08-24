import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
	
	//Max/set values for the board
	private final int B_WIDTH = 300;
	private final int B_HEIGHT = 300;
	private final int DOT_SIZE = 10;
	private final int ALL_DOTS = 900;
	private final int RAND_POS = 29;
	private final int DELAY = 140;
	
	private final int x[] = new int[ALL_DOTS];
	private final int y[] = new int[ALL_DOTS];
	
	private int dots;
	private int apple_x;
	private int apple_y;
	
	private boolean leftDirection = false;
	private boolean rightDirection = true;
	private boolean upDirection = false;
	private boolean downDirection = false;
	private boolean mainMenu = true;
	private boolean inGame = false;
	
	private Timer timer;
	private Image ball;
	private Image apple;
	private Image head;
	
	public Board() {
		
		addKeyListener(new TAdapter());
		setBackground(Color.black);
		setFocusable(true);
		
		setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
		setDoubleBuffered(true);
		loadImages();
		initGame();
	}
	
	//Grab images
	private void loadImages() {
		
		ImageIcon iid = new ImageIcon(getClass().getResource("/images/dot.png"));
		ball = iid.getImage();
		
		ImageIcon iia = new ImageIcon(getClass().getResource("/images/apple.png"));
		apple = iia.getImage();
		
		ImageIcon iih = new ImageIcon(getClass().getResource("/images/head.png"));
		head = iih.getImage();
	}
	
	//Begin game
	private void initGame() {
		
		dots = 3;
		
		for (int z = 0; z < dots; z++) {
			x[z] = 50 - z * 10;
			y[z] = 50;
		}
		
		locateApple();
		
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		doDrawing(g);
	}
	
	private void doDrawing(Graphics g) {
		
		//Draw main menu screen
		if (mainMenu) {
			String str_Snake = "SNAKE";
			String str_by = "by Jeran Norman";
			String dir_1 = "Use the arrow keys to collect the apples.";
			String dir_2 = "Don't run in to yourself or the walls!";
			String dir_3 = "Press any arrow key to begin...";
			
			g.setColor(Color.white);
			Font huge = new Font("Helvetica", Font.BOLD, 40);
			Font small = new Font("Helvetica", Font.BOLD, 14);
			FontMetrics metr_huge = getFontMetrics(huge);
			FontMetrics metr_small = getFontMetrics(small);
			
			g.setFont(huge);
			g.drawString(str_Snake, (B_WIDTH - metr_huge.stringWidth(str_Snake)) / 2, 75);
			g.setFont(small);
			g.drawString(str_by, (B_WIDTH - metr_small.stringWidth(str_by)) / 2, 105);
			g.drawString(dir_1, (B_WIDTH - metr_small.stringWidth(dir_1)) / 2, 150);
			g.drawString(dir_2, (B_WIDTH - metr_small.stringWidth(dir_2)) / 2, 175);
			g.drawString(dir_3, (B_WIDTH - metr_small.stringWidth(dir_3)) / 2, 225);
		}
		
		//Draw in-game screen
		else if (inGame) {
			
			g.drawImage(apple, apple_x, apple_y, this);
			
			String Score = String.valueOf(dots - 3);
			Font large = new Font("Helvetica", Font.BOLD, 32);
			FontMetrics metr = getFontMetrics(large);
			
			g.setColor(Color.white);
			g.setFont(large);
			g.drawString(Score, (B_WIDTH - metr.stringWidth(Score)) / 2, B_HEIGHT / 2);
			
			//Draw each dot in Snake matrix
			for (int z = 0; z < dots; z++) {
				if (z == 0) {
					g.drawImage(head, x[z], y[z], this);
				} else {
					g.drawImage(ball, x[z], y[z], this);
				}
			}
			
			Toolkit.getDefaultToolkit().sync();
			
		} else {
			
			gameOver(g);
		}
	}
	
	private void gameOver(Graphics g) {
		
		String msg = "Game Over";
		String fScore = "Final Score: " + String.valueOf(dots - 3);
		String playAgain = "[R]estart?";
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);
		
		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(fScore, (B_WIDTH - metr.stringWidth(fScore)) / 2, B_HEIGHT / 2 - 30);
		g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
		g.drawString(playAgain, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 + 30);
	}
	
	private void restart() {
		
		leftDirection = false;
		rightDirection = true;
		upDirection = false;
		downDirection = false;
		
		inGame = true;
		timer.stop();
		
		initGame();
	}
	
	private void checkApple() {
		
		if ((x[0] == apple_x) && (y[0] == apple_y)) {
			
			dots++;
			locateApple();
		}
	}
	
	private void move() {
		
		for (int z = dots; z > 0; z--) {
			x[z] = x[z - 1];
			y[z] = y[z - 1];
		}
		
		if (leftDirection) x[0] -= DOT_SIZE;
		if (rightDirection) x[0] += DOT_SIZE;
		if (upDirection) y[0] -= DOT_SIZE;
		if (downDirection) y[0] += DOT_SIZE;
	}
	
	//See if snake collides with wall, self, or apple
	private void checkCollision() {
		
		for (int z = dots; z > 0; z--) {
			
			if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
				inGame = false;
			}
		}
		
		if (y[0] >= B_HEIGHT) inGame = false;
		if (y[0] < 0) inGame = false;
		if (x[0] >= B_WIDTH) inGame = false;
		if (x[0] < 0) inGame = false;
		//if (!inGame) timer.stop();
	}
	
	//Set new apple location
	private void locateApple() {
		
		int r = (int) (Math.random() * RAND_POS);
		apple_x = ((r * DOT_SIZE));
		
		r = (int) (Math.random() * RAND_POS);
		apple_y = ((r * DOT_SIZE));
		
		for (int i = 0; i < dots; i++) {
			if ((apple_x == x[i]) && (apple_y == y[i])) {
				locateApple();
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (mainMenu) {

		}
		
		else if (inGame) {
			
			checkApple();
			checkCollision();
			move();
		}
		
		repaint();
	}
	
	//Manages keyboard input...
	private class TAdapter extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			int key = e.getKeyCode();
			
			//...in main menu
			if (mainMenu) {
				if ((key == KeyEvent.VK_LEFT) || (key == KeyEvent.VK_RIGHT)
						|| (key == KeyEvent.VK_UP) || (key == KeyEvent.VK_DOWN)) {
					inGame = true;
					mainMenu = false;
				}
			}
			
			//...if in game
			else if (inGame) {
				if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
	                leftDirection = true;
	                upDirection = false;
	                downDirection = false;
	            }
	
	            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
	                rightDirection = true;
	                upDirection = false;
	                downDirection = false;
	            }
	
	            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
	                upDirection = true;
	                rightDirection = false;
	                leftDirection = false;
	            }
	
	            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
	                downDirection = true;
	                rightDirection = false;
	                leftDirection = false;
	            }
			}
			
			//...if game over
			else {
				if (key == KeyEvent.VK_R) {
					restart();
				}
			}
		}
	}
}
