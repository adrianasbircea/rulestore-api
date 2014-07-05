package ruleml.api.util;

/**
 * Util class for generating an unique ID.
 * 
 * @author Adriana
 */
public class IDUtil {
	/**
	 * Number of small letters.
	 */
	private static final int BASE = 'z' - 'a' + 1;
	/**
	 * An offset to compute the current time.
	 */
	private static final long OFFSET = 1286892000000L;
	/**
	 * The last timestamp used to generate an unique ID.
	 */
	private static long lastUsedTimestamp = 0;
	
	public static String generateID() {
		String uid = "";
		long time = System.currentTimeMillis() - OFFSET;
		
		if (time < lastUsedTimestamp) {
			time = lastUsedTimestamp + 1;
		}
		
		lastUsedTimestamp = time;
		int underscore = 0;
		while(time != 0) {
			int mod = (int) (time % BASE);
			char ch = (char) ('a' + mod);
			
			if (!uid.isEmpty()) {
				uid = uid + convertVowelToInt(ch);
			} else {
				uid += ch;
			}
			
			time = time / BASE;
			
			if (time > 0 && (uid.length() - underscore) % 5 == 0) {
				uid += '_';
				underscore ++;
			}
		}
		
		return uid;
	}

	/**
	 * Converts a vowel to a number.
	 * 
	 * @param ch The character to be converted.
	 * 
	 * @return Converted vowel or the given character if it's not a vowel.
	 */
	private static char convertVowelToInt(char ch) {
		char toRet = ch;
		
		switch (ch) {
		case 'a':
			toRet = '1';
			break;
		case 'e':
			toRet = '2';
			break;
		case 'i':
			toRet = '3';
			break;
		case 'o':
			toRet = '4';
			break;
		case 'u':
			toRet = '5';
			break;
		}
		
		return toRet;
	}
}
