package rule.ml.client;

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
	
	@Override
	protected void scrollByUnit(int paramInt) {
		super.scrollByUnit(paramInt);
	}
}
