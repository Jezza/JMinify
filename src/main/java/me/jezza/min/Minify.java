package me.jezza.min;

import java.io.FileNotFoundException;

/**
 * @author jezza
 * @date Nov 4, 2015
 */
public class Minify {

	private Minify() {
		throw new IllegalStateException();
	}

	public static void main(final String[] args) throws FileNotFoundException {
		JavascriptNavigator read = new JavascriptReader("main();").read();
	}

	public static String js(CharSequence sequence) {
		return sequence.toString();
	}
}
