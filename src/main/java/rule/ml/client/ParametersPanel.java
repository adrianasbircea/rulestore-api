package rule.ml.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ButtonUI;

/**
 * Panel which contains all the query parameters.
 * 
 * @author Adriana
 */
@SuppressWarnings("serial")
public class ParametersPanel extends JPanel {

	/**
	 * List with all the parameters from the panel.
	 */
	List<ParameterPanel> params = new ArrayList<ParameterPanel>();
	private JLabel jLabel;
	private JPanel paramsPanel;
	private GridBagConstraints constr;
	private JScrollPane sp;
	
	public ParametersPanel(String labelName) {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		jLabel = new JLabel(" ");
		jLabel.setOpaque(false);
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		// Button for add a new parameter
		JButton addParamButton = new JButton("  Add  ");
		addParamButton.setMinimumSize(new Dimension(70, 30));
		addParamButton.setPreferredSize(new Dimension(70, 30));
		addParamButton.setToolTipText("Add a new parameter.");
		addParamButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addParameter(paramsPanel, constr);
				sp.revalidate();
				sp.repaint();
			}
		});

		panel.add(addParamButton);
		panel.setOpaque(false);
		add(panel, BorderLayout.SOUTH);

		paramsPanel = new JPanel(new GridBagLayout());
		paramsPanel.setOpaque(false);
		sp = new JScrollPane(paramsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
		        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
			@Override
			public Dimension getPreferredSize() {
				int componentCount = getViewport().getComponentCount();
				
				return new Dimension(getSize().width, getComponentsSize((JComponent) getViewport().getComponent(0)));
			}

			private int getComponentsSize(JComponent parent) {
				int height = parent.getSize().height;
				
				for (int i = 0; i < parent.getComponentCount(); i++) {
					height += getComponentsSize((JComponent) parent.getComponent(i)); 
				}
				
				return height;
			}
		};
		sp.setOpaque(false);
		sp.getViewport().setOpaque(false);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 0;
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.insets = new Insets(5, 0, 0, 5);
		addParameter(paramsPanel, constr);
		
		sp.revalidate();
		sp.repaint();
		constr.gridy ++;
		add(sp, BorderLayout.CENTER);
		
//		setOpaque(false);
	}
	
	/**
	 * Add a new parameter to the current panel.
	 * 
	 * @param paramsPanel 	The panel containing all the parameters.
	 * @param constr 		The {@link GridBagConstraints} object.
	 */
	private void addParameter(final JPanel paramsPanel, GridBagConstraints constr) {
		paramsPanel.remove(jLabel);
		
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.weightx = 1.0;
		constr.weighty = 0.0;
		final JPanel p = new JPanel(new GridBagLayout());
		p.setOpaque(false);
		GridBagConstraints pconstr = new GridBagConstraints();
		pconstr.gridx = 0;
		pconstr.gridy = 0;
		pconstr.anchor = GridBagConstraints.WEST;
		pconstr.fill = GridBagConstraints.BOTH;
		pconstr.insets = new Insets(0, 10, 0, 2);
		pconstr.weightx = 1.0;
		pconstr.weighty = 1.0;
		
		final ParameterPanel parameterPanel = new ParameterPanel();
		p.add(parameterPanel, pconstr);
		params.add(parameterPanel);
		
		JButton btn = new JButton("   REMOVE   ");
		btn.setBorder(new EmptyBorder(5, 0, 0, 5));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				paramsPanel.remove(p);
				params.remove(parameterPanel);
				paramsPanel.repaint();
				sp.repaint();
			}
		});
		pconstr.gridx ++;
		pconstr.anchor = GridBagConstraints.WEST;
		pconstr.fill = GridBagConstraints.VERTICAL;
		pconstr.weightx = 0.0;
		pconstr.weighty = 1.0;
		pconstr.insets = new Insets(0, 0, 0, 10);
		p.add(btn, pconstr);
		paramsPanel.add(p, constr);
		
		constr.gridx = 0;
		constr.gridy ++;
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 1.0;
		constr.weighty = 1.0;
		paramsPanel.add(jLabel, constr);
		
		paramsPanel.validate();
		paramsPanel.repaint();
	}
}
