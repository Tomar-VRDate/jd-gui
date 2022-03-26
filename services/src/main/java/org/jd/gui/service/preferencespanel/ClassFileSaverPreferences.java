package org.jd.gui.service.preferencespanel;

import org.jd.gui.spi.GenericPreferencesPanel;
import org.jd.gui.util.swing.Preference;

import java.util.LinkedHashMap;

enum ClassFileSaverPreferences
				implements Preference {
	writeLineNumbers("Write original line numbers",
	                 Preference.FALSE,
	                 Preference.FALSE,
	                 Preference.TRUE),
	writeMetadata("Write metadata",
	              Preference.FALSE,
	              Preference.FALSE,
	              Preference.TRUE);
	private static final LinkedHashMap<String, ClassFileSaverPreferences> nameClassClassFileSaverPreferencesMap
					= GenericPreferencesPanel.toLinkedHashMapByName(ClassFileSaverPreferences.values());

	private static final LinkedHashMap<String, ClassFileSaverPreferences> descriptionCClassFileSaverPreferencesMap
					= GenericPreferencesPanel.toLinkedHashMapByDescription(ClassFileSaverPreferences.values());

	private final String   description;
	private final String[] possibleValues;
	private final String   deselectedValue;
	private final String   defaultValue;
	private       String   selectedValue;

	ClassFileSaverPreferences(String description,
	                          String defaultValue,
	                          String... possibleValues) {
		this.description = description;
		this.possibleValues = possibleValues;
		this.defaultValue = defaultValue;
		this.deselectedValue = possibleValues.length > 0
		                       ? possibleValues[0]
		                       : defaultValue;
		this.selectedValue = possibleValues.length > 1
		                     ? possibleValues[1]
		                     : defaultValue;
	}

	public static ClassFileSaverPreferences getByName(String name) {
		return nameClassClassFileSaverPreferencesMap.get(name);
	}

	public static ClassFileSaverPreferences getByDescription(String description) {
		return descriptionCClassFileSaverPreferencesMap.get(description);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String[] getPossibleValues() {
		return possibleValues;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getDeselectedValue() {
		return deselectedValue;
	}

	@Override
	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}
}
