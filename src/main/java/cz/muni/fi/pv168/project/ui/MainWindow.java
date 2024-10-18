package cz.muni.fi.pv168.project.ui;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.ManagedEntity;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.action.AddAction;
import cz.muni.fi.pv168.project.ui.action.DeleteAction;
import cz.muni.fi.pv168.project.ui.action.EditAction;
import cz.muni.fi.pv168.project.ui.action.QuitAction;
import cz.muni.fi.pv168.project.ui.model.CategoryTableModel;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;
import cz.muni.fi.pv168.project.ui.model.TimeUnitTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MainWindow {
    private final JFrame frame;

    private final Action quitAction = new QuitAction();
    private final Action addAction;
    private final Action deleteAction;
    private final Action editAction;

    public MainWindow() {
        frame = createFrame();

        var tabPanel = new JTabbedPane();
        JPanel eventsTab = createEventsTab();
        JPanel managerTab = createManagerTab();
        tabPanel.addTab("Events", eventsTab);
        tabPanel.addTab("Manager", managerTab);

        frame.add(tabPanel, BorderLayout.CENTER);

        addAction = new AddAction();
        deleteAction = new DeleteAction();
        editAction = new EditAction();

        frame.add(createToolbar(), BorderLayout.BEFORE_FIRST_LINE);
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        changeActionsState(0);
    }

    public JPanel createEventsTab(){
        var testDataGenerator = new TestDataGenerator();
        List<TodoEvent> todoEvents = testDataGenerator.createTodoEvents(10);
        EventTableModel eventTableModel = new EventTableModel(todoEvents);
        JTable eventTable = createTable(eventTableModel);

        JPanel eventsTab = new JPanel();
        eventsTab.add(new JScrollPane(eventTable), BorderLayout.CENTER);

        return eventsTab;
    }

    // Function to update the table model when combo box selection changes
    private static void updateTableModel(ActionEvent e, JTable table) {
        JComboBox<ManagedEntity> comboBox = (JComboBox<ManagedEntity>) e.getSource();
        ManagedEntity selectedEntity = (ManagedEntity)comboBox.getSelectedItem();

        TestDataGenerator testDataGenerator = new TestDataGenerator();
        TableModel newModel;

        switch (selectedEntity) {
            case CATEGORIES:
                List<Category> categories = testDataGenerator.createCategories();
                newModel = new CategoryTableModel(categories); // Switch to CategoryTableModel
                break;
            case TEMPLATES:
                // TODO: implement templates to be displayed here, for now i will just re-use categories
                //List<Template> templates = testDataGenerator.createTemplates();
                //newModel = new TemplateTableModel(templates); // Switch to TemplateTableModel
                List<Category> templates = testDataGenerator.createCategories();
                newModel = new CategoryTableModel(templates); // Switch to CategoryTableModel
                break;
            case INTERVALS:
                List<TimeUnit> intervals = testDataGenerator.createTimeUnits();
                newModel = new TimeUnitTableModel(intervals); // Switch to IntervalTableModel
                break;
            default:
                // default since otherwise the setModel function may have an unitialized newModel
                // TODO: once normal app logic is being implemented an error/exception should be thrown here
                List<TimeUnit> timeUnits = testDataGenerator.createTimeUnits();
                newModel = new TimeUnitTableModel(timeUnits); // Default to TimeUnitTableModel
                break;
        }

        // Update the table with the new model
        table.setModel(newModel);
    }

    public JPanel createManagerTab(){
        var testDataGenerator = new TestDataGenerator();
        List<Category> categories = testDataGenerator.createCategories();

        CategoryTableModel CategoryTableModel = new CategoryTableModel(categories);
        JTable managerTabTable = createTable(CategoryTableModel);
        // TODO:
        // timeUnitTable.setComponentPopupMenu( );

        JComboBox<ManagedEntity> managedEntityComboBox = new JComboBox<>(ManagedEntity.values());

        JPanel managerTab = new JPanel(new BorderLayout());
        JPanel managerTabToolPanel = new JPanel(new BorderLayout());
        managerTabToolPanel.add(managedEntityComboBox, BorderLayout.EAST);
        managerTab.add(managerTabToolPanel, BorderLayout.NORTH);
        managerTab.add(new JScrollPane(managerTabTable), BorderLayout.CENTER);

        //
        managedEntityComboBox.addActionListener(e -> updateTableModel(e, managerTabTable));

        return managerTab;
    }
    public void show() {
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        var frame = new JFrame("Employee records");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    private <T extends AbstractTableModel> JTable createTable(T model) {
        var table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    private JPopupMenu createEmployeeTablePopupMenu() {
        var menu = new JPopupMenu();
        menu.add(deleteAction);
        menu.add(editAction);
        menu.add(addAction);
        return menu;
    }

    private JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();
        var editMenu = new JMenu("Edit");
        editMenu.setMnemonic('e');
        editMenu.add(addAction);
        editMenu.add(editAction);
        editMenu.add(deleteAction);
        editMenu.addSeparator();
        editMenu.add(quitAction);
        menuBar.add(editMenu);
        return menuBar;
    }

    private JToolBar createToolbar() {
        var toolbar = new JToolBar();
        toolbar.add(quitAction);
        toolbar.addSeparator();
        toolbar.add(addAction);
        toolbar.add(editAction);
        toolbar.add(deleteAction);
        return toolbar;
    }

    private void changeActionsState(int selectedItemsCount) {
        editAction.setEnabled(selectedItemsCount == 1);
        deleteAction.setEnabled(selectedItemsCount >= 1);
    }
}
