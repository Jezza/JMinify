package me.jezza.min;

import me.jezza.min.js.JavascriptNavigator;
import me.jezza.min.js.JavascriptReader;

import java.io.FileNotFoundException;
import java.util.HashSet;

/**
 * @author jezza
 * @date Nov 4, 2015
 */
public class Minify {
	public static final HashSet<String> reserved = new HashSet<>();

	static {
		reserved.add("abstract");
		reserved.add("arguments");
		reserved.add("boolean");
		reserved.add("break");
		reserved.add("byte");
		reserved.add("case");
		reserved.add("catch");
		reserved.add("char");
		reserved.add("class");
		reserved.add("const");
		reserved.add("continue");
		reserved.add("debugger");
		reserved.add("default");
		reserved.add("delete");
		reserved.add("do");
		reserved.add("double");
		reserved.add("else");
		reserved.add("enum");
		reserved.add("eval");
		reserved.add("export");
		reserved.add("extends");
		reserved.add("false");
		reserved.add("final");
		reserved.add("finally");
		reserved.add("float");
		reserved.add("for");
		reserved.add("function");
		reserved.add("goto");
		reserved.add("if");
		reserved.add("implements");
		reserved.add("import");
		reserved.add("in");
		reserved.add("instanceof");
		reserved.add("int");
		reserved.add("interface");
		reserved.add("let");
		reserved.add("long");
		reserved.add("native");
		reserved.add("new");
		reserved.add("null");
		reserved.add("package");
		reserved.add("private");
		reserved.add("protected");
		reserved.add("public");
		reserved.add("return");
		reserved.add("short");
		reserved.add("static");
		reserved.add("super");
		reserved.add("switch");
		reserved.add("synchronized");
		reserved.add("this");
		reserved.add("throw");
		reserved.add("throws");
		reserved.add("transient");
		reserved.add("true");
		reserved.add("try");
		reserved.add("typeof");
		reserved.add("var");
		reserved.add("void");
		reserved.add("volatile");
		reserved.add("while");
		reserved.add("with");
		reserved.add("yield");
	}


	private Minify() {
		throw new IllegalStateException();
	}

	public static void main(final String[] args) throws FileNotFoundException {
		JavascriptNavigator read = new JavascriptReader("1+1").read();
		System.out.println("Size: " + read.size());
		while (read.hasNext())
			System.out.println(read.next().typeString() + ":" + read.asString());
	}

	public static String js(CharSequence sequence) {
		return sequence.toString();
	}
}
