package me.jezza.min;

import me.jezza.min.buffer.CharBuffer;
import me.jezza.min.buffer.IndexBuffer;
import me.jezza.min.lib.Input;
import me.jezza.min.lib.Utils;

import java.io.*;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

import static me.jezza.min.ElementTypes.*;

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

		final long start = System.currentTimeMillis();

		for (position = 0; position < dataBuffer.length; position++) {
			final char c = dataBuffer.data[position];
			if (Character.isAlphabetic(c)) {
				position = consume(Character::isAlphabetic, this::consumeNamespace);
				continue;
			}
			if (Character.isDigit(c)) {
				position = consume(Character::isAlphabetic, this::consumeNumber);
				continue;
			}
			switch (c) {
				case ':':
					setElementData(elementIndex++, COLON, position);
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
					//
				case '/':
					skipComment();
					break;
					//
				case '<':
					setElementData(elementIndex++, LESSER, position);
					break;
				case '>':
					setElementData(elementIndex++, GREATER, position);
					break;
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
				case '\'':
				case '"':
					consumeString();
					break;
				default:
			}
		}

		final long finish = System.currentTimeMillis();
		System.out.println(finish - start);

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
		if (position + 1 > dataBuffer.length) {
			position = dataBuffer.length;
			return;
		}
		position += 1;

		switch (dataBuffer.data[position]) {
			case '*':
				skipBlockComment();
				break;
			case '/':
				skipLineComment();
				break;
			default:
		}
	}

	private void skipBlockComment() {
		int tempPos = position;
		boolean endOfCommentFound = false;
		while (!endOfCommentFound) {
			switch (dataBuffer.data[++tempPos]) {
				case '*':
					endOfCommentFound = dataBuffer.data[tempPos + 1] == '/';
					break;
				default:
			}
		}
		position = tempPos;
	}

	private void skipLineComment() {
		int tempPos = position;
		boolean endOfCommentFound = false;
		while (!endOfCommentFound) {
			switch (dataBuffer.data[++tempPos]) {
				case NEW_LINE:
					endOfCommentFound = true;
					break;
				default:
			}

			if (dataBuffer.length <= tempPos + 1) {
				endOfCommentFound = true;
			}
		}
		position = tempPos;
	}

	private int consume(final IntPredicate predicate, final IntConsumer consumer) {
		int tempPos = position;

		boolean endOfBlock = false;
		while (!endOfBlock) {
			if (++tempPos >= dataBuffer.length) {
				consumer.accept(tempPos);
				endOfBlock = true;
				continue;
			}
			final char c = dataBuffer.data[tempPos];
			if (predicate.test(c)) {
				continue;
			}
			switch (c) {
				case ' ':
				case NEW_LINE:
					consumer.accept(tempPos);
					endOfBlock = true;
					continue;
					//
				case '{':
					consumer.accept(tempPos);
					consumeMethodStart(tempPos);
					endOfBlock = true;
					continue;
				case '}':
					consumer.accept(tempPos);
					consumeMethodEnd(tempPos);
					endOfBlock = true;
					continue;
					//
				case '(':
					consumer.accept(tempPos);
					consumeFunctionStart(tempPos);
					endOfBlock = true;
					continue;
				case ')':
					consumer.accept(tempPos);
					consumeFunctionEnd(tempPos);
					endOfBlock = true;
					continue;
					//
				case '[':
					consumer.accept(tempPos);
					consumeArrayStart(tempPos);
					endOfBlock = true;
					continue;
				case ']':
					consumer.accept(tempPos);
					consumeArrayEnd(tempPos);
					endOfBlock = true;
					continue;
					//
				case ':':
					consumer.accept(tempPos);
					consumeColon(tempPos);
					endOfBlock = true;
					continue;
				case ';':
					consumer.accept(tempPos);
					consumeSemiColon(tempPos);
					endOfBlock = true;
					continue;
				case ',':
					consumer.accept(tempPos);
					consumeComma(tempPos);
					endOfBlock = true;
					continue;
				case '.':
					consumer.accept(tempPos);
					consumePeriod(tempPos);
					endOfBlock = true;
					continue;
					//
				case '=':
					consumer.accept(tempPos);
					consumeEqual(tempPos);
					endOfBlock = true;
					continue;
				case '+':
					consumer.accept(tempPos);
					consumeAdd(tempPos);
					endOfBlock = true;
					continue;
				case '-':
					consumer.accept(tempPos);
					consumeSub(tempPos);
					endOfBlock = true;
					continue;
				case '*':
					consumer.accept(tempPos);
					consumeMul(tempPos);
					endOfBlock = true;
					continue;
				case '/':
					consumer.accept(tempPos);
					consumeDiv(tempPos);
					endOfBlock = true;
					continue;
				case '<':
					consumer.accept(tempPos);
					consumeLesser(tempPos);
					continue;
				case '>':
					consumer.accept(tempPos);
					consumeGreater(tempPos);
					continue;
				case '\'':
				case '"':
					throw exception("Unexpected quotation character");
				default:
			}
		}

		return tempPos;
	}

	private void consumeNamespace(final int position) {
		setElementData(elementIndex++, NAMESPACE, this.position, position - this.position);
	}

	private void consumeNumber(final int position) {
		setElementData(elementIndex++, NUMBER, this.position, position - this.position);
	}

	private void consumeMethodStart(final int position) {
		setElementData(elementIndex++, BODY_START, position);
	}

	private void consumeMethodEnd(final int position) {
		setElementData(elementIndex++, BODY_END, position);
	}

	private void consumeFunctionStart(final int position) {
		setElementData(elementIndex++, FUNCTION_START, position);
	}

	private void consumeFunctionEnd(final int position) {
		setElementData(elementIndex++, FUNCTION_END, position);
	}

	private void consumeArrayStart(final int position) {
		setElementData(elementIndex++, ARRAY_START, position);
	}

	private void consumeArrayEnd(final int position) {
		setElementData(elementIndex++, ARRAY_END, position);
	}

	private void consumeColon(final int position) {
		setElementData(elementIndex++, COLON, position);
	}

	private void consumeSemiColon(final int position) {
		setElementData(elementIndex++, SEMI_COLON, position);
	}

	private void consumeComma(final int position) {
		setElementData(elementIndex++, COMMA, position);
	}

	private void consumePeriod(final int position) {
		setElementData(elementIndex++, PERIOD, position);
	}

	private void consumeEqual(final int position) {
		setElementData(elementIndex++, EQUAL, position);
	}

	private void consumeAdd(final int position) {
		setElementData(elementIndex++, ADD, position);
	}

	private void consumeSub(final int position) {
		setElementData(elementIndex++, SUB, position);
	}

	private void consumeMul(final int position) {
		setElementData(elementIndex++, MUL, position);
	}

	private void consumeDiv(final int position) {
		setElementData(elementIndex++, DIV, position);
	}

	private void consumeLesser(final int position) {
		setElementData(elementIndex++, LESSER, position);
	}

	private void consumeGreater(final int position) {
		setElementData(elementIndex++, GREATER, position);
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

	@SuppressWarnings("unused")
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
