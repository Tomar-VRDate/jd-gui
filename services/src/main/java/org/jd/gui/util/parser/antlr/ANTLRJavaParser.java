/*
 * Copyright (c) 2008-2022 Emmanuel Dupuy & Tomer Bar-Shlomo.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui.util.parser.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jd.gui.util.exception.ExceptionUtil;

public class ANTLRJavaParser {
	public static void parse(CharStream input,
	                         JavaListener listener) {
		try {
			JavaLexer lexer = new JavaLexer(input);

			lexer.removeErrorListeners();

			CommonTokenStream tokens = new CommonTokenStream(lexer);
			JavaParser        parser = new JavaParser(tokens);

			parser.removeErrorListeners();

			ParseTree tree = parser.compilationUnit();

			ParseTreeWalker.DEFAULT.walk(listener,
			                             tree);
		} catch (StackOverflowError e) {
			// Too complex source file, probably not written by a human.
			// This error may happen on Java file generated by ANTLR for example.
			assert ExceptionUtil.printStackTrace(e);
		}
	}
}
