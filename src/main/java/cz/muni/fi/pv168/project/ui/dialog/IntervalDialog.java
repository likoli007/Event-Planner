package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.ui.documentFilters.NumericDocumentFilter;
import cz.muni.fi.pv168.project.ui.utils.NumericInputValue;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

public final class IntervalDialog extends EntityDialog<TimeUnit> {

    private final JTextField nameField = new JTextField();
    private final JTextField shortcutField = new JTextField();
    private final JTextField minutesField = new JTextField();

    private final TimeUnit timeUnit;

    public IntervalDialog(TimeUnit timeUnit) {
        this.timeUnit = new TimeUnit(timeUnit);
        setValues();
        addFields();

        ((AbstractDocument) minutesField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
    }

    private void setValues() {
        nameField.setText(timeUnit.getName());
        shortcutField.setText(timeUnit.getShortcut());

        int minutesValue = timeUnit.getMinutes();
        if (minutesValue != 0) {
            minutesField.setText(String.valueOf(minutesValue));
        }
    }

    private void addFields() {
        add("Name:", nameField);
        add("Shortcut:", shortcutField);
        add("Minutes:", minutesField);
    }

    @Override
    TimeUnit getEntity() {
        String name = nameField.getText();
        timeUnit.setName(name);
        String shortcut = shortcutField.getText();
        timeUnit.setShortcut(shortcut);
        timeUnit.setMinutes(NumericInputValue.get(minutesField.getText(), "minutes"));
        return timeUnit;
    }
}
