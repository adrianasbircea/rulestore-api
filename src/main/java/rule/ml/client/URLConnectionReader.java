package rule.ml.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class URLConnectionReader {
	public static void main(String[] args) throws Exception {
		getAllRepositories("test");
		createNewRepository();
	}

	private static void getAllRepositories(String storeID) throws Exception {

		URL ruleML = new URL("http://localhost:8081/ruleml/test/repositories?name=test&description=My%20description");
		HttpURLConnection ruleMLConn = (HttpURLConnection) ruleML.openConnection();

		ruleMLConn.setRequestProperty("Content-Type", "application/xml");

		ruleMLConn.setRequestProperty("Content-Language", "en-US");  
		ruleMLConn.setRequestMethod("POST");
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ruleMLConn.getInputStream()));
		System.out.println("getHeaderField(0) " + ruleMLConn.getHeaderField(0));
		String inputLine;

		while ((inputLine = in.readLine()) != null) 
			System.out.println(inputLine);
		in.close();
	
	}
	
	private static void createNewRepository() throws Exception {
		URL ruleML = new URL("http://localhost:8081/ruleml/test/repositories?name=test&description=My%20description");
		HttpURLConnection ruleMLConn = (HttpURLConnection) ruleML.openConnection();

		ruleMLConn.setRequestProperty("Content-Type", "application/xml");

		ruleMLConn.setRequestProperty("Content-Language", "en-US");  
		ruleMLConn.setRequestMethod("POST");
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ruleMLConn.getInputStream()));
		System.out.println("getHeaderField(0) " + ruleMLConn.getHeaderField(0));
		String inputLine;

		while ((inputLine = in.readLine()) != null) 
			System.out.println(inputLine);
		in.close();
	
	
	}
}
