package rule.ml.client;
import java.awt.Color;
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
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * Panel for the URL.
 * 
 * @author Adriana
 */
@SuppressWarnings("serial")
public class URLPanel extends JPanel {

	private class MyComboBoxEditor extends BasicComboBoxEditor {
		public MyComboBoxEditor() {
			this.editor = new JTextField("", 9) {
				public void setText(String paramString) {
					if (getText().equals(paramString))
						return;
					super.setText(paramString);
				}

				public Dimension getPreferredSize() {
					Dimension localDimension = super.getPreferredSize();
					localDimension.height += 4;
					return localDimension;
				}

				public Dimension getMinimumSize() {
					Dimension localDimension = super.getMinimumSize();
					localDimension.height += 4;
					return localDimension;
				}
			};
			this.editor.setBorder(new EmptyBorder(0, 0, 0, 0));
			editor.setBackground(Color.BLACK);
		}
	};
	private MainPanel mainPanel;
	private JComboBox methodCombo;
	private JTextField urlTxtField;

	public URLPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
		
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
		
		methodCombo = new JComboBox(new String[] {"GET", "POST", "DELETE"});
		
		methodCombo.setEditor(new MyComboBoxEditor());
		
		int componentCount = methodCombo.getComponentCount();
		for (int i = 0; i < componentCount; i++) {
			System.out.println(methodCombo.getComponent(i).getClass());
			if (methodCombo.getComponent(i) instanceof JButton) {
				((JButton)methodCombo.getComponent(i)).setBorderPainted(false);
			}
		}
		add(methodCombo, constr);
		
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
		
		JButton sendBtn = new JButton("SEND");
		sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				URLPanel.this.mainPanel.clear();
				URLPanel.this.mainPanel.sendRequest();
				
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
