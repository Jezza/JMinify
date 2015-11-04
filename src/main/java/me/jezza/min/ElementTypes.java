package me.jezza.min;

/**
 * @author jezza
 * @date Nov 4, 2015
 */
public class ElementTypes {
	// Standard stuffs
	public static final byte NAMESPACE = 1; // a block of characters with no defined meaning.
	public static final byte NUMBER = 2; // 1
	public static final byte STRING = 3; // " or '

	// Method stuff
	public static final byte BODY_START = 10; // {
	public static final byte BODY_END = 11; // }
	public static final byte FUNCTION_START = 12; // (
	public static final byte FUNCTION_END = 13; // )
	public static final byte ARRAY_START = 14; // [
	public static final byte ARRAY_END = 15; // ]

	public static final byte COLON = 20; // :
	public static final byte QUESTION = 21; // ?
	public static final byte SEMI_COLON = 22; // ;
	public static final byte COMMA = 23; // ,
	public static final byte PERIOD = 24; // .

	// Operators
	public static final byte EQUAL = 30; // =
	public static final byte ADD = 31; // +
	public static final byte SUB = 32; // -
	public static final byte MUL = 33; // *
	public static final byte DIV = 34; // /

	public static final byte REM = 40; // %
	public static final byte AMP = 41; // &
	public static final byte PIPE = 42; // |
	public static final byte CARET = 43; // ^
	public static final byte LESSER = 44; // <
	public static final byte GREATER = 45; // >
	public static final byte NOT = 46; // !
	public static final byte TILDE = 47; // ~

	private ElementTypes() {
		throw new IllegalStateException();
	}
}
