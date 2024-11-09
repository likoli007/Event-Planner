package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.service.crud.CrudService;

import javax.swing.table.AbstractTableModel;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TemplateTableModel extends AbstractTableModel {
    private final CrudService<Template> templateCrudService;
    private List<Template> templates;

    private final List<Column<Template, ?>> columns = List.of(
            Column.readonly("Name", String.class, Template::getName),
            Column.readonly("Details", String.class, Template::getDetails),
            Column.readonly("Start time", LocalTime.class, Template::getStartTime),
            Column.readonly("Interval", String.class, Template::formatInterval),
            Column.readonly("Categories", List.class, Template::getCategories)
    );

    public TemplateTableModel(CrudService<Template> templateCrudService) {
        this.templateCrudService = templateCrudService;
        this.templates = new ArrayList<>(templateCrudService.findAll());
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

    public void addRow(Template template) {
        templateCrudService.create(template);
        templates.add(template);
        int rowIndex = templates.size() - 1;
        fireTableRowsInserted(rowIndex, rowIndex);
    }

    public void updateRow(Template template) {
        templateCrudService.update(template);
        int rowIndex = templates.indexOf(template);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void deleteRow(int modelRow) {
        var template = getEntity(modelRow);
        templateCrudService.deleteById(template.getId());
        templates.remove(template);
        fireTableRowsDeleted(modelRow, modelRow);
    }
    public void refresh() {
        this.templates = new ArrayList<>(templateCrudService.findAll());
        fireTableDataChanged();
    }
    public CrudService<Template> getTemplateCrudService() {
        return templateCrudService;
    }
}
