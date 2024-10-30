package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.service.crud.CrudService;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TimeUnitTableModel extends AbstractTableModel {
    private final CrudService<TimeUnit> timeUnitCrudService;
    private List<TimeUnit> timeUnits;

    private final List<Column<TimeUnit, ?>> columns = List.of(
            Column.editable("Name", String.class, TimeUnit::getName, TimeUnit::setName),
            Column.editable("Shortcut", String.class, TimeUnit::getShortcut, TimeUnit::setShortcut),
            Column.editable("Minutes", Integer.class, TimeUnit::getMinutes, TimeUnit::setMinutes)
    );

    public TimeUnitTableModel(CrudService<TimeUnit> timeUnitCrudService) {
        this.timeUnitCrudService = timeUnitCrudService;
        this.timeUnits = new ArrayList<>(timeUnitCrudService.findAll());
    }

    @Override
    public int getRowCount() {
        return timeUnits.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var timeUnit = getEntity(rowIndex);
        return columns.get(columnIndex).getValue(timeUnit);
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

    public TimeUnit getEntity(int rowIndex) {
        return timeUnits.get(rowIndex);
    }
}
