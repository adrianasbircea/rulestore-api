package ruleml.client.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ResponsePanel extends JPanel {

	private ContentArea headersContentArea;
	private ContentArea contentArea;
	private JTabbedPane fields;

	public ResponsePanel() {
		setBorder(new LineBorder(new Color(221, 221, 221), 1, true));
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(15, 10));
		
		// Title
		JLabel title = new JLabel("   Response") {
			@Override
			public void paint(Graphics g) {
				int width = getWidth();
			    int height = getHeight();

			    // Create the gradient paint
			    GradientPaint paint = new GradientPaint( 0, 0, Color.WHITE, 0, height, new Color(221, 221, 221), false );

			    // we need to cast to Graphics2D for this operation
			    Graphics2D g2d = ( Graphics2D )g;

			    // save the old paint
			    Paint oldPaint = g2d.getPaint();

			    // set the paint to use for this operation
			    g2d.setPaint( paint );

			    // fill the background using the paint
			    g2d.fillRect( 0, 0, width, height );

			    // restore the original paint
			    g2d.setPaint( oldPaint );

			    super.paint( g );
			}
		};
		title.setBorder(new EmptyBorder(0, 0, 0, 0));
		title.setFont(new Font(title.getFont().getFontName(), title.getFont().getStyle(), title.getFont().getSize() + 3));
		title.setPreferredSize(new Dimension(title.getPreferredSize().width, 40));
		add(title, BorderLayout.NORTH);
		
		fields = new JTabbedPane();
		fields.setFont(new Font("Sans Script", Font.PLAIN, fields.getFont().getSize() + 3));
		fields.setBorder(new EmptyBorder(10, 15, 5, 15));
		
		// Headers
		headersContentArea = new ContentArea("Headers", false, true, false);
		fields.addTab("Headers", headersContentArea);
		// Content 
		contentArea = new ContentArea("Content", false, false, true);
		fields.addTab("Body", contentArea);
		add(fields, BorderLayout.CENTER);
	}

	public void setStatus(String status) {
		headersContentArea.setStatus(status);
	}

	public void setHeaders(String content) {
		headersContentArea.setContent(content);
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyContent(String content) {
		contentArea.setContent(content);
	}

	public void clear() {
		headersContentArea.clear();
		contentArea.clear();
		fields.setSelectedIndex(0);
	}
}
