/*
 * Copyright (c) 2008-2022 Emmanuel Dupuy & Tomer Bar-Shlomo.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui.api.feature;

import org.jd.gui.api.API;
import org.jd.gui.api.model.Container;

import java.nio.file.Path;

public interface SourcesSavable {
	String getSourceFileName();

	Container.Entry getEntry();

	int getFileCount();

	void save(API api,
	          Controller controller,
	          Listener listener,
	          Path path);

	interface Controller {
		boolean isCancelled();
	}

	interface Listener {
		void pathSaved(Path path);
	}
}
