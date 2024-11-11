package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacade;
import cz.muni.fi.pv168.project.business.filter.Filter;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.business.filter.TodoEventFilter;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventTableModel extends AbstractTableModel {
    private final TodoEventsServiceFacade todoEventFacade;
    private List<TodoEvent> todoEvents;
    private Filter<TodoEvent> filter;

    private final List<Column<TodoEvent, ?>> columns = List.of(
            Column.readonly("Name", String.class, TodoEvent::getName),
            Column.readonly("Details", String.class, TodoEvent::getDetails),
            Column.readonly("Start", LocalDateTime.class, TodoEvent::getStart),
            Column.readonly("Interval", String.class, TodoEvent::formatInterval),
            Column.readonly("Categories", List.class, TodoEvent::getCategories),
            Column.editable("Done", Boolean.class, TodoEvent::isDone, TodoEvent::setDone)
    );

    public EventTableModel(TodoEventsServiceFacade todoEventFacade, Filter<TodoEvent> filter) {
        this.todoEventFacade = todoEventFacade;
        this.todoEvents = new ArrayList<>(todoEventFacade.findAll());
        this.filter = filter;
    }

    public void refetch(TodoEventFilter filter) {
        this.filter = filter;
        todoEvents = new ArrayList<>( todoEventFacade.getEventsByFilter(filter));
        fireTableDataChanged();
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
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        var event = getEntity(rowIndex);
        if (columns.get(columnIndex).getName().equals("Done")) {
            event.setDone((Boolean) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
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

    public int getColumnIndexByName(String columnName) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    public void addRow(TodoEvent todoEvent) {
        todoEventFacade.create(todoEvent);

        if(!filter.isMatch(todoEvent)){
            return;
        }
        // only add the event to table if it matches the filter
        todoEvents.add(todoEvent);
        int rowIndex = todoEvents.size() - 1;
        fireTableRowsInserted(rowIndex, rowIndex);
    }

    public void updateRow(TodoEvent todoEvent) {
        todoEventFacade.update(todoEvent);

        // delete the event from table if it doesn't match the filter anymore
        if(!filter.isMatch(todoEvent)){
            int rowIndex = todoEvents.indexOf(todoEvent);
            todoEvents.remove(todoEvent);
            fireTableRowsDeleted(rowIndex, rowIndex);
            return;
        }

        int rowIndex = todoEvents.indexOf(todoEvent);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void deleteRow(int modelRow) {
        var template = getEntity(modelRow);
        todoEventFacade.deleteById(template.getId());
        todoEvents.remove(template);
        fireTableRowsDeleted(modelRow, modelRow);
    }
}
