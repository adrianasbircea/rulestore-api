package rule.ml.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class RequestPanel extends JPanel {

	private ParametersPanel queryParamsPanel;
	private ParametersPanel headerParamsPanel;
	private ContentArea contentArea;

	public RequestPanel() {
		setBorder(new LineBorder(new Color(221, 221, 221), 1, true));
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(15, 10));
		
		// Title
		JLabel title = new JLabel("   Request") {
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
		
		JTabbedPane fields = new JTabbedPane();
		fields.setFont(new Font("Sans Script", Font.PLAIN, fields.getFont().getSize() + 3));
		fields.setBorder(new EmptyBorder(10, 15, 5, 15));
		
		// Query params
		queryParamsPanel = new ParametersPanel("Query params");
		fields.addTab("Query", queryParamsPanel);
		// Headers
		headerParamsPanel = new ParametersPanel("Headers");
		fields.addTab("Headers", headerParamsPanel);
		// Content 
		contentArea = new ContentArea("Content", true, false);
		fields.addTab("Body", contentArea);
		add(fields, BorderLayout.CENTER);
	}
	
	public List<ParameterPanel> getQueryParams() {
		return queryParamsPanel.params;
	}
	
	public List<ParameterPanel> getHeadersParams() {
		return headerParamsPanel.params;
	}
	
	public String getRequestBody() {
		return contentArea.area.getText();
	}
}
