package ruleml.client.util;

public class ClientUtil {

	public static String correctURL(String url) {
		
		StringBuilder correctURL = new StringBuilder();
		
		for (int i = 0; i < url.length(); i++) {
			char ch = url.charAt(i);
			switch (ch) {
			case ' ':
				correctURL.append("%20");
				break;
			default:
				correctURL.append(ch);
				break;
			}
		}
		
		return correctURL.toString();
	}
	
	private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }
	
	private static boolean isUnsafe(char ch) {
        if (ch > 128 || ch < 0)
            return true;
        return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
    }
}
