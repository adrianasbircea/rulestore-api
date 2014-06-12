package rule.ml.client;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main frame class for Rulestore API Client application.
 * 
 * @author Adriana
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	/**
	 * Constructor.
	 */
	public MainFrame() {
		super("Rulestore API Client");
		
		try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    } 
    catch (UnsupportedLookAndFeelException e) {
       // handle exception
    }
    catch (ClassNotFoundException e) {
       // handle exception
    }
    catch (InstantiationException e) {
       // handle exception
    }
    catch (IllegalAccessException e) {
       // handle exception
    }
		
		
		ImageIcon imageIcon = new ImageIcon(new File("images/main_frame.png").getPath(), "desc");
		setIconImage(imageIcon.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().add(new MainPanel());
		
		pack();
		setResizable(true);
	}
	
	
	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}
}
