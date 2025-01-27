// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be
// found in the LICENSE file.
package org.jetbrains.java.decompiler.modules.decompiler.stats;

import org.jetbrains.java.decompiler.util.TextBuffer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;


public class GeneralStatement
				extends Statement {

	// *****************************************************************************
	// constructors
	// *****************************************************************************

	private GeneralStatement() {
		type = Statement.TYPE_GENERAL;
	}

	public GeneralStatement(Statement head,
	                        Collection<? extends Statement> statements,
	                        Statement post) {

		this();

		first = head;
		stats.addWithKey(head,
		                 head.id);

		Set<Statement> set = new LinkedHashSet<>(statements);
		set.remove(head);

		for (Statement st : set) {
			stats.addWithKey(st,
			                 st.id);
		}

		this.post = post;
	}

	// *****************************************************************************
	// public methods
	// *****************************************************************************

	@Override
	public TextBuffer toJava(int indent) {
		TextBuffer buf = new TextBuffer();

		if (isLabeled()) {
			buf.appendIndent(indent)
			   .append("label")
			   .append(this.id.toString())
			   .append(":")
			   .appendLineSeparator();
		}

		buf.appendIndent(indent)
		   .append("abstract statement {")
		   .appendLineSeparator();
		for (Statement stat : stats) {
			buf.append(stat.toJava(indent + 1));
		}
		buf.appendIndent(indent)
		   .append("}");
		buf.appendLineSeparator();

		return buf;
	}
}
