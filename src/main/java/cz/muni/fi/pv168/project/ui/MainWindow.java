package cz.muni.fi.pv168.project.ui;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.action.AddAction;
import cz.muni.fi.pv168.project.ui.action.DeleteAction;
import cz.muni.fi.pv168.project.ui.action.EditAction;
import cz.muni.fi.pv168.project.ui.action.QuitAction;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.util.List;

public class MainWindow {
    private final JFrame frame;
    private final JTable eventTable;

    private final Action quitAction = new QuitAction();
    private final Action addAction;
    private final Action deleteAction;
    private final Action editAction;

    public MainWindow() {
        frame = createFrame();

        var testDataGenerator = new TestDataGenerator();
        eventTable = createTodoEventTable(testDataGenerator.createTodoEvents(10));

        editAction = new EditAction(eventTable);
        deleteAction = new DeleteAction();
        addAction = new AddAction(eventTable);

        eventTable.setComponentPopupMenu(createEventTablePopupMenu());
        eventTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRowsCount = eventTable.getSelectedRowCount();
            changeActionsState(selectedRowsCount);
        });

        frame.add(new JScrollPane(eventTable), BorderLayout.CENTER);
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

    private JTable createTodoEventTable(List<TodoEvent> todoEvents) {
        var model = new EventTableModel(todoEvents);
        var table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    private JPopupMenu createEventTablePopupMenu() {
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
