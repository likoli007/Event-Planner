package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.business.service.validation.ValidatorUtils;

import javax.swing.*;

public final class IntervalDialog extends EntityDialog<TimeUnit> {

    private final JTextField nameField = new JTextField();
    private final JTextField shortcutField = new JTextField();
    private final JTextField minutesField = new JTextField();

    private final TimeUnit timeUnit;

    public IntervalDialog(TimeUnit timeUnit) {
        this.timeUnit = new TimeUnit(timeUnit);
        setValues();
        addFields();
    }

    private void setValues() {
        nameField.setText(timeUnit.getName());
        shortcutField.setText(timeUnit.getShortcut());
        minutesField.setText(String.valueOf(timeUnit.getMinutes()));
    }

    private void addFields() {
        add("Name:", nameField);
        add("Shortcut:", shortcutField);
        add("Minutes:", minutesField);
    }

    @Override
    TimeUnit getEntity() {
        String name = nameField.getText();
        ValidatorUtils.validateNonemptyStringOld("Time unit name", name);
        timeUnit.setName(name);

        String shortcut = shortcutField.getText();
        ValidatorUtils.validateNonemptyStringOld("Time unit shortcut", shortcut);
        timeUnit.setShortcut(shortcut);

        timeUnit.setMinutes(ValidatorUtils.parseIntOld("Time unit length", minutesField.getText()));
        return timeUnit;
    }
}
