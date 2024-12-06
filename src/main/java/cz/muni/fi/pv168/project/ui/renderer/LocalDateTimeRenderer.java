package cz.muni.fi.pv168.project.ui.renderer;

import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeRenderer extends JLabel implements TableCellRenderer {

    private final DateTimeFormatter formatter;

    public LocalDateTimeRenderer() {
        this.formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy EEE");
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) value;
            setText(dateTime.format(formatter));
        } else {
            setText("");
        }

        EventTableModel model = (EventTableModel) table.getModel();
        TodoEvent event = model.getEntity(table.convertRowIndexToModel(row));

        if (event.isDone()) {
            setForeground(Color.GRAY);
        } else {
            setForeground(table.getForeground());
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(Color.WHITE);
        } else {
            setBackground(table.getBackground());
        }

        return this;
    }
}
