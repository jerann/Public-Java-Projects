import java.awt.Color;
import javax.swing.ImageIcon;

public class Heart extends Sprite {
	
	public Heart() {
	
	//Gets heart image
	ImageIcon ii = new ImageIcon(getClass().getResource("/images/Heart.png"));
	image = ii.getImage();
	image = Transparency.makeColorTransparent(image, Color.WHITE);
	}
}