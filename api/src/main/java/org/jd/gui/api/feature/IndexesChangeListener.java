/*
 * Copyright (c) 2008-2022 Emmanuel Dupuy & Tomer Bar-Shlomo.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui.api.feature;

import org.jd.gui.api.model.Indexes;

import java.util.Collection;
import java.util.concurrent.Future;

public interface IndexesChangeListener {
	void indexesChanged(Collection<Future<Indexes>> collectionOfFutureIndexes);
}
