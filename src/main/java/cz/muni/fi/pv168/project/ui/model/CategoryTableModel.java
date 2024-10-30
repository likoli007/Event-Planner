package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Color;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.service.crud.CrudService;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CategoryTableModel extends AbstractTableModel {
    private final CrudService<Category> categoryCrudService;
    private List<Category> categories;

    private final List<Column<Category, ?>> columns = List.of(
            Column.editable("Name", String.class, Category::getName, Category::setName),
            Column.editable("Color", Color.class, Category::getColor, Category::setColor)
    );

    public CategoryTableModel(CrudService<Category> categoryCrudService) {
        this.categoryCrudService = categoryCrudService;
        this.categories = new ArrayList<>(categoryCrudService.findAll());
    }

    @Override
    public int getRowCount() {
        return categories.size();
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

    public Category getEntity(int rowIndex) {
        return categories.get(rowIndex);
    }
}
