package cz.muni.fi.pv168.project.ui;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.ManagedEntity;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.action.*;
import cz.muni.fi.pv168.project.ui.model.CategoryTableModel;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;
import cz.muni.fi.pv168.project.ui.model.TimeUnitTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
    private final Action aboutAction;
    private final Action keybindsAction;
    private final Action contactAction;

    public MainWindow() {
        frame = createFrame();

        var testDataGenerator = new TestDataGenerator();
        eventTable = createTodoEventTable(testDataGenerator.createTodoEvents(10));
        managerTabTable = createCategoryTable(testDataGenerator.createCategories());

        currentTable = eventTable;

        addAction = new AddAction(() -> currentTable);
        deleteAction = new DeleteAction(() -> currentTable);
        editAction = new EditAction(() -> currentTable);
        importAction = new ImportAction(frame);
        exportAction = new ExportAction(frame);
        aboutAction = new AboutAction(frame);
        keybindsAction = new KeybindsAction(frame);
        contactAction = new ContactAction(frame);

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
        eventsTab.add(createFilterPanel(), BorderLayout.NORTH);
        eventsTab.add(new JScrollPane(eventTable), BorderLayout.CENTER);
        eventTable.setComponentPopupMenu(createPopupMenu());
        eventsTab.add(createStatisticsPanel(), BorderLayout.SOUTH);


        return eventsTab;
    }

    //TODO: actual computing of statistics
    // can use this to display statistics between the different tabs, left alone for now
    //  i.e. use createStatisticsPanel to just create the panel, then make a 'changeDisplayedStatistics' function
    //  which sets the currently relevant statistics
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

        JTextArea statisticsArea = new JTextArea();
        statisticsArea.setEditable(false);
        statisticsArea.setBackground(null);
        // Default text shown
        // TODO: in the future fetch these statistics
        statisticsArea.setText("""
            Total No. of Tasks With Selected Category: 5
            Percentage of Total Tasks With Selected Category: 14%
            """);


        JPanel managerTab = new JPanel(new BorderLayout());
        JPanel managerTabToolPanel = new JPanel(new BorderLayout());
        managerTabToolPanel.add(managedEntityComboBox, BorderLayout.EAST);
        managerTab.add(managerTabToolPanel, BorderLayout.NORTH);


        managerTab.add(new JScrollPane(managerTabTable), BorderLayout.CENTER);
        managerTab.add(statisticsArea, BorderLayout.SOUTH);

        managedEntityComboBox.addActionListener(e -> updateTableModel(e, managerTabTable, statisticsArea));

        return managerTab;
    }

    private static void updateTableModel(ActionEvent e, JTable table, JTextArea statisticsArea) {
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

                        statisticsArea.setText("""
                            Total No. of Tasks With Selected Category: 5
                            Percentage of Total Tasks With Selected Category: 14%
                            """);
                        //TODO: statistics like this should be in its own function where they will be calculated
                    }
                    case TEMPLATES -> {
                        // TODO: implement templates to be displayed here, for now i will just re-use categories
                        //List<Template> templates = testDataGenerator.createTemplates();
                        //newModel = new TemplateTableModel(templates); // Switch to TemplateTableModel
                        List<Category> templates = testDataGenerator.createCategories();
                        newModel = new CategoryTableModel(templates); // Switch to CategoryTableModel

                        //TODO: statistics for used templates? for now leaving blank
                        statisticsArea.setText("");
                    }
                    case INTERVALS -> {
                        List<TimeUnit> intervals = testDataGenerator.createTimeUnits();
                        newModel = new TimeUnitTableModel(intervals); // Switch to IntervalTableModel

                        //TODO: statistics for used intervals? for now leaving blank
                        statisticsArea.setText("");
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
        helpMenu.add(aboutAction);
        helpMenu.add(keybindsAction);
        helpMenu.add(contactAction);
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

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel fromLabel = new JLabel("From:");
        JSpinner fromTime = new JSpinner(new SpinnerDateModel());
        fromTime.setEditor(new JSpinner.DateEditor(fromTime, "yyyy-MM-dd HH:mm"));

        JLabel toLabel = new JLabel("To:");
        JSpinner toTime = new JSpinner(new SpinnerDateModel());
        toTime.setEditor(new JSpinner.DateEditor(toTime, "yyyy-MM-dd HH:mm"));

        JLabel unitLabel = new JLabel("Units:");
        JComboBox<String> unitComboBox = createMultiSelectComboBox(new String[]{"Minutes", "Hours", "Class"});

        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryComboBox = createMultiSelectComboBox(new String[]{"Work", "Study", "Exercise"});

        JLabel statusLabel = new JLabel("Status:");
        JCheckBox doneCheckBox = new JCheckBox("Done");
        JCheckBox plannedCheckBox = new JCheckBox("Planned");

        JButton clearButton = new JButton("Clear");

        filterPanel.add(fromLabel);
        filterPanel.add(fromTime);
        filterPanel.add(toLabel);
        filterPanel.add(toTime);
        filterPanel.add(unitLabel);
        filterPanel.add(unitComboBox);
        filterPanel.add(categoryLabel);
        filterPanel.add(categoryComboBox);
        filterPanel.add(statusLabel);
        filterPanel.add(doneCheckBox);
        filterPanel.add(plannedCheckBox);
        filterPanel.add(clearButton);

        return filterPanel;
    }

    private static JComboBox<String> createMultiSelectComboBox(String[] options) {
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"Select"});
        comboBox.setPrototypeDisplayValue("Select");

        JPopupMenu popupMenu = new JPopupMenu();
        List<JCheckBox> checkBoxes = new ArrayList<>();

        for (String option : options) {
            JCheckBox checkBox = new JCheckBox(option);
            checkBoxes.add(checkBox);
            popupMenu.add(checkBox);
        }

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (popupMenu.isShowing()) {
                    popupMenu.setVisible(false);
                } else {
                    popupMenu.show(comboBox, 0, comboBox.getHeight());
                }
            }
        });

        return comboBox;
    }
}
