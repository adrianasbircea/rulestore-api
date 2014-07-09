package ruleml.client.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ruleml.client.component.ParameterPanel;
import ruleml.client.component.RequestPanel;
import ruleml.client.component.ResponsePanel;

/**
 * Main panel for the client application.
 * 
 * @author Adriana
 */
@SuppressWarnings("serial")
public class MainPanel extends JPanel {

	private static final int NR_COLUMNS = 2;
	public static final Color COLOR = new Color(242, 242, 242);
	private URLPanel urlPanel;
	private RequestPanel requestPanel;
	private ResponsePanel responsePanel;

	public MainPanel() {
		setLayout(new GridBagLayout());
		setBackground(COLOR);
		GridBagConstraints constr = new GridBagConstraints();
		
		constr.gridx = 0;
		constr.gridy = 0;
		constr.anchor = GridBagConstraints.WEST;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.gridwidth = NR_COLUMNS;
		constr.weightx = 1.0;
		constr.weighty = 0.0;
		constr.insets = new Insets(15, 20, 0, 20);
		
		urlPanel = new URLPanel(this);
		add(urlPanel, constr);
		
		
		constr.gridx = 0;
		constr.gridy ++;
		constr.anchor = GridBagConstraints.WEST;
		constr.fill = GridBagConstraints.BOTH;
		constr.gridwidth = 1;
		constr.weightx = 0.5;
		constr.weighty = 1.0;
		constr.insets = new Insets(25, 20, 10, 20);
		JPanel p = new JPanel(new GridLayout(1, 2, 25, 5));
		requestPanel = new RequestPanel();
		p.add(requestPanel);
		
		responsePanel = new ResponsePanel();
		p.add(responsePanel);
		
		add(p, constr);
		urlPanel.requestFocusInWindow();
		setPreferredSize(new Dimension(800, 400));
		setVisible(true);
	}

	/**
	 * Send the request
	 */
	public void sendRequest() {
		DataOutputStream wr = null;
		BufferedReader in = null;
		// Obtain the URL
		String urlString = urlPanel.getURL();
		
		List<ParameterPanel> queryParams = requestPanel.getQueryParams();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < queryParams.size(); i++) {
			ParameterPanel parameterPanel = queryParams.get(i);
			String key = parameterPanel.getParamName();
			String value = parameterPanel.getParamValue();
			sb.append(key).append("=").append(value);
			if (i < queryParams.size() - 1) {
				sb.append("&");
			}
		}
		
		// Add the query parameters to the URL
		if (urlString.length() > 0 && sb.toString().trim().length() > 0) {
			urlString += "?" + sb.toString();
		}
		
		if (urlString.length() > 0) {

			URL url;
			System.out.println("url |" + urlString + "|");
			try {
				url = new URL(urlString);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setInstanceFollowRedirects(false); 
				
				// Request Headers
				List<ParameterPanel> requestHeaders = requestPanel.getHeadersParams();
				boolean headerAdd = false;
				for (int i = 0; i < requestHeaders.size(); i++) {
					ParameterPanel parameterPanel = requestHeaders.get(i);
					String key = parameterPanel.getParamName();
					String value = parameterPanel.getParamValue();
					if (key.trim().length() > 0 && value.trim().length() > 0) {
						connection.setRequestProperty(key.trim(), value.trim());
						headerAdd = true;
					}
				}
				
				if (!headerAdd) {
					connection.setRequestProperty("Content-type", "text/plain");
				}

				// Method
				connection.setRequestMethod(urlPanel.getMethod());
				String requestBodyText = requestPanel.getRequestBody();
				System.out.println("request body |" + requestBodyText + "|");
				if (requestBodyText.length() > 0) {
					connection.setRequestProperty("Content-Length", Integer.toString(requestBodyText.length()));
					connection.getOutputStream().write(requestBodyText.getBytes("UTF8"));
//					wr = new DataOutputStream(connection.getOutputStream ());
//					wr.writeBytes(requestBodyText);
//					wr.flush();
				}
				
				// The response
				String responseMessage = connection.getResponseMessage();
				processResponse(in, connection, responseMessage);
			} catch (MalformedURLException e) {
				JOptionPane.showMessageDialog(this, "Invalid URL");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
				if (wr != null) {
					try {
						wr.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "An URL must be provided!");
		}
	}

	private BufferedReader processResponse(BufferedReader in,
			HttpURLConnection connection, String responseMessage)
			throws IOException {
		System.out.println("response " + responseMessage);
		if (responseMessage != null) {
			responsePanel.setStatus((String.valueOf(connection.getResponseCode()) + " " + responseMessage));
		} else {
			responsePanel.setStatus(String.valueOf(connection.getResponseCode()));
		}
		
		Map<String, List<String>> headerFields = connection.getHeaderFields();
		Set<String> headers = headerFields.keySet();
		StringBuilder hds = new StringBuilder();
		for (String headerKey : headers) {
			if (headerKey != null) {
				hds.append(headerKey).append(": ");
				List<String> list = headerFields.get(headerKey);
				for (int i = 0; i < list.size(); i++) {
					hds.append(list.get(i));
					hds.append("; ");
				}
				hds.append("\n");
			}
		}
		
		if (hds.length() > 0) {
			responsePanel.setHeaders(hds.toString());
		}
		
		
		if (connection.getContentLength() > 0) {
			in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder body = new StringBuilder();
			while ((inputLine = in.readLine()) != null) { 
				body.append(inputLine).append("\n");
			}
			
			if (body.length() > 0) {
				responsePanel.setBodyContent(body.toString());
			}
		}
		return in;
	}
	
	public void clear() {
		responsePanel.clear();
	}
}
