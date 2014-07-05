package ruleml.client.component;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
  
public class SwingBrowser extends JFXPanel {
 
    private WebEngine engine;
 
    private final JLabel lblStatus = new JLabel();

    /**
     * Constructor
     */
    public SwingBrowser() {
        super();
        initComponents();
    }

    
    /**
     * Initialize browser's components
     */
    private void initComponents() {
        createScene();
        ActionListener al = new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
//                loadURL(txtURL.getText());
            }
        };
    }
 
    private void createScene() {
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
 
                WebView view = new WebView();
                engine = view.getEngine();
 
                engine.titleProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override 
                            public void run() {
//                                SimpleSwingBrowser.this.setTitle(newValue);
                            }
                        });
                    }
                });
 
                engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
                    @Override 
                    public void handle(final WebEvent<String> event) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override 
                            public void run() {
                                lblStatus.setText(event.getData());
                            }
                        });
                    }
                });
 
                engine.getLoadWorker()
                        .exceptionProperty()
                        .addListener(new ChangeListener<Throwable>() {
 
                            @Override
                            public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
                                if (engine.getLoadWorker().getState() == javafx.concurrent.Worker.State.FAILED) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override public void run() {
                                            JOptionPane.showMessageDialog(
                                                    null,
                                                    (value != null) ?
                                                    engine.getLocation() + "\n" + value.getMessage() :
                                                    engine.getLocation() + "\nUnexpected error.",
                                                    "Loading error...",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    });
                                }
                            }
                        });

                setScene(new Scene(view));
            }
        });
    }
 
    public void loadURL(final String url) {
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                String tmp = toURL(url);
 
                if (tmp == null) {
                    tmp = toURL("http://" + url);
                }
 
                engine.load(tmp);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
                return null;
        }
    }

    
    /**
     * Loads content into the browser.
     * @param content
     */
    public void loadContent(final String content) {
      Platform.runLater(new Runnable() {
        @Override 
        public void run() {
            engine.loadContent(content);
            try {
				engine.setUserStyleSheetLocation(new File("C:/Users/Adriana/Desktop/test.css").toURI().toURL().toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    });
    }
    

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            JFrame frame = new JFrame();
            SwingBrowser browser = new SwingBrowser();
            browser.setVisible(true);
            frame.getContentPane().add(browser);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(1024, 800));
            frame.pack();
            frame.setVisible(true);
            
            browser.loadURL("http://www.google.com");;
         }     
     });
    }
}
