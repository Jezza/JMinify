package me.jezza.min.js;

import me.jezza.min.buffer.CharBuffer;
import me.jezza.min.buffer.IndexBuffer;
import me.jezza.min.lib.Input;
import me.jezza.min.lib.Utils;

import java.io.*;

import static me.jezza.min.js.ElementTypes.*;

/**
 * @author jezza
 * @date Nov 4, 2015
 */
public class JavascriptReader {
	public static final char NEW_LINE = '\n';

	private int position = 0;
	private int elementIndex = 0;

	private final CharBuffer dataBuffer;
	private IndexBuffer elementBuffer;
	private final Input in;

	public JavascriptReader(final String string) {
		this(() -> string);
	}

	public JavascriptReader(final File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	public JavascriptReader(final Reader in) {
		this(() -> Utils.toString(in));
	}

	public JavascriptReader(final Input in) {
		if (in == null) {
			throw new NullPointerException("Input is null.");
		}
		this.in = in;
		dataBuffer = new CharBuffer();
	}

	public JavascriptNavigator read() {
		String input;
		try {
			input = in.input();
		} catch (final IOException e) {
			throw exception("Failed to read input!", e);
		}

		dataBuffer.set(input.toCharArray());
		elementBuffer = new IndexBuffer(dataBuffer.length, true);

		for (position = 0; position < dataBuffer.length; position++) {
			final char c = dataBuffer.data[position];
			if (Character.isAlphabetic(c)) {
				position = consumeNamespace();
				continue;
			}
			if (Character.isDigit(c)) {
				position = consumeNumber();
				continue;
			}
			switch (c) {
				case '\'':
				case '"':
					consumeString();
					break;
				//
				case '{':
					setElementData(elementIndex++, BODY_START, position);
					break;
				case '}':
					setElementData(elementIndex++, BODY_END, position);
					break;
				case '(':
					setElementData(elementIndex++, FUNCTION_START, position);
					break;
				case ')':
					setElementData(elementIndex++, FUNCTION_END, position);
					break;
				case '[':
					setElementData(elementIndex++, ARRAY_START, position);
					break;
				case ']':
					setElementData(elementIndex++, ARRAY_END, position);
					break;
				//
				case ':':
					setElementData(elementIndex++, COLON, position);
					break;
				case '?':
					setElementData(elementIndex++, QUESTION, position);
					break;
				case ';':
					setElementData(elementIndex++, SEMI_COLON, position);
					break;
				case ',':
					setElementData(elementIndex++, COMMA, position);
					break;
				case '.':
					setElementData(elementIndex++, PERIOD, position);
					break;
				//
				case '=':
					setElementData(elementIndex++, EQUAL, position);
					break;
				case '+':
					setElementData(elementIndex++, ADD, position);
					break;
				case '-':
					setElementData(elementIndex++, SUB, position);
					break;
				case '*':
					setElementData(elementIndex++, MUL, position);
					break;
				//
				case '/':
					skipComment();
					break;
				//
				case '%':
					setElementData(elementIndex++, REM, position);
					break;
				case '&':
					setElementData(elementIndex++, AMP, position);
					break;
				case '|':
					setElementData(elementIndex++, PIPE, position);
					break;
				case '^':
					setElementData(elementIndex++, CAR, position);
					break;
				case '<':
					setElementData(elementIndex++, LESS, position);
					break;
				case '>':
					setElementData(elementIndex++, GREAT, position);
					break;
				case '!':
					setElementData(elementIndex++, NOT, position);
					break;
				case '~':
					setElementData(elementIndex++, TILDE, position);
					break;
				default:
			}
		}

		elementBuffer.size = elementIndex;
		return new JavascriptNavigator(dataBuffer, elementBuffer);
	}

	private void consumeString() {
		int tempPos = position;
		boolean endOfStringFound = false;
		while (!endOfStringFound) {
			if (++tempPos >= dataBuffer.length) {
				throw exception("Unexpected end of String");
			}
			switch (dataBuffer.data[tempPos]) {
				case '\'':
				case '"':
					endOfStringFound = dataBuffer.data[tempPos - 1] != '\\';
					break;
				default:
			}
		}
		setElementData(elementIndex++, STRING, position, tempPos - position + 1);
		position = tempPos;
	}

	private void skipComment() {
		if (position + 1 >= dataBuffer.length) {
			setElementData(elementIndex++, DIV, position);
			return;
		}
		switch (dataBuffer.data[++position]) {
			case '*':
				position = skipBlockComment();
				break;
			case '/':
				position = skipLineComment();
				break;
			default:
				setElementData(elementIndex++, DIV, --position);
		}
	}

	private int skipBlockComment() {
		int tempPos = position;
		if (tempPos + 1 >= dataBuffer.length)
			throw exception("Unexpected end of block comment.", tempPos);
		while (++tempPos < dataBuffer.length) {
			if (tempPos + 1 >= dataBuffer.length)
				throw exception("Unexpected end of block comment.", tempPos);
			switch (dataBuffer.data[tempPos]) {
				case '*':
					if (dataBuffer.data[tempPos + 1] == '/')
						return tempPos + 1;
				default:
			}
		}
		return tempPos;
	}

	private int skipLineComment() {
		int tempPos = position;
		while (++tempPos < dataBuffer.length) {
			switch (dataBuffer.data[tempPos]) {
				case NEW_LINE:
					return tempPos;
				default:
			}
		}
		return tempPos;
	}

	private int consumeNamespace(){
		int tempPos = position;
		while (++tempPos < dataBuffer.length) {
			if (!Character.isAlphabetic(dataBuffer.data[tempPos])) {
				setElementData(elementIndex++, NAMESPACE, this.position, tempPos - this.position);
				return tempPos - 1;
			}
		}
		setElementData(elementIndex++, NAMESPACE, this.position, tempPos - this.position);
		return tempPos;
	}
	private int consumeNumber() {
		int tempPos = position;
		while (++tempPos < dataBuffer.length) {
			if (!Character.isDigit(dataBuffer.data[tempPos])) {
				setElementData(elementIndex++, NUMBER, this.position, tempPos - this.position);
				return tempPos - 1;
			}
		}
		setElementData(elementIndex++, NUMBER, this.position, tempPos - this.position);
		return tempPos;
	}

	private void setElementData(final int index, final byte type, final int position) {
		elementBuffer.type[index] = type;
		elementBuffer.position[index] = position;
		elementBuffer.length[index] = 1;
	}

	private void setElementData(final int index, final byte type, final int position, final int length) {
		elementBuffer.type[index] = type;
		elementBuffer.position[index] = position;
		elementBuffer.length[index] = length;
	}

	private RuntimeException exception(final String message) {
		throw new RuntimeException(message + ':' + getPositionString());
	}

	private RuntimeException exception(final String message, final Throwable cause) {
		throw new RuntimeException(message + ':' + getPositionString(), cause);
	}

	private RuntimeException exception(final String message, final int position) {
		throw new RuntimeException(message + ':' + getPositionString(position));
	}

	@SuppressWarnings("unused")
	private RuntimeException exception(final String message, final int position, final Throwable cause) {
		throw new RuntimeException(message + ':' + getPositionString(position), cause);
	}

	private String getPositionString() {
		return getPositionString(position);
	}

	private String getPositionString(final int position) {
		int line = 1;
		int index = 0;

		for (int i = 0; i < position; i++) {
			if (dataBuffer.data[i] == NEW_LINE) {
				line++;
				index = 0;
			} else {
				index++;
			}
		}

		return String.format("Line #%s, Char #%s", Integer.toString(line), Integer.toString(index));
	}
}
