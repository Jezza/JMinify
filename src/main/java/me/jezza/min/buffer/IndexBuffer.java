package me.jezza.min.buffer;

/**
 * @author jezza
 * @date Nov 4, 2015
 */
public class IndexBuffer {
	public int[] position = null;
	public int[] length = null;
	public byte[] type = null;
	public int size = 0;

	public IndexBuffer() {
	}

	public IndexBuffer(final int capacity, final boolean useTypeArray) {
		position = new int[capacity];
		length = new int[capacity];
		if (useTypeArray) {
			type = new byte[capacity];
		}
	}
}
