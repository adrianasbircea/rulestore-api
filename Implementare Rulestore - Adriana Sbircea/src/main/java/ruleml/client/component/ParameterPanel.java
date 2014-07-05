package ruleml.client.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 * A panel which represents a parameter (a query or a header).
 * 
 * @author Adriana
 */
@SuppressWarnings("serial")
public class ParameterPanel extends JPanel {
	
	/**
	 * Text field for the parameter name.
	 */
	PlaceholderTextField name;
	/**
	 * Text field for the parameter value.
	 */
	PlaceholderTextField value;
	
	/**
	 * Constructor.
	 */
	public ParameterPanel() {
		setOpaque(false);
		GridLayout gridBagLayout = new GridLayout(1, 2, 7, 2);
		setLayout(gridBagLayout);
		// Param name
		name = new PlaceholderTextField("");
		name.setPlaceholder("Name");
		name.setFont(new Font(name.getFont().getFontName(), name.getFont().getStyle(), 
				name.getFont().getSize() + 3));
		add(name);
		
		// Param value
		value = new PlaceholderTextField("");
		value.setPlaceholder("Value");
		value.setFont(new Font(value.getFont().getFontName(), value.getFont().getStyle(), value.getFont().getSize() + 3));
		add(value);
		setMinimumSize(new Dimension(getPreferredSize().width, 30));
		setPreferredSize(new Dimension(getPreferredSize().width, 30));
		name.setFocusable(true);
		name.requestFocus();
	}
	
	/**
	 * Obtain the name of the parameter.
	 * 
	 * @return The name of the parameter.
	 */
	public String getParamName() {
		return name.getText();
	}
	/**
	 * Obtain the value of the parameter.
	 * 
	 * @return The value of the parameter.
	 */
	public String getParamValue() {
		return value.getText();
	}
	
	/**
	 * Set the name of the parameter.
	 */
	public void setParamName(String paramName) {
		if (paramName != null) {
			name.setText(paramName);
		}
	}
	
	/**
	 * Set the value of the parameter.
	 */
	public void setParamValue(String paramValue) {
		if (paramValue != null) {
			name.setText(paramValue);
		}
	}
}
