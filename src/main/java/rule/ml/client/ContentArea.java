package rule.ml.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

public class ContentArea extends JPanel {

	JTextArea area = new JTextArea();
	private JLabel statusCodeLabel;
	private String label;
	private JLabel contentLabel;
	private JLabel statusLabel;
	private JSeparator contentSeparator;
	private JSeparator statusSeparator;
	
	public ContentArea(String label, boolean editable, boolean addStatusLabel) {
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
		
		if (addStatusLabel) {
			statusLabel = new JLabel("");
			statusLabel.setFont(new Font(statusLabel.getFont().getFontName(), 
					statusLabel.getFont().getStyle(), statusLabel.getFont().getSize() + 3));
			add(statusLabel, constr);
			
			constr.gridy ++;
			constr.fill = GridBagConstraints.HORIZONTAL;
			constr.gridwidth = 1;
			constr.weightx = 1.0;
			constr.weighty = 0.0;
			constr.insets = new Insets(0, 5, 5, 10);
			statusSeparator = new JSeparator();
			statusSeparator.setForeground(new Color(221, 221, 221));
			statusSeparator.setVisible(false);
			add(statusSeparator, constr);
			
			statusCodeLabel = new JLabel();
			constr.gridx = 0;
			constr.gridy ++;
			constr.fill = GridBagConstraints.NONE;
			constr.gridwidth = 1;
			constr.weightx = 0.0;
			constr.weighty = 0.0;
			constr.insets = new Insets(5, 5, 15, 10);
			add(statusCodeLabel, constr);
			
			constr.gridy ++;
			constr.insets = new Insets(10, 5, 0, 10);
		}
		
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
		area.setLineWrap(true);
		area.setRows(7);
		area.setEditable(editable);
		area.setForeground(Color.BLACK);
		area.setMinimumSize(new Dimension(500, 40));
		JScrollPane sp = new JScrollPane(
				area, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(sp, constr);
	}
	
	public void setStatus(String statusValue) {
		if (statusCodeLabel != null) {
			statusLabel.setText("Status");
			statusCodeLabel.setText(statusValue);
			statusSeparator.setVisible(true);
		}
	}
	
	public void setContent(String content) {
		contentLabel.setText(label);
		area.setText(content);
		contentSeparator.setVisible(true);
	}

	public void clear() {
		if (statusCodeLabel != null) {
			statusLabel.setText("");
			statusCodeLabel.setText("");
			statusSeparator.setVisible(false);
		}
		
		contentLabel.setText("");
		area.setText("");
		contentSeparator.setVisible(false);
	}
}
