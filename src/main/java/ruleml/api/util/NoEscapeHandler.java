package ruleml.api.util;

import java.io.IOException;
import java.io.Writer;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

public class NoEscapeHandler implements CharacterEscapeHandler {

	private static NoEscapeHandler theInstance;



	public static NoEscapeHandler getInstance() {
		if (theInstance == null) {
			theInstance = new NoEscapeHandler();
		}

		return theInstance;
	}



	@Override
	public void escape(char[] buf, int start, int len, boolean isAttValue,
			Writer out) throws IOException {

		for (int i = start; i < start + len; i++) {
			char ch = buf[i];
			out.write(ch);
		}
	}
}
