package org.jd.gui.util.swing;

public interface Preference {
	String FALSE = "0";
	String TRUE  = "1";
	String TRACE = "TRACE";
	String INFO  = "INFO";
	String WARN  = "WARN";
	String ERROR = "ERROR";

	String name();

	String getDescription();

	String getDefaultValue();

	String getDeselectedValue();

	String getSelectedValue();

	void setSelectedValue(String selectedValue);

	String[] getPossibleValues();
}
