import java.awt.*;
import java.awt.image.*;

public class Transparency {
	
  //Returns image that replace pixels of 'color' with null pixels
	public static Image makeColorTransparent (Image image, final Color color) {
	
    ImageFilter filter = new RGBImageFilter() {
    	
    	//colorData holds the info of color in integer form
    	public int colorData = color.getRGB() | 0xFF000000;

    	public final int filterRGB(int x, int y, int rgb) {
    		
    		//Sets all pixels of 'color' (colorData) to null
    		if (( rgb | 0xFF000000) == colorData) {
          
    			return 0x00FFFFFF & rgb;
    		}
    		
    		//Returns original pixels that aren't 'color
    		else return rgb;
        }
      }; 
    
    //Create and return final image
    ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
    return Toolkit.getDefaultToolkit().createImage(ip);
    }
}