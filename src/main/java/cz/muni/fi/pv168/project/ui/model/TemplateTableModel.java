package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TemplateTableModel extends AbstractTableModel {
    private final List<Template> templates;

    private final List<Column<Template, ?>> columns = List.of(
            Column.readonly("Name", String.class, Template::getName),
            Column.readonly("Details", String.class, Template::getDetails),
            Column.readonly("Date", LocalDateTime.class, Template::getDate),
            Column.readonly("Interval", String.class, Template::formatInterval),
            Column.readonly("Categories", List.class, Template::getCategories)
    );

    public TemplateTableModel(List<Template> templates) {
        this.templates = new ArrayList<>(templates);
    }

    @Override
    public int getRowCount() {
        return templates.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var template = getEntity(rowIndex);
        return columns.get(columnIndex).getValue(template);
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

    public Template getEntity(int rowIndex) {
        return templates.get(rowIndex);
    }

    public void addTemplate(Template template) {
        templates.add(template);
        int rowIndex = templates.size() - 1;
        fireTableRowsInserted(rowIndex, rowIndex);
    }
}
