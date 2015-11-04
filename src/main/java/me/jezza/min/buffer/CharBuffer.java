package me.jezza.min.buffer;

/**
 * @author jezza
 * @date Nov 4, 2015
 */
public class CharBuffer {
	public char[] data = null;
	public int length = 0;

	public CharBuffer() {
	}

	public CharBuffer(final int capacity) {
		data = new char[capacity];
	}

	public CharBuffer set(final char[] data) {
		this.data = data;
		length = data.length;
		return this;
	}

}
