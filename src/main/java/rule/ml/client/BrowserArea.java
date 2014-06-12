package rule.ml.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLEditorKit;

public class BrowserArea extends JPanel {

	 private final JEditorPane area = new JEditorPane();
	private String label;
	private JLabel contentLabel;
	private JSeparator contentSeparator;
	
	public BrowserArea(String label, boolean editable) {
		this.label = label;
		setOpaque(false);
		setLayout(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints();
		// Param name
		constr.gridx = 0;
		constr.gridy = 0;
		constr.anchor = GridBagConstraints.WEST;
		constr.fill = GridBagConstraints.NONE;
		constr.gridwidth = 1;
		constr.weightx = 0.0;
		constr.weighty = 0.0;
		constr.insets = new Insets(10, 5, 5, 10);
		
		
		contentLabel = new JLabel("");
		contentLabel.setFont(new Font(contentLabel.getFont().getFontName(), 
				contentLabel.getFont().getStyle(), contentLabel.getFont().getSize() + 3));
		add(contentLabel, constr);

		constr.gridy ++;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.gridwidth = 1;
		constr.weightx = 1.0;
		constr.weighty = 0.0;
		constr.insets = new Insets(0, 5, 5, 10);
		contentSeparator = new JSeparator();
		contentSeparator.setForeground(new Color(221, 221, 221));
		add(contentSeparator, constr);
		if (editable) {
			contentLabel.setText(label);
		} else {
			contentSeparator.setVisible(false);
		}
		constr.gridy ++;
		constr.anchor = GridBagConstraints.WEST;
		constr.fill = GridBagConstraints.BOTH;
		constr.gridwidth = 1;
		constr.weightx = 1.0;
		constr.weighty = 1.0;
		constr.insets = new Insets(1, 2, 1, 5);
		area.setEditorKit(new HTMLEditorKit());
		area.setEditable(editable);
		area.setForeground(Color.BLACK);
		area.setMinimumSize(new Dimension(500, 40));
		add(area, constr);
	}
	
	public void setContent(String content) {
		contentLabel.setText(label);
		area.setText(content);
		contentSeparator.setVisible(true);
	}
}
