import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PointHoverManager {
	
	ArrayList<PointHover> pointHovers;
	
	Timer pointHoverTimer;
	
	public PointHoverManager() {
		
		pointHovers = new ArrayList<PointHover>();
		
		pointHoverTimer = new Timer();
		pointHoverTimer.scheduleAtFixedRate(new pointHoverTask() , 0, 20);
	}
	
	public void draw(Graphics2D g2d) {
		
		Font pointFont = new Font("Helvetica", Font.BOLD, 12);
		g2d.setFont(pointFont);
		g2d.setColor(Color.WHITE);
		
		for (int i = 0; i < pointHovers.size(); ++i) {
			g2d.drawString("+" + pointHovers.get(i).points, pointHovers.get(i).x,
					pointHovers.get(i).y);
		}
	}
	
	public void addPointHover (int x, int y, int points) {
		
		pointHovers.add(new PointHover(x, y, points));
	}
	
	class pointHoverTask extends TimerTask {
		
		public void run() {
			
			for (int i = 0; i < pointHovers.size(); ++i) {
				pointHovers.get(i).y -= pointHovers.get(i).dy;
				
				if (pointHovers.get(i).y <= pointHovers.get(i).finalY) {
					pointHovers.remove(i);
				}
			}
		}
	}
	
	protected class PointHover {
		
		int x, y, finalY, points;
		int dy;
		
		public PointHover(int x, int y, int points) {
			
			this.x = x;
			this.y = y;
			this.points = points;
			
			finalY = this.y - 25;
			dy = 1;
		}
	}
}