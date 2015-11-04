package me.jezza.min.lib;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Jezza
 */
public class Utils {
	public static final int EOL = -1;

	public static String toString(Reader in) throws IOException {
		char[] arr = new char[8 * 1024];
		StringBuilder buffer = new StringBuilder();
		int numCharsRead;
		while ((numCharsRead = in.read(arr, 0, arr.length)) != EOL)
			buffer.append(arr, 0, numCharsRead);
		return buffer.toString();
	}

}
