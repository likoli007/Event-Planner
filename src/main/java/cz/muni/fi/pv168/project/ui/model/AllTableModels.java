package cz.muni.fi.pv168.project.ui.model;

public class AllTableModels {
    private EventTableModel eventTableModel;
    private CategoryTableModel categoryTableModel;
    private TemplateTableModel templateTableModel;

    public AllTableModels(EventTableModel eventTableModel, CategoryTableModel categoryTableModel, TemplateTableModel templateTableModel, TimeUnitTableModel timeUnitTableModel) {
        this.eventTableModel = eventTableModel;
        this.categoryTableModel = categoryTableModel;
        this.templateTableModel = templateTableModel;
        this.timeUnitTableModel = timeUnitTableModel;
    }

    private TimeUnitTableModel timeUnitTableModel;

    public EventTableModel getEventTableModel() {
        return eventTableModel;
    }

    public CategoryTableModel getCategoryTableModel() {
        return categoryTableModel;
    }

    public TemplateTableModel getTemplateTableModel() {
        return templateTableModel;
    }

    public TimeUnitTableModel getTimeUnitTableModel() {
        return timeUnitTableModel;
    }
}
