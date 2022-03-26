/*
 * Copyright (c) 2008-2022 Emmanuel Dupuy & Tomer Bar-Shlomo.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui.spi;

import org.jd.gui.util.map.MapToolbox;
import org.jd.gui.util.swing.Preference;
import org.jd.gui.util.swing.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static org.jd.gui.spi.GenericPreferencesPanel.getSelectedPreferenceValue;

@SuppressWarnings("UnnecessaryLocalVariable")
public interface GenericPreferencesPanel
				extends ActionListener {
	boolean NAME_AND_DEFAULT = false;

	static <Source extends Preference> LinkedHashMap<String, Source> toLinkedHashMapByName(Source[] sources) {
		LinkedHashMap<String, Source> linkedHashMapByName = MapToolbox.toLinkedHashMap(Preference::name,
		                                                                               Preference::name,
		                                                                               source -> source,
		                                                                               sources);
		return linkedHashMapByName;
	}

	static <Source extends Preference> LinkedHashMap<String, Source> toLinkedHashMapByDescription(Source[] sources) {
		LinkedHashMap<String, Source> linkedHashMapByDescription = MapToolbox.toLinkedHashMap(Preference::getDescription,
		                                                                                      Preference::getDescription,
		                                                                                      source -> source,
		                                                                                      sources);
		return linkedHashMapByDescription;
	}

	static <Source extends Preference> LinkedHashMap<String, Source> toLinkedHashMapByName(Collection<Source> sources) {
		LinkedHashMap<String, Source> linkedHashMapByName = MapToolbox.toLinkedHashMap(Preference::name,
		                                                                               Preference::name,
		                                                                               source -> source,
		                                                                               sources);
		return linkedHashMapByName;
	}

	static <Source extends Preference> LinkedHashMap<String, Source> toLinkedHashMapByDescription(Collection<Source> sources) {
		LinkedHashMap<String, Source> linkedHashMapByDescription = MapToolbox.toLinkedHashMap(Preference::getDescription,
		                                                                                      Preference::getDescription,
		                                                                                      source -> source,
		                                                                                      sources);
		return linkedHashMapByDescription;
	}

	static <Source extends Preference> LinkedHashMap<Source, java.util.List<AbstractButton>> toAbstractButtonsByPreferenceDescriptionMap(Source[] sources,
	                                                                                                                                     GenericPreferencesPanel preferencesPanel) {
		LinkedHashMap<Source, List<AbstractButton>> abstractButtonsByPreferenceDescriptionMap = Arrays.stream(sources)
		                                                                                              .sorted(Comparator.comparing(Preference::getDescription))
		                                                                                              .collect(Collectors.toMap(source -> source,
		                                                                                                                        preference -> toAbstractButtons(preference,
		                                                                                                                                                        preferencesPanel),
		                                                                                                                        (x, y) -> y,
		                                                                                                                        LinkedHashMap::new));
		return abstractButtonsByPreferenceDescriptionMap;

	}

	static <Source extends Preference> LinkedHashMap<Source, List<AbstractButton>> toAbstractButtonsByPreferenceDescriptionMap(Collection<Source> sources,
	                                                                                                                           GenericPreferencesPanel preferencesPanel) {
		LinkedHashMap<Source, List<AbstractButton>> abstractButtonsByPreferenceDescriptionMap = sources.stream()
		                                                                                               .sorted(Comparator.comparing(Preference::getDescription))
		                                                                                               .collect(Collectors.toMap(source -> source,
		                                                                                                                         preference -> toAbstractButtons(preference,
		                                                                                                                                                         preferencesPanel),
		                                                                                                                         (x, y) -> y,
		                                                                                                                         LinkedHashMap::new));
		return abstractButtonsByPreferenceDescriptionMap;
	}

	static List<AbstractButton> toAbstractButtons(Preference preference,
	                                              GenericPreferencesPanel genericPreferencesPanel) {
		String[]             possibleValues  = preference.getPossibleValues();
		List<AbstractButton> abstractButtons = new ArrayList<>();
		AbstractButton       abstractButton;
		if (possibleValues.length == 2) {
			abstractButton = toJCheckBox(preference,
			                             genericPreferencesPanel);
			genericPreferencesPanel.add(abstractButton);
			abstractButtons.add(abstractButton);
		} else {
			ButtonGroup buttonGroup = new ButtonGroup();
			for (String possibleValue : possibleValues) {
				abstractButton = toJRadioButton(preference,
				                                possibleValue,
				                                genericPreferencesPanel);
				genericPreferencesPanel.add(abstractButton);
				buttonGroup.add(abstractButton);
				abstractButtons.add(abstractButton);
			}
		}
		return abstractButtons;
	}

	static JCheckBox toJCheckBox(Preference preference,
	                             GenericPreferencesPanel preferencesPanel) {
		String preferenceValue = preference.getSelectedValue();
		Action action = toAction(preference,
		                         preferenceValue,
		                         preferencesPanel);
		JCheckBox jCheckBox = new JCheckBox(action);
		setSelected(jCheckBox,
		            preference,
		            preferenceValue);
		return jCheckBox;
	}

	static JRadioButton toJRadioButton(Preference preference,
	                                   String preferenceValue,
	                                   GenericPreferencesPanel preferencesPanel) {
		Action action = toAction(preference,
		                         preferenceValue,
		                         preferencesPanel);
		JRadioButton jRadioButton = new JRadioButton(action);
		setSelected(jRadioButton,
		            preference,
		            preferenceValue);
		return jRadioButton;
	}

	static Action toAction(Preference preference,
	                       String preferenceValue,
	                       GenericPreferencesPanel preferencesPanel) {
		String actionName = getActionName(preference,
		                                  preferenceValue);
		Action action = SwingUtil.newAction(actionName,
		                                    true,
		                                    preferencesPanel);
		action.putValue(SwingUtil.PREFERENCE,
		                preference);
		action.putValue(SwingUtil.PREFERENCE_VALUE,
		                preferenceValue);
		return action;
	}

	static String getActionName(Preference preference,
	                            String preferenceValue) {
		String   preferenceName = preference.name();
		String   defaultValue   = preference.getDefaultValue();
		String   description    = preference.getDescription();
		String[] possibleValues = preference.getPossibleValues();
		String name = NAME_AND_DEFAULT
		              ? getNameAndDefault(preferenceValue,
		                                  preferenceName,
		                                  defaultValue,
		                                  description,
		                                  possibleValues)
		              : getName(preferenceValue,
		                        description,
		                        possibleValues);
		return name;
	}

	static String getName(String preferenceValue,
	                      String description,
	                      String[] possibleValues) {
		return possibleValues.length > 2
		       ? String.format("%s %s",
		                       preferenceValue,
		                       description)
		       : String.format("%s",
		                       description);
	}

	static String getNameAndDefault(String preferenceValue,
	                                String preferenceName,
	                                String defaultValue,
	                                String description,
	                                String[] possibleValues) {
		return possibleValues.length > 2
		       ? String.format("'%s' (%s) %s %s",
		                       preferenceName,
		                       defaultValue,
		                       preferenceValue,
		                       description)
		       : String.format("'%s' (%s) %s",
		                       preferenceName,
		                       preferenceValue.equals(defaultValue)
		                       ? '+'
		                       : '-',
		                       description);
	}

	static void setSelected(AbstractButton abstractButton,
	                        Preference preference,
	                        String preferenceValue) {
		String  defaultValue = preference.getDefaultValue();
		boolean selected     = preferenceValue.equals(defaultValue);
		abstractButton.setSelected(selected);
	}

	static void save(Map<String, String> preferences,
	                 LinkedHashMap<Preference, List<AbstractButton>> preferenceAbstractButtonLinkedHashMap) {
		preferenceAbstractButtonLinkedHashMap.values()
		                                     .forEach(abstractButton -> save(preferences,
		                                                                     abstractButton));
	}

	static void save(Map<String, String> preferences,
	                 List<AbstractButton> abstractButtons) {
		boolean isGroup = abstractButtons.size() > 1;
		for (AbstractButton abstractButton : abstractButtons) {
			boolean isSelected = abstractButton.isSelected();
			if (!isGroup || isSelected) {
				save(preferences,
				     abstractButton);
			}
		}
	}

	static void save(Map<String, String> preferences,
	                 AbstractButton abstractButton) {
		Action     action     = abstractButton.getAction();
		Preference preference = (Preference) action.getValue(SwingUtil.PREFERENCE);
		String preferenceValue = action.getValue(SwingUtil.PREFERENCE_VALUE)
		                               .toString();
		String preferenceName = preference.getClass()
		                                  .getSimpleName() + "." + preference.name();
		String deselectedValue = preference.getDeselectedValue();
		String actionName = action.getValue(Action.NAME)
		                          .toString();
		boolean isSelected = abstractButton.isSelected();
		String selectedValue = isSelected
		                       ? preferenceValue
		                       : deselectedValue;
		System.out.printf("%s '%s'=%s %s %s%n",
		                  "Saving preference",
		                  preferenceName,
		                  selectedValue,
		                  isSelected
		                  ? '+'
		                  : '-',
		                  actionName);
		preferences.put(preferenceName,
		                selectedValue);
	}

	static void load(LinkedHashMap<Preference, List<AbstractButton>> preferenceAbstractButtonLinkedHashMap,
	                 Map<String, String> preferences) {
		preferenceAbstractButtonLinkedHashMap.values()
		                                     .forEach(abstractButton -> load(preferences,
		                                                                     abstractButton));
	}

	static void load(Map<String, String> preferences,
	                 List<AbstractButton> abstractButtons) {
		for (AbstractButton abstractButton : abstractButtons) {
			load(preferences,
			     abstractButton);
		}
	}

	static void load(Map<String, String> preferences,
	                 AbstractButton abstractButton) {
		Action     action     = abstractButton.getAction();
		Preference preference = (Preference) action.getValue(SwingUtil.PREFERENCE);
		String preferenceName = preference.getClass()
		                                  .getSimpleName() + "." + preference.name();
		String actionName = action.getValue(Action.NAME)
		                          .toString();
		String defaultValue = preference.getDefaultValue();
		String preferenceValue = action.getValue(SwingUtil.PREFERENCE_VALUE)
		                               .toString();
		boolean isSelectedByDefault          = defaultValue.equals(preferenceValue);
		String  preferencesValue             = preferences.get(preferenceName);
		boolean isSelectedByPreferencesValue = preferencesValue != null;
		String selectedValue = isSelectedByDefault && isSelectedByPreferencesValue
		                       ? preferenceValue
		                       : !isSelectedByPreferencesValue
		                         ? defaultValue
		                         : preferencesValue;
		preference.setSelectedValue(selectedValue);
		boolean isSelected = selectedValue.equals(preferenceValue);
		System.out.printf("%s '%s'=%s %s %s %s%n",
		                  "Loading preference",
		                  preferenceName,
		                  selectedValue,
		                  preferencesValue,
		                  isSelected
		                  ? '+'
		                  : '-',
		                  actionName);
		abstractButton.setSelected(isSelected);
	}

	static String getSelectedPreferenceValue(AbstractButton abstractButton) {
		Action     action         = abstractButton.getAction();
		Preference preference     = (Preference) action.getValue(SwingUtil.PREFERENCE);
		String     preferenceName = preference.name();
		String actionName = action.getValue(Action.NAME)
		                          .toString();
		String defaultValue = preference.getDefaultValue();
		String preferenceValue = action.getValue(SwingUtil.PREFERENCE_VALUE)
		                               .toString();
		boolean isSelected = abstractButton.isSelected();
		String selectedValue = isSelected
		                       ? preferenceValue
		                       : defaultValue;
		preference.setSelectedValue(selectedValue);
		System.out.printf("%s '%s'=%s %s %s%n",
		                  "Selected preference",
		                  preferenceName,
		                  selectedValue,
		                  isSelected
		                  ? '+'
		                  : '-',
		                  actionName);
		return selectedValue;
	}

	Component add(Component component);

	default void actionPerformed(ActionEvent actionEvent) {
		Object source = actionEvent.getSource();
		AbstractButton abstractButton = source instanceof AbstractButton
		                                ? (AbstractButton) source
		                                : null;
		if (abstractButton != null) {
			getSelectedPreferenceValue(abstractButton);
		}
	}
}
