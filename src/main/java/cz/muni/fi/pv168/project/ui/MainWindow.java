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
import java.awt.*;
import java.util.List;

public class MainWindow {
    private final JFrame frame;
    private final JTable eventTable;
    private final JTable timeUnitTable;
    private JTable currentTable;

    private final Action quitAction = new QuitAction();
    private final Action addAction;
    private final Action deleteAction;
    private final Action editAction;

    public MainWindow() {
        frame = createFrame();

        var testDataGenerator = new TestDataGenerator();
        eventTable = createTodoEventTable(testDataGenerator.createTodoEvents(10));
        timeUnitTable = createTimeUnitTable(testDataGenerator.createTimeUnits());

        currentTable = eventTable;

        addAction = new AddAction(currentTable);
        deleteAction = new DeleteAction();
        editAction = new EditAction(currentTable);

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
                currentTable = timeUnitTable;
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

        timeUnitTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && currentTable == timeUnitTable) {
                int selectedRowsCount = timeUnitTable.getSelectedRowCount();
                changeActionsState(selectedRowsCount);
            }
        });

        frame.add(tabPanel, BorderLayout.CENTER);
        frame.add(createToolbar(), BorderLayout.BEFORE_FIRST_LINE);
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        changeActionsState(0);
    }

    public void show() {
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        var frame = new JFrame("TODO list");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    private JPanel createEventsTab() {
        JPanel eventsTab = new JPanel(new BorderLayout());
        eventsTab.add(new JScrollPane(eventTable), BorderLayout.CENTER);

        eventTable.setComponentPopupMenu(createPopupMenu());
        return eventsTab;
    }

    private JPanel createManagerTab() {
        JPanel managerTab = new JPanel(new BorderLayout());
        managerTab.add(new JScrollPane(timeUnitTable), BorderLayout.CENTER);

        timeUnitTable.setComponentPopupMenu(createPopupMenu());
        return managerTab;
    }

    private JTable createTodoEventTable(List<TodoEvent> todoEvents) {
        var model = new EventTableModel(todoEvents);
        var table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    private JTable createTimeUnitTable(List<TimeUnit> timeUnits) {
        var model = new TimeUnitTableModel(timeUnits);
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
