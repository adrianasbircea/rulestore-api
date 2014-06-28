package rule.ml.client.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ThinScrollBarUI extends BasicScrollBarUI {
	@Override
	protected void paintTrack(Graphics paramGraphics,
			JComponent paramJComponent, Rectangle paramRectangle) {
		// No track
	}
	
	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		Rectangle paramRectangle = new Rectangle(thumbBounds.x, thumbBounds.y, 5, thumbBounds.height);
		if ((paramRectangle.isEmpty()) || (!(this.scrollbar.isEnabled())))
			return;
		int i = paramRectangle.width;
		int j = paramRectangle.height;
		thumbColor = new Color(120, 120, 120);
		g.setColor(this.thumbColor);
		g.translate(paramRectangle.x, paramRectangle.y);
		g.drawRoundRect(0, 0, i - 1, j - 1, 15, 5);
		g.fillRoundRect(0, 0, i - 1, j - 1, 15, 5);
		g.translate(-paramRectangle.x, -paramRectangle.y);
	
	}
	@Override
	protected JButton createIncreaseButton(int paramInt) {
		return new JButton() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(0, 0);
				
			}
		};
	}
	
	@Override
	protected JButton createDecreaseButton(int paramInt) {
		return new JButton() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(0, 0);
				
			}
		};
	}
	
//	@Override
//	protected void scrollByUnit(int paramInt) {
//		super.scrollByUnit(paramInt);
//	}
}
