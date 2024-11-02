package cz.muni.fi.pv168.project.ui.renderer;

import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class EventTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        EventTableModel model = (EventTableModel) table.getModel();
        TodoEvent event = model.getEntity(table.convertRowIndexToModel(row));

        if (event.isDone()) {
            component.setForeground(Color.GRAY);
        } else {
            component.setForeground(Color.BLACK);
        }

        return component;
    }
}
