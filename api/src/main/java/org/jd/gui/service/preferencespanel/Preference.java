package org.jd.gui.service.preferencespanel;

import java.util.Map;

public interface Preference {
	String FALSE = "0";
	String TRUE  = "1";
	String TRACE = "TRACE";
	String INFO  = "INFO";
	String WARN  = "WARN";
	String ERROR = "ERROR";

	static String get(Map<String, String> preferences,
	                  Preference preference) {
		String key   = preference.getKey();
		String value = preferences.get(key);
		String preferenceValue = value == null
		                         ? preference.getDefaultValue()
		                         : value;
		return preferenceValue;
	}

	default String getKey() {
		String key = getClass().getSimpleName() + "." + name();
		return key;
	}

	String name();

	String getDescription();

	String getDefaultValue();

	String getDeselectedValue();

	String getSelectedValue();

	void setSelectedValue(String selectedValue);

	String[] getPossibleValues();
}
