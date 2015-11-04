package me.jezza.min.lib;

import java.io.IOException;

/**
 * @author Jezza
 */
@FunctionalInterface
public interface Input {
	String input() throws IOException;
}
