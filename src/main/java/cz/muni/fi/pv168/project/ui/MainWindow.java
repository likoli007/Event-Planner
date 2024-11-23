package cz.muni.fi.pv168.project.ui;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacade;
import cz.muni.fi.pv168.project.business.filter.TodoEventFilter;
import cz.muni.fi.pv168.project.business.service.crud.CrudService;
import cz.muni.fi.pv168.project.business.service.export.GenericExportService;
import cz.muni.fi.pv168.project.business.service.export.GenericImportService;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.ui.action.*;
import cz.muni.fi.pv168.project.ui.action.add.*;
import cz.muni.fi.pv168.project.ui.model.*;
import cz.muni.fi.pv168.project.ui.renderer.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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

    private final GenericImportService importService;
    private final GenericExportService exportService;


    private final CrudService<Category> categoryCrudService;
    private final CrudService<Template> templateCrudService;
    private final CrudService<TimeUnit> timeUnitCrudService;
    private final TodoEventsServiceFacade todoEventsServiceFacade;



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

    //panels used for showing statistics
    JTextArea statisticsArea;
    JTextArea catgoryStatisticsArea;
    private boolean categoryTableShown = true;
    private final TodoEventFilter filter = new TodoEventFilter();

    public MainWindow( TodoEventsServiceFacade todoEventsServiceFacade,
                      CrudService<Category> categoryCrudService,
                      CrudService<Template> templateCrudService,
                      CrudService<TimeUnit> timeUnitCrudService,
                      GenericImportService importService,
                      GenericExportService exportService) {


        frame = createFrame();

        this.todoEventsServiceFacade = todoEventsServiceFacade;
        this.categoryCrudService = categoryCrudService;
        this.templateCrudService = templateCrudService;
        this.timeUnitCrudService = timeUnitCrudService;

        this.importService = importService;
        this.exportService = exportService;


        eventTableModel = new EventTableModel(todoEventsServiceFacade, filter);
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

        importAction = new ImportAction(frame, importService, this::refresh);


        exportAction = new ExportAction(frame, exportService,todoEventsServiceFacade::getFilteredEvents,
                this.todoEventsServiceFacade::findAll
                );


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
                computeEventStatistics();
            } else if (selectedIndex == 1) {
                currentTable = managerTabTable;
                if(categoryTableShown) {
                    computeCategoryStatistics();
                }
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
                if(categoryTableShown) {
                    computeCategoryStatistics();
                }
            }
        });

        eventTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                computeEventStatistics();
            }
        });
        computeEventStatistics();

        frame.add(tabPanel, BorderLayout.CENTER);
        frame.add(createToolbar(), BorderLayout.BEFORE_FIRST_LINE);
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        changeActionsState(0);
        setupKeyBindings(frame.getRootPane());
        setupSelectAllShortcut(eventTable);
        setupSelectAllShortcut(managerTabTable);
    }

    private void refresh() {
        eventTableModel.refresh();
        categoryTableModel.refresh();
        templateTableModel.refresh();
        timeUnitTableModel.refresh();
    }

    public JPanel createEventsTab(){
        JPanel eventsTab = new JPanel(new BorderLayout());
        eventsTab.add(createFilterPanel(), BorderLayout.NORTH);
        eventsTab.add(new JScrollPane(eventTable), BorderLayout.CENTER);
        eventTable.setComponentPopupMenu(createPopupMenu());
        eventsTab.add(createStatisticsPanel(), BorderLayout.SOUTH);


        return eventsTab;
    }


    public void computeCategoryStatistics(){
        int categoryEventCount = 0;
        int categoryColumnIndex = eventTableModel.getColumnIndexByName("Categories");
        int[] selectedRows = managerTabTable.getSelectedRows();


        int totalRows = eventTable.getRowCount();
        for (int i = 0; i < eventTable.getRowCount(); i++) {
            for (int j = 0; j < selectedRows.length; j++) {
                boolean categoryFound = false;
                Category category = (Category) managerTabTable.getValueAt(selectedRows[j], 0);
                for (Category eventCategory : (List<Category>) eventTable.getValueAt(i, categoryColumnIndex)) {
                    if (eventCategory.equals(category)) {
                        categoryEventCount++;
                        categoryFound = true;
                        break;
                    }
                }
                if (categoryFound) {
                    break;
                }
            }
        }

        double percentage = ((double) categoryEventCount / (double) totalRows) * 100.0;

        catgoryStatisticsArea.setText(
                "Tasks With Selected Categories: " + categoryEventCount + " (" + String.format("%.1f", percentage) + "%)"
        );

    }

    public void computeEventStatistics(){
        int doneEvents = 0;
        int plannedEvents = 0;
        int totalEvents = 0;
        int totalDoneLength = 0;
        int totalPlannedLength = 0;


        List<TodoEvent> eventList = todoEventsServiceFacade.getFilteredEvents();
        List<TodoEvent> allEventList = todoEventsServiceFacade.findAll();


        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).isDone()) {
                doneEvents++;
                Interval interval = eventList.get(i).getInterval();
                TimeUnit timeUnit = interval.getTimeUnit();
                totalDoneLength += interval.getAmount() * timeUnit.getMinutes();
            } else {
                plannedEvents++;
                Interval interval = eventList.get(i).getInterval();
                TimeUnit timeUnit = interval.getTimeUnit();
                totalPlannedLength += interval.getAmount() * timeUnit.getMinutes();
            }
            totalEvents++;
        }

        int allDoneEvents = 0;
        int allPlannedEvents = 0;
        int allTotalEvents = 0;
        int allTotalDoneLength = 0;
        int allTotalPlannedLength = 0;
        if (eventList.size() != allEventList.size()) {
            for (int i = 0; i < allEventList.size(); i++) {
                if (allEventList.get(i).isDone()) {
                    allDoneEvents++;
                    Interval interval = allEventList.get(i).getInterval();
                    TimeUnit timeUnit = interval.getTimeUnit();
                    allTotalDoneLength += interval.getAmount() * timeUnit.getMinutes();
                }
                else{
                    allPlannedEvents++;
                    Interval interval = allEventList.get(i).getInterval();
                    TimeUnit timeUnit = interval.getTimeUnit();
                    allTotalPlannedLength += interval.getAmount() * timeUnit.getMinutes();
                }
                allTotalEvents++;
            }

            statisticsArea.setText(
                    "Total events: " + computePadding(allTotalEvents) + allTotalEvents + " (" + totalEvents + ") | " +
                            "Done events: " + computePadding(allDoneEvents) + allDoneEvents + " (" + doneEvents +") | " +
                            "Length of done events: " + computePadding(allTotalDoneLength) + allTotalDoneLength + " min ("
                            + totalDoneLength + " min) | " +
                            "Planned events: " + computePadding(allPlannedEvents) + allPlannedEvents + " (" + plannedEvents +") | " +
                            "Length of planned events: " + computePadding(allTotalPlannedLength) + allTotalPlannedLength + " min" +
                            " (" + totalPlannedLength + " min)\n"
            );
            return;
        }


        statisticsArea.setText(
                "Total events: " + computePadding(totalEvents) + totalEvents + " | " +
                        "Done events: " + computePadding(doneEvents) + doneEvents + " | " +
                        "Length of done events: " + computePadding(totalDoneLength) + totalDoneLength + " min | " +
                        "Planned events: " + computePadding(plannedEvents) + plannedEvents + " | " +
                        "Length of planned events: " + computePadding(totalPlannedLength) + totalPlannedLength + " min\n"
        );




    
    }

    String computePadding(int number){
        int maxDigits = 6;
        int count = maxDigits - String.valueOf(number).length();
        if (count > 0)
            return " ".repeat(count);
        return "";
    }

    public JPanel createStatisticsPanel(){
        JPanel statisticsPanel = new JPanel(new BorderLayout());

        statisticsArea = new JTextArea();
        statisticsArea.setEditable(false);
        statisticsArea.setBackground(null);

        statisticsPanel.add(statisticsArea, BorderLayout.WEST);
        return statisticsPanel;
    }

    public JPanel createManagerTab(){
        managerTabTable.setModel(categoryTableModel);

        JButton templateButton = new JButton("Templates");
        JButton categoryButton = new JButton("Categories");
        JButton intervalButton = new JButton("Intervals");


        catgoryStatisticsArea = new JTextArea();
        catgoryStatisticsArea.setEditable(false);
        catgoryStatisticsArea.setBackground(null);
        computeCategoryStatistics();


        JPanel managerTab = new JPanel(new BorderLayout());
        JPanel managerTabToolPanel = new JPanel();
        managerTabToolPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        managerTabToolPanel.add(categoryButton);
        managerTabToolPanel.add(templateButton);
        managerTabToolPanel.add(intervalButton);
        managerTab.add(managerTabToolPanel, BorderLayout.NORTH);


        managerTab.add(new JScrollPane(managerTabTable), BorderLayout.CENTER);
        managerTab.add(catgoryStatisticsArea, BorderLayout.SOUTH);

        categoryButton.addActionListener(e -> {
            updateTableModel(ManagedEntity.CATEGORIES, managerTabTable, catgoryStatisticsArea);
            categoryTableShown = true;
            computeCategoryStatistics();
        });
        templateButton.addActionListener(e -> {
            updateTableModel(ManagedEntity.TEMPLATES, managerTabTable, catgoryStatisticsArea);
            categoryTableShown = false;
        });
        intervalButton.addActionListener(e -> {
            updateTableModel(ManagedEntity.INTERVALS, managerTabTable, catgoryStatisticsArea);
            categoryTableShown = false;
        });

        return managerTab;
    }

    private void updateTableModel(ManagedEntity selectedEntity, JTable table, JTextArea categoryStatisticsArea) {
        switch (selectedEntity) {
            case TEMPLATES -> {
                table.setModel(templateTableModel);
                categoryStatisticsArea.setVisible(false);
                categoryTableShown = false;
            }
            case INTERVALS -> {
                table.setModel(timeUnitTableModel);
                categoryStatisticsArea.setVisible(false);
                categoryTableShown = false;
                configureMinutesColumnRenderer(table);
            }
            default -> {
                table.setModel(categoryTableModel);
                categoryStatisticsArea.setVisible(true);
                categoryTableShown = true;
                computeCategoryStatistics();
            }
        }
    }

    private void configureMinutesColumnRenderer(JTable table) {
        int minutesColumnIndex = timeUnitTableModel.getColumnIndexByName("Minutes");
        if (minutesColumnIndex != -1) {
            TableColumnModel columnModel = table.getColumnModel();
            if (minutesColumnIndex < columnModel.getColumnCount()) {
                columnModel.getColumn(minutesColumnIndex).setCellRenderer(new LeftAlignedCellRenderer());
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

    private JTable createTodoEventTable(EventTableModel model) {
        var table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(LocalDateTime.class, new LocalDateTimeRenderer());

        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new EventTableCellRenderer());
        }

        int startColumnIndex = model.getColumnIndexByName("Start");
        if (startColumnIndex != -1) {
            TableColumn startColumn = table.getColumnModel().getColumn(startColumnIndex);
            startColumn.setCellRenderer(new LocalDateTimeRenderer());
            startColumn.setPreferredWidth(200);
            startColumn.setMaxWidth(200);
            startColumn.setMinWidth(200);
        }

        int intervalColumnIndex = model.getColumnIndexByName("Interval");
        if (intervalColumnIndex != -1) {
            TableColumn intervalColumn = table.getColumnModel().getColumn(intervalColumnIndex);
            intervalColumn.setPreferredWidth(150);
            intervalColumn.setMaxWidth(200);
            intervalColumn.setMinWidth(100);
        }

        int doneColumnIndex = model.getColumnIndexByName("Done");
        if (doneColumnIndex != -1) {
            TableColumn doneColumn = table.getColumnModel().getColumn(doneColumnIndex);
            doneColumn.setCellRenderer(table.getDefaultRenderer(Boolean.class));
            doneColumn.setCellEditor(table.getDefaultEditor(Boolean.class));

            doneColumn.setPreferredWidth(50);
            doneColumn.setMaxWidth(50);
            doneColumn.setMinWidth(50);
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

        importAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt I"));
        exportAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt E"));
        quitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt Q"));

        fileMenu.add(importAction);
        fileMenu.add(exportAction);
        fileMenu.addSeparator();
        fileMenu.add(quitAction);
        menuBar.add(fileMenu);

        var addMenu = new JMenu("Add");
        addMenu.setMnemonic('a');
        addMenu.add(new AddEvent(() -> currentTable, allTableModels));
        addMenu.add(new AddCategory(() -> currentTable, allTableModels));
        addMenu.add(new AddTemplate(() -> currentTable, allTableModels));
        addMenu.add(new AddTimeUnit(() -> currentTable, allTableModels));
        menuBar.add(addMenu);

        var helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');

        aboutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt A"));
        keybindsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt K"));
        contactAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt C"));

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
        JPanel filterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 1: From Date, From Time, To Date, To Time, Today and This Week buttons
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        filterPanel.add(new JLabel("From Date:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        DatePicker fromDatePicker = new DatePicker();
        filterPanel.add(fromDatePicker, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        filterPanel.add(new JLabel("Time:"), gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        TimePicker fromTimePicker = new TimePicker();
        filterPanel.add(fromTimePicker, gbc);

        gbc.gridx = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        filterPanel.add(new JLabel("To Date:"), gbc);

        gbc.gridx = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        DatePicker toDatePicker = new DatePicker();
        filterPanel.add(toDatePicker, gbc);

        gbc.gridx = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        filterPanel.add(new JLabel("Time:"), gbc);

        gbc.gridx = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        TimePicker toTimePicker = new TimePicker();
        filterPanel.add(toTimePicker, gbc);

        gbc.gridx = 8;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> {
            fromDatePicker.setDateToToday();
            toDatePicker.setDateToToday();
            fromTimePicker.setTime(LocalTime.MIN);
            toTimePicker.setTime(LocalTime.MAX);
        });
        filterPanel.add(todayButton, gbc);

        gbc.gridx = 9;
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
        filterPanel.add(thisWeekButton, gbc);

        // Row 2: Category, Units, Status, and Clear button
        gbc.gridy = 1;
        gbc.gridx = 0;
        filterPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        List<String> categories = new ArrayList<>();
        categories.add(null);
        categories.addAll(categoryCrudService.findAll().stream().map(Category::getName).toList());
        JComboBox<String> categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));
        filterPanel.add(categoryComboBox, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        filterPanel.add(new JLabel("Units:"), gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        List<String> units = new ArrayList<>();
        units.add(null);
        units.addAll(timeUnitCrudService.findAll().stream().map(TimeUnit::getName).toList());
        JComboBox<String> unitComboBox = new JComboBox<>(units.toArray(new String[0]));
        filterPanel.add(unitComboBox, gbc);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusPanel.add(new JLabel("Status:"));
        JRadioButton doneRadioButton = new JRadioButton("Done");
        JRadioButton plannedRadioButton = new JRadioButton("Planned");
        JRadioButton allRadioButton = new JRadioButton("All");
        allRadioButton.setSelected(true);

        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(doneRadioButton);
        statusGroup.add(plannedRadioButton);
        statusGroup.add(allRadioButton);

        statusPanel.add(doneRadioButton);
        statusPanel.add(plannedRadioButton);
        statusPanel.add(allRadioButton);

        gbc.gridx = 4;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        filterPanel.add(statusPanel, gbc);

        gbc.gridx = 9;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            fromDatePicker.clear();
            fromTimePicker.clear();
            toDatePicker.clear();
            toTimePicker.clear();
            statusGroup.clearSelection();
            allRadioButton.setSelected(true);
            categoryComboBox.setSelectedIndex(0);
            unitComboBox.setSelectedIndex(0);

            filter.setFromDate(null);
            filter.setFromTime(null);
            filter.setToDate(null);
            filter.setToTime(null);
            filter.setSelectedCategory(null);
            filter.setSelectedUnit(null);
            filter.setDone(null);

            eventTableModel.refetch(filter);
        });
        filterPanel.add(clearButton, gbc);

        // Add listener to update 'from' DateTime filter
        fromDatePicker.addDateChangeListener(event -> {
            filter.setFromDate(fromDatePicker.getDate());
            eventTableModel.refetch(filter);
        });

        fromTimePicker.addTimeChangeListener(event -> {
           filter.setFromTime(fromTimePicker.getTime());
           eventTableModel.refetch(filter);
        });

        toDatePicker.addDateChangeListener(event -> {
            filter.setToDate(toDatePicker.getDate());
            eventTableModel.refetch(filter);
        });

        toTimePicker.addTimeChangeListener(event -> {
            filter.setToTime(toTimePicker.getTime());
            eventTableModel.refetch(filter);
        });

        unitComboBox.addActionListener(e -> {
            filter.setSelectedUnit((String) unitComboBox.getSelectedItem());
            eventTableModel.refetch(filter);
        });

        categoryComboBox.addActionListener(e -> {
            filter.setSelectedCategory((String) categoryComboBox.getSelectedItem());
            eventTableModel.refetch(filter);
        });

        doneRadioButton.addActionListener(e -> {
            filter.setDone(Boolean.TRUE);
            eventTableModel.refetch(filter);
        });

        plannedRadioButton.addActionListener(e -> {
            filter.setDone(Boolean.FALSE);
            eventTableModel.refetch(filter);
        });

        allRadioButton.addActionListener(e -> {
            filter.setDone(null);
            eventTableModel.refetch(filter);
        });

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

    private void setupSelectAllShortcut(JTable table) {
        KeyStroke ctrlA = KeyStroke.getKeyStroke("control A");

        InputMap inputMap = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(ctrlA, "selectAll");

        ActionMap actionMap = table.getActionMap();
        actionMap.put("selectAll", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.selectAll();
            }
        });
    }

    private void setupKeyBindings(JComponent component) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ctrl N"), "addActionContextual");
        actionMap.put("addActionContextual", addActionContextual);

        inputMap.put(KeyStroke.getKeyStroke("ctrl D"), "deleteAction");
        actionMap.put("deleteAction", deleteAction);

        inputMap.put(KeyStroke.getKeyStroke("ctrl E"), "editAction");
        actionMap.put("editAction", editAction);

        inputMap.put(KeyStroke.getKeyStroke("ctrl Q"), "quitAction");
        actionMap.put("quitAction", quitAction);
    }
}
