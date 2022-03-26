/*
 * Copyright (c) 2008-2022 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui.service.preferencespanel;

import org.jd.gui.spi.GenericPreferencesPanel;
import org.jd.gui.spi.PreferencesPanel;
import org.jd.gui.util.swing.Preference;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassFileSaverPreferencesProvider
				extends JPanel
				implements PreferencesPanel,
				           GenericPreferencesPanel {
	private static LinkedHashMap<Preference, List<AbstractButton>> preferenceAbstractButtonsMap = null;


	public ClassFileSaverPreferencesProvider() {
		super(new GridLayout(0,
		                     1));

		preferenceAbstractButtonsMap
						= GenericPreferencesPanel.toAbstractButtonsByPreferenceDescriptionMap(ClassFileSaverPreferences.values(),
						                                                                      this);
	}

	// --- PreferencesPanel --- //
	@Override
	public String getPreferencesGroupTitle() {return "Source Saver";}

	@Override
	public String getPreferencesPanelTitle() {return "Class file";}

	@Override
	public JComponent getPanel() {return this;}

	@Override
	public void init(Color errorBackgroundColor) {}

	@Override
	public boolean isActivated() {return true;}

	@Override
	public void loadPreferences(Map<String, String> preferences) {
		GenericPreferencesPanel.load(preferenceAbstractButtonsMap,
		                             preferences);
	}

	@Override
	public void savePreferences(Map<String, String> preferences) {
		GenericPreferencesPanel.save(preferences,
		                             preferenceAbstractButtonsMap);
	}

	@Override
	public boolean arePreferencesValid() {return true;}

	@Override
	public void addPreferencesChangeListener(PreferencesPanel.PreferencesPanelChangeListener listener) {}

	@Override
	public Component add(Component component) {
		return super.add(component);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		GenericPreferencesPanel.super.actionPerformed(actionEvent);
	}
}

