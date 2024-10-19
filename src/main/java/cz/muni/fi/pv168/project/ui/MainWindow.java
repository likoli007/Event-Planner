package cz.muni.fi.pv168.project.ui;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.ManagedEntity;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.action.AddAction;
import cz.muni.fi.pv168.project.ui.action.DeleteAction;
import cz.muni.fi.pv168.project.ui.action.EditAction;
import cz.muni.fi.pv168.project.ui.action.ExportAction;
import cz.muni.fi.pv168.project.ui.action.ImportAction;
import cz.muni.fi.pv168.project.ui.action.QuitAction;
import cz.muni.fi.pv168.project.ui.model.CategoryTableModel;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;
import cz.muni.fi.pv168.project.ui.model.TimeUnitTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MainWindow {
    private final JFrame frame;
    private final JTable eventTable;
    private final JTable managerTabTable;
    private JTable currentTable;

    private final Action quitAction = new QuitAction();
    private final Action addAction;
    private final Action deleteAction;
    private final Action editAction;
    private final Action importAction;
    private final Action exportAction;

    public MainWindow() {
        frame = createFrame();

        var testDataGenerator = new TestDataGenerator();
        eventTable = createTodoEventTable(testDataGenerator.createTodoEvents(10));
        managerTabTable = createCategoryTable(testDataGenerator.createCategories());

        currentTable = eventTable;

        addAction = new AddAction(currentTable);
        deleteAction = new DeleteAction();
        editAction = new EditAction(currentTable);
        importAction = new ImportAction();
        exportAction = new ExportAction();

        var tabPanel = new JTabbedPane();
        JPanel eventsTab = createEventsTab();
        JPanel managerTab = createManagerTab();
        tabPanel.addTab("Events", eventsTab);
        tabPanel.addTab("Manager", managerTab);

        tabPanel.addChangeListener(e -> {
            int selectedIndex = tabPanel.getSelectedIndex();
            if (selectedIndex == 0) {
                currentTable = eventTable;
            } else if (selectedIndex == 1) {
                currentTable = managerTabTable;
            }
            int selectedRowsCount = currentTable.getSelectedRowCount();
            changeActionsState(selectedRowsCount);
        });

        eventTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && currentTable == eventTable) {
                int selectedRowsCount = eventTable.getSelectedRowCount();
                changeActionsState(selectedRowsCount);
            }
        });

        managerTabTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && currentTable == managerTabTable) {
                int selectedRowsCount = managerTabTable.getSelectedRowCount();
                changeActionsState(selectedRowsCount);
            }
        });

        frame.add(tabPanel, BorderLayout.CENTER);
        frame.add(createToolbar(), BorderLayout.BEFORE_FIRST_LINE);
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        changeActionsState(0);
    }

    public JPanel createEventsTab(){
        JPanel eventsTab = new JPanel(new BorderLayout());
        eventsTab.add(new JScrollPane(eventTable), BorderLayout.CENTER);
        eventTable.setComponentPopupMenu(createPopupMenu());
        eventsTab.add(createStatisticsPanel(), BorderLayout.SOUTH);


        return eventsTab;
    }

    //TODO: actual computing of statistics
    public JPanel createStatisticsPanel(){
        JPanel statisticsPanel = new JPanel(new BorderLayout());

        JTextArea statisticsArea = new JTextArea(
                """
                        Total No. of Done Events: 42
                        Total No. of Planned Events: 13
                        """
                );
        statisticsArea.setEditable(false);
        statisticsArea.setBackground(null);
        statisticsPanel.add(statisticsArea);
        return statisticsPanel;
    }

    public JPanel createManagerTab(){
        var testDataGenerator = new TestDataGenerator();
        List<Category> categories = testDataGenerator.createCategories();

        CategoryTableModel categoryTableModel = new CategoryTableModel(categories);
        managerTabTable.setModel(categoryTableModel);

        JComboBox<ManagedEntity> managedEntityComboBox = new JComboBox<>(ManagedEntity.values());

        JPanel managerTab = new JPanel(new BorderLayout());
        JPanel managerTabToolPanel = new JPanel(new BorderLayout());
        managerTabToolPanel.add(managedEntityComboBox, BorderLayout.EAST);
        managerTab.add(managerTabToolPanel, BorderLayout.NORTH);
        managerTab.add(new JScrollPane(managerTabTable), BorderLayout.CENTER);

        managedEntityComboBox.addActionListener(e -> updateTableModel(e, managerTabTable));

        return managerTab;
    }

    private static void updateTableModel(ActionEvent e, JTable table) {
        Object source = e.getSource();

        if (source instanceof JComboBox<?> comboBox) {
            Object selectedItem = comboBox.getSelectedItem();
            if (selectedItem instanceof ManagedEntity selectedEntity) {

                TestDataGenerator testDataGenerator = new TestDataGenerator();
                TableModel newModel;

                switch (selectedEntity) {
                    case CATEGORIES -> {
                        List<Category> categories = testDataGenerator.createCategories();
                        newModel = new CategoryTableModel(categories); // Switch to CategoryTableModel
                    }
                    case TEMPLATES -> {
                        // TODO: implement templates to be displayed here, for now i will just re-use categories
                        //List<Template> templates = testDataGenerator.createTemplates();
                        //newModel = new TemplateTableModel(templates); // Switch to TemplateTableModel
                        List<Category> templates = testDataGenerator.createCategories();
                        newModel = new CategoryTableModel(templates); // Switch to CategoryTableModel
                    }
                    case INTERVALS -> {
                        List<TimeUnit> intervals = testDataGenerator.createTimeUnits();
                        newModel = new TimeUnitTableModel(intervals); // Switch to IntervalTableModel
                    }
                    default -> {
                        // default since otherwise the setModel function may have an uninitialized newModel
                        // TODO: once normal app logic is being implemented an error/exception should be thrown here
                        List<TimeUnit> timeUnits = testDataGenerator.createTimeUnits();
                        newModel = new TimeUnitTableModel(timeUnits); // Default to TimeUnitTableModel
                    }
                }

                table.setModel(newModel);
            }
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        var frame = new JFrame("TODO Manager");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    private JTable createTodoEventTable(List<TodoEvent> todoEvents) {
        var model = new EventTableModel(todoEvents);
        var table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    private JTable createCategoryTable(List<Category> categories) {
        var model = new CategoryTableModel(categories);
        var table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    private JPopupMenu createPopupMenu() {
        var menu = new JPopupMenu();
        menu.add(addAction);
        menu.add(editAction);
        menu.add(deleteAction);
        return menu;
    }

    private JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        fileMenu.add(importAction);
        fileMenu.add(exportAction);
        fileMenu.addSeparator();
        fileMenu.add(quitAction);
        menuBar.add(fileMenu);

        var editMenu = new JMenu("Edit");
        editMenu.setMnemonic('e');
        editMenu.add(addAction);
        editMenu.add(editAction);
        editMenu.add(deleteAction);
        menuBar.add(editMenu);

        var optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic('o');
        menuBar.add(optionsMenu);

        var helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');
        menuBar.add(helpMenu);

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
