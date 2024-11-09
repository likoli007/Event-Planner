package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.model.TodoEventFilter;
import cz.muni.fi.pv168.project.service.crud.CrudService;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class EventTableModel extends AbstractTableModel {
    private final CrudService<TodoEvent> todoEventCrudService;
    private List<TodoEvent> todoEvents;

    private final List<Column<TodoEvent, ?>> columns = List.of(
            Column.readonly("Name", String.class, TodoEvent::getName),
            Column.readonly("Details", String.class, TodoEvent::getDetails),
            Column.readonly("Start", LocalDateTime.class, TodoEvent::getStart),
            Column.readonly("Interval", String.class, TodoEvent::formatInterval),
            Column.readonly("Categories", List.class, TodoEvent::getCategories),
            Column.editable("Done", Boolean.class, TodoEvent::isDone, TodoEvent::setDone)
    );

    public EventTableModel(CrudService<TodoEvent> todoEventCrudService) {
        this.todoEventCrudService = todoEventCrudService;
        this.todoEvents = new ArrayList<>(todoEventCrudService.findAll());
    }

    public void refetch(TodoEventFilter filter) {
        // this logic will be moved
        todoEvents = todoEventCrudService.findAll().stream().filter(

                        event -> filter.getDone() == null || event.isDone() == filter.getDone()
                ).filter(
                        event-> filter.getFromDate() == null ||  event.getStart().toLocalDate().isBefore(filter.getFromDate())
                ).filter(
                        event-> filter.getToDate() == null ||  event.getStart().toLocalDate().isAfter(filter.getToDate())
                )
                .toList();
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
        todoEventCrudService.create(todoEvent);
        todoEvents.add(todoEvent);
        int rowIndex = todoEvents.size() - 1;
        fireTableRowsInserted(rowIndex, rowIndex);
    }

    public void updateRow(TodoEvent todoEvent) {
        todoEventCrudService.update(todoEvent);
        int rowIndex = todoEvents.indexOf(todoEvent);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void deleteRow(int modelRow) {
        var template = getEntity(modelRow);
        todoEventCrudService.deleteById(template.getId());
        todoEvents.remove(template);
        fireTableRowsDeleted(modelRow, modelRow);
    }



    public CrudService<TodoEvent> getTodoEventCrudService() {
        return todoEventCrudService;
    }
}
