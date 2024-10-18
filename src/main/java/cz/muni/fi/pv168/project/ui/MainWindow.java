package cz.muni.fi.pv168.project.ui;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.action.AddAction;
import cz.muni.fi.pv168.project.ui.action.DeleteAction;
import cz.muni.fi.pv168.project.ui.action.EditAction;
import cz.muni.fi.pv168.project.ui.action.QuitAction;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;
import cz.muni.fi.pv168.project.ui.model.TimeUnitTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
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

    public JPanel createManagerTab(){
        var testDataGenerator = new TestDataGenerator();
        List<TimeUnit> timeUnits = testDataGenerator.createTimeUnits();

        TimeUnitTableModel timeUnitTableModel = new TimeUnitTableModel(timeUnits);
        JTable timeUnitTable = createTable(timeUnitTableModel);
        // TODO:
        // timeUnitTable.setComponentPopupMenu( );

        String[] managedEntities = {"Categories", "Templates", "Intervals"};
        JComboBox managedEntityCombobox = new JComboBox(managedEntities);

        JPanel managerTab = new JPanel(new BorderLayout());
        JPanel managerTabToolPanel = new JPanel(new BorderLayout());
        managerTabToolPanel.add(managedEntityCombobox, BorderLayout.EAST);
        managerTab.add(managerTabToolPanel, BorderLayout.NORTH);
        managerTab.add(new JScrollPane(timeUnitTable), BorderLayout.CENTER);

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
