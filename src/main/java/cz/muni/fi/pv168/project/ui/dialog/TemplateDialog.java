package cz.muni.fi.pv168.project.ui.dialog;

import com.github.lgooddatepicker.components.TimePicker;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.ui.documentFilters.NumericDocumentFilter;
import cz.muni.fi.pv168.project.ui.model.AllTableModels;
import cz.muni.fi.pv168.project.ui.utils.NumericInputValue;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Position;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public final class TemplateDialog extends EntityDialog<Template> {

    private final JTextField nameField = new JTextField();
    private final JTextField detailsField = new JTextField();
    private final TimePicker timeField = new TimePicker();

    private final JTextField intervalField = new JTextField(5);
    private final JList<Category> categoryList;
    private final ComboBoxModel<TimeUnit> timeUnitModel;
    private final DefaultListModel<Category> categoryModel;
    private final Template template;

    public TemplateDialog(Template template, AllTableModels allTableModels) {
        this.template = new Template(template);
        this.categoryModel = new DefaultListModel<>();
        for (Category category : allTableModels.getCategoryTableModel().getCategoryCrudService().findAll()) {
            this.categoryModel.addElement(category);
        }
        this.categoryList = new JList<>(categoryModel);

        this.timeUnitModel = new DefaultComboBoxModel<>(allTableModels.getTimeUnitTableModel().getTimeUnitCrudService().findAll().toArray(new TimeUnit[0]));

        ((AbstractDocument) intervalField.getDocument()).setDocumentFilter(new NumericDocumentFilter());

        setValues();
        addFields();
    }

    private void setValues() {
        nameField.setText(template.getName());
        detailsField.setText(template.getDetails());

        LocalTime startTime = template.getStartTime();
        timeField.setTime(Objects.requireNonNullElseGet(startTime, LocalTime::now));

        intervalField.setText(String.valueOf(template.getInterval().getAmount()));
        for (Category category : template.getCategories()) {
            int index = categoryList.getNextMatch(category.toString(), 0, Position.Bias.Forward);
            if (index != -1) {
                categoryList.addSelectionInterval(index, index);
            }
        }
        timeUnitModel.setSelectedItem(template.getInterval().getTimeUnit());
    }

    private void addFields() {
        var timeUnitComboBox = new JComboBox<>(timeUnitModel);
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        intervalPanel.add(intervalField);
        intervalPanel.add(timeUnitComboBox);

        add("Name:", nameField);
        add("Details:", detailsField);
        add("Time:", timeField);
        add("Length:", intervalPanel);
        add("Categories:", categoryList);
    }

    @Override
    Template getEntity() {
        String name = nameField.getText();
        template.setName(name);

        template.setDetails(detailsField.getText());

        LocalTime time = timeField.getTime();
        if (time != null) {
            template.setStartTime(time);
        }

        template.getInterval().setAmount(NumericInputValue.get(intervalField.getText(), "length"));
        template.getInterval().setTimeUnit((TimeUnit) timeUnitModel.getSelectedItem());

        List<Category> selectedCategories = categoryList.getSelectedValuesList();
        template.setCategories(selectedCategories);

        return template;
    }
}
