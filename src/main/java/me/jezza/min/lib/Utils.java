package me.jezza.min.lib;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Jezza
 */
public class Utils {
	public static final int EOL = -1;
	public static final int DEFAULT_BUFFER_SIZE = 4096;

	public static String toString(Reader in) throws IOException {
		char[] arr = new char[DEFAULT_BUFFER_SIZE];
		StringBuilder buffer = new StringBuilder();
		int numCharsRead;
		while ((numCharsRead = in.read(arr, 0, DEFAULT_BUFFER_SIZE)) != EOL)
			buffer.append(arr, 0, numCharsRead);
		return buffer.toString();
	}

}
