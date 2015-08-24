import java.awt.EventQueue;
import javax.swing.JFrame;

public class SpaceShooter extends JFrame {
	
	public SpaceShooter() {
	
		add(new Board());
		
		setResizable(true);
		pack();
		
		setTitle("Space Shooter");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			//WOOOOOOOOOOOOO!!!
			public void run() {
				JFrame ex = new SpaceShooter();
				ex.setVisible(true);
			}
		});
	}
}