package cz.muni.fi.pv168.project.ui;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.service.crud.CategoryCrudService;
import cz.muni.fi.pv168.project.service.crud.TemplateCrudService;
import cz.muni.fi.pv168.project.service.crud.TimeUnitCrudService;
import cz.muni.fi.pv168.project.service.crud.TodoEventCrudService;
import cz.muni.fi.pv168.project.service.export.GenericExportService;
import cz.muni.fi.pv168.project.service.export.GenericImportService;
import cz.muni.fi.pv168.project.service.export.JSONFileExporter;
import cz.muni.fi.pv168.project.service.export.JSONFileImporter;
import cz.muni.fi.pv168.project.storage.InMemoryRepository;
import cz.muni.fi.pv168.project.ui.action.*;
import cz.muni.fi.pv168.project.ui.action.add.*;
import cz.muni.fi.pv168.project.ui.model.*;
import cz.muni.fi.pv168.project.ui.renderer.EventTableCellRenderer;
import cz.muni.fi.pv168.project.ui.renderer.LocalDateTimeRenderer;
import cz.muni.fi.pv168.project.ui.renderer.LocalTimeRenderer;
import cz.muni.fi.pv168.project.ui.renderer.CategoryListRenderer;
import cz.muni.fi.pv168.project.ui.renderer.CategoryRenderer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {
    private final JFrame frame;
    private final JTable eventTable;
    private final JTable managerTabTable;
    private JTable currentTable;

    private final EventTableModel eventTableModel;
    private final CategoryTableModel categoryTableModel;
    private final TemplateTableModel templateTableModel;
    private final TimeUnitTableModel timeUnitTableModel;
    private final AllTableModels allTableModels;

    private final Action quitAction = new QuitAction();
    private final Action addActionContextual;
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

        var categoryRepository = new InMemoryRepository<>(testDataGenerator.createCategories());
        var templateRepository = new InMemoryRepository<>(testDataGenerator.createTemplates());
        var timeUnitRepository = new InMemoryRepository<>(testDataGenerator.createTimeUnits());
        var eventRepository = new InMemoryRepository<>(testDataGenerator.createTodoEvents());

        var categoryCrudService = new CategoryCrudService(categoryRepository);
        var templateCrudService = new TemplateCrudService(templateRepository);
        var timeUnitCrudService = new TimeUnitCrudService(timeUnitRepository);
        var eventCrudService = new TodoEventCrudService(eventRepository);

        var exportService = new GenericExportService(categoryCrudService, timeUnitCrudService,
                templateCrudService, eventCrudService, List.of(new JSONFileExporter()));

        var importService = new GenericImportService(categoryCrudService, timeUnitCrudService,
                templateCrudService, eventCrudService, List.of(new JSONFileImporter()));

        eventTableModel = new EventTableModel(eventCrudService);
        categoryTableModel = new CategoryTableModel(categoryCrudService);
        templateTableModel = new TemplateTableModel(templateCrudService);
        timeUnitTableModel = new TimeUnitTableModel(timeUnitCrudService);
        allTableModels = new AllTableModels(eventTableModel, categoryTableModel, templateTableModel, timeUnitTableModel);

        eventTable = createTodoEventTable(eventTableModel);
        managerTabTable = createCategoryTable(categoryTableModel);
        currentTable = eventTable;

        managerTabTable.setDefaultRenderer(List.class, new CategoryListRenderer());

        addActionContextual = new AddContextual(() -> currentTable, allTableModels);
        deleteAction = new DeleteAction(() -> currentTable, allTableModels);
        editAction = new EditAction(() -> currentTable, allTableModels);
        importAction = new ImportAction(frame, importService);
        exportAction = new ExportAction(frame, exportService);
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

        JTextArea statisticsLengthArea = new JTextArea(
                """
                Total length of done events: 189 minutes
                Total number of events: 55 events
                """
        );

        statisticsArea.setEditable(false);
        statisticsArea.setBackground(null);
        statisticsPanel.add(statisticsArea, BorderLayout.WEST);
        statisticsLengthArea.setEditable(false);
        statisticsLengthArea.setBackground(null);
        statisticsPanel.add(statisticsLengthArea, BorderLayout.EAST);
        return statisticsPanel;
    }

    public JPanel createManagerTab(){
        managerTabTable.setModel(categoryTableModel);

        JButton templateButton = new JButton("Templates");
        JButton categoryButton = new JButton("Categories");
        JButton intervalButton = new JButton("Intervals");

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
        JPanel managerTabToolPanel = new JPanel();
        managerTabToolPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        managerTabToolPanel.add(categoryButton);
        managerTabToolPanel.add(templateButton);
        managerTabToolPanel.add(intervalButton);
        managerTab.add(managerTabToolPanel, BorderLayout.NORTH);


        managerTab.add(new JScrollPane(managerTabTable), BorderLayout.CENTER);
        managerTab.add(statisticsArea, BorderLayout.SOUTH);

        categoryButton.addActionListener(e -> updateTableModel(ManagedEntity.CATEGORIES, managerTabTable, statisticsArea));
        templateButton.addActionListener(e -> updateTableModel(ManagedEntity.TEMPLATES, managerTabTable, statisticsArea));
        intervalButton.addActionListener(e -> updateTableModel(ManagedEntity.INTERVALS, managerTabTable, statisticsArea));

        return managerTab;
    }

    private void updateTableModel(ManagedEntity selectedEntity, JTable table, JTextArea statisticsArea) {
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        TableModel newModel;

        switch (selectedEntity) {
            case CATEGORIES -> {
                newModel = categoryTableModel;

                statisticsArea.setText("""
                    Total No. of Tasks With Selected Category: 5
                    Percentage of Total Tasks With Selected Category: 14%
                """);
                //TODO: statistics like this should be in its own function where they will be calculated
            }
            case TEMPLATES -> {
                newModel = templateTableModel;

                //TODO: statistics for used templates? for now leaving blank
                statisticsArea.setText("");
            }
            case INTERVALS -> {
                newModel = timeUnitTableModel;

                //TODO: statistics for used intervals? for now leaving blank
                statisticsArea.setText("");
            }
            default -> {
                newModel = timeUnitTableModel;
            }
        }

        table.setModel(newModel);
    }

    public void show() {
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        var frame = new JFrame("TODO Manager");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    private JTable createTodoEventTable(EventTableModel model) {
        var table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(LocalDateTime.class, new LocalDateTimeRenderer());

        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new EventTableCellRenderer());
        }

        int startColumnIndex = model.getColumnIndexByName("Start");
        if (startColumnIndex != -1) {
            table.getColumnModel().getColumn(startColumnIndex).setCellRenderer(new LocalDateTimeRenderer());
        }

        int doneColumnIndex = model.getColumnIndexByName("Done");
        if (doneColumnIndex != -1) {
            table.getColumnModel().getColumn(doneColumnIndex).setCellRenderer(table.getDefaultRenderer(Boolean.class));
            table.getColumnModel().getColumn(doneColumnIndex).setCellEditor(table.getDefaultEditor(Boolean.class));
        }
        int categoryColumnIndex = model.getColumnIndexByName("Categories");
        if (categoryColumnIndex != -1) {
            table.getColumnModel().getColumn(categoryColumnIndex).setCellRenderer(new CategoryListRenderer());
        }


        return table;
    }

    private JTable createCategoryTable(CategoryTableModel model) {
        var table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(LocalTime.class, new LocalTimeRenderer());
        table.setDefaultRenderer(Category.class, new CategoryRenderer());

        return table;
    }

    private JPopupMenu createPopupMenu() {
        var menu = new JPopupMenu();
        menu.add(addActionContextual);
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
        editMenu.add(new AddEvent(() -> currentTable, allTableModels));
        editMenu.add(new AddCategory(() -> currentTable, allTableModels));
        editMenu.add(new AddTemplate(() -> currentTable, allTableModels));
        editMenu.add(new AddTimeUnit(() -> currentTable, allTableModels));
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
        toolbar.add(addActionContextual);
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

        JLabel fromLabel = new JLabel("From Date:");
        DatePicker fromDatePicker = new DatePicker();
        fromDatePicker.setDateToToday();

        JLabel fromTimeLabel = new JLabel("Time:");
        TimePicker fromTimePicker = new TimePicker();
        fromTimePicker.setTimeToNow();

        JLabel toLabel = new JLabel("To Date:");
        DatePicker toDatePicker = new DatePicker();
        toDatePicker.setDateToToday();

        JLabel toTimeLabel = new JLabel("Time:");
        TimePicker toTimePicker = new TimePicker();
        toTimePicker.setTimeToNow();

        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> {
            fromDatePicker.setDateToToday();
            toDatePicker.setDateToToday();
            fromTimePicker.setTimeToNow();
            toTimePicker.setTimeToNow();
        });

        JButton thisWeekButton = new JButton("This Week");
        thisWeekButton.addActionListener(e -> {
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

            fromDatePicker.setDate(startOfWeek);
            toDatePicker.setDate(endOfWeek);
            fromTimePicker.setTime(LocalTime.MIN);
            toTimePicker.setTime(LocalTime.MAX);
        });

        JLabel unitLabel = new JLabel("Units:");
        JComboBox<String> unitComboBox = createMultiSelectComboBox(new String[]{"Minutes", "Hours", "Class"});

        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryComboBox = createMultiSelectComboBox(new String[]{"Work", "Study", "Exercise"});

        JLabel statusLabel = new JLabel("Status:");
        JCheckBox doneCheckBox = new JCheckBox("Done");
        JCheckBox plannedCheckBox = new JCheckBox("Planned");

        JButton clearButton = new JButton("Clear");

        clearButton.addActionListener(e -> {
            fromDatePicker.clear();
            fromTimePicker.clear();
            toDatePicker.clear();
            toTimePicker.clear();
            doneCheckBox.setSelected(false);
            plannedCheckBox.setSelected(false);
        });

        filterPanel.add(fromLabel);
        filterPanel.add(fromDatePicker);
        filterPanel.add(fromTimeLabel);
        filterPanel.add(fromTimePicker);

        filterPanel.add(toLabel);
        filterPanel.add(toDatePicker);
        filterPanel.add(toTimeLabel);
        filterPanel.add(toTimePicker);

        filterPanel.add(todayButton);
        filterPanel.add(thisWeekButton);

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
