import java.awt.Image;
import java.awt.Rectangle;

//Very straightforward - abstract base class for all visible objects
public class Sprite {
	
	protected int x, y;
	public double dx, dy;
	protected int width, height;

	protected Image image;
	
	public void setX(int x) {
		
		this.x = x;
	}
	
	public int getX() {
		
		return x;
	}
	
	public void setY(int y) {
		
		this.y = y;
	}
	
	public int getY() {
		
		return y;
	}
	
	public int getWidth() {
		
		return width;
	}
	
	public int getHeight() {
		
		return height;
	}
	
	Image getImage() {
		
		return image;
	}
	
	//Used for collision detection
	//Would like to use tight collision rather than rectangle bounds...
	Rectangle getBounds() {
		
		return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
	}
}