package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.TodoEvent;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class EventTableModel extends AbstractTableModel {
    private final List<TodoEvent> todoEvents;

    private final List<Column<TodoEvent, ?>> columns = List.of(
            Column.readonly("Name", String.class, TodoEvent::getName),
            Column.readonly("Details", String.class, TodoEvent::getDetails),
            Column.readonly("Start", LocalDateTime.class, TodoEvent::getStart),
            Column.readonly("Interval", String.class, TodoEvent::formatInterval),
            Column.readonly("Categories", List.class, TodoEvent::getCategories),
            Column.editable("Done", Boolean.class, TodoEvent::isDone, TodoEvent::setDone)
    );

    public EventTableModel(List<TodoEvent> todoEvents) {
        this.todoEvents = new ArrayList<>(todoEvents);
    }

    @Override
    public int getRowCount() {
        return todoEvents.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var event = getEntity(rowIndex);
        return columns.get(columnIndex).getValue(event);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns.get(columnIndex).getName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns.get(columnIndex).getColumnType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).isEditable();
    }

    public TodoEvent getEntity(int rowIndex) {
        return todoEvents.get(rowIndex);
    }
}
