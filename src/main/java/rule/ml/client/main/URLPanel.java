package rule.ml.client.main;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Panel for the URL.
 * 
 * @author Adriana
 */
@SuppressWarnings("serial")
public class URLPanel extends JPanel {
	/**
	 * The parent panel.
	 */
	private MainPanel mainPanel;
	/**
	 * Combo which presents the HTPP methods.
	 */
	private JComboBox methodCombo;
	/**
	 * The input field for the URL.
	 */
	private JTextField urlTxtField;
	
	public URLPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
		
		// Method combobox
		setLayout(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 0;
		constr.anchor = GridBagConstraints.WEST;
		constr.fill = GridBagConstraints.VERTICAL;
		constr.gridwidth = 1;
		constr.weightx = 0.0;
		constr.weighty = 1.0;
		constr.insets = new Insets(0, 0, 0, 0);
		methodCombo = new JComboBox(new String[] {"GET", "POST", "PUT", "DELETE"});
		add(methodCombo, constr);
		
		// URL text field
		urlTxtField = new JTextField();
		urlTxtField.setPreferredSize(new Dimension(100, urlTxtField.getPreferredSize().height));
		urlTxtField.setBorder(new CompoundBorder(new LineBorder(MainPanel.COLOR.darker(), 1), new EmptyBorder(2, 7, 2, 2)));
		urlTxtField.setFont(new Font(urlTxtField.getFont().getFontName(), urlTxtField.getFont().getStyle(), urlTxtField.getFont().getSize() + 3));
		constr.gridx ++;
		constr.fill = GridBagConstraints.BOTH;
		constr.gridwidth = 1;
		constr.weightx = 1.0;
		constr.weighty = 1.0;
		add(urlTxtField, constr);
		
		// Button to send the request
		JButton sendBtn = new JButton("SEND");
		sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Cursor initialCursor = URLPanel.this.mainPanel.getCursor();
				try {
					// Set a wait cursor while the response is received
					URLPanel.this.mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					URLPanel.this.mainPanel.clear();
					URLPanel.this.mainPanel.sendRequest();
				} finally {
					URLPanel.this.mainPanel.setCursor(initialCursor);
				}

			}
		});
		
		constr.gridx ++;
		constr.fill = GridBagConstraints.VERTICAL;
		constr.gridwidth = 1;
		constr.weightx = 0.0;
		constr.weighty = 1.0;
		add(sendBtn, constr);
		setMinimumSize(new Dimension(300, 30));
		setPreferredSize(new Dimension(300, 30));
		urlTxtField.requestFocusInWindow();
	}
	
	/**
	 * Obtain the selected method.
	 * @return
	 */
	public String getMethod() {
		return (String) methodCombo.getSelectedItem();
	}
	
	public String getURL() {
		return urlTxtField.getText();
	}
}
