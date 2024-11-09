package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.validation.Validator;

import javax.swing.*;

public final class IntervalDialog extends EntityDialog<TimeUnit> {

    private final JTextField nameField = new JTextField();
    private final JTextField shortcutField = new JTextField();
    private final JTextField minutesField = new JTextField();

    private final TimeUnit timeUnit;

    public IntervalDialog(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
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
        timeUnit.setName(nameField.getText());
        timeUnit.setShortcut(shortcutField.getText());
        timeUnit.setMinutes(Validator.parseInt("Time unit length", minutesField.getText()));
        return timeUnit;
    }
}
