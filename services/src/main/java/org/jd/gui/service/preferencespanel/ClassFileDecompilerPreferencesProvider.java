/*
 * Copyright (c) 2008-2022 Emmanuel Dupuy & Tomer Bar-Shlomo.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui.service.preferencespanel;

public class ClassFileDecompilerPreferencesProvider
				extends GenericPreferencesPanelProvider<ClassFileDecompilerPreferences> {
	public ClassFileDecompilerPreferencesProvider() {
		super(ClassFileDecompilerPreferences.values());
	}

	@Override
	public String getPreferencesGroupTitle() {return "Decompiler";}

	@Override
	public String getPreferencesPanelTitle() {return "Class file";}
}

