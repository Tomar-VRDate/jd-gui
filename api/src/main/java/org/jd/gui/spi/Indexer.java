/*
 * Copyright (c) 2008-2022 Emmanuel Dupuy & Tomer Bar-Shlomo.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui.spi;

import org.jd.gui.api.API;
import org.jd.gui.api.model.Container;
import org.jd.gui.api.model.Indexes;

import java.util.regex.Pattern;

public interface Indexer {
	String[] getSelectors();

	Pattern getPathPattern();

	void index(API api,
	           Container.Entry entry,
	           Indexes indexes);
}
