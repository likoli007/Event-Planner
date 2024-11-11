package cz.muni.fi.pv168.project.ui;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacade;
import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacadeImpl;
import cz.muni.fi.pv168.project.business.filter.TodoEventFilter;
import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.service.crud.*;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainWindow {
    private final JFrame frame;
    private final JTable eventTable;
    private final JTable managerTabTable;
    private JTable currentTable;

    private final CrudService<TodoEvent> eventCrudService;
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
    JTextArea statisticsLengthArea;
    private boolean categoryTableShown = true;
    private final TodoEventFilter filter = new TodoEventFilter();

    public MainWindow() {
        frame = createFrame();

        var testDataGenerator = new TestDataGenerator();

        var categoryRepository = new InMemoryRepository<>(testDataGenerator.createCategories());
        var templateRepository = new InMemoryRepository<>(testDataGenerator.createTemplates());
        var timeUnitRepository = new InMemoryRepository<>(testDataGenerator.createTimeUnits());
        var eventRepository = new InMemoryRepository<>(testDataGenerator.createTodoEvents());

        categoryCrudService = new CategoryCrudService(categoryRepository);
        templateCrudService = new TemplateCrudService(templateRepository);
        timeUnitCrudService = new TimeUnitCrudService(timeUnitRepository);
        eventCrudService = new TodoEventCrudService(eventRepository);
        todoEventsServiceFacade = new TodoEventsServiceFacadeImpl(eventCrudService);


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
                "Total No. of Tasks With Selected Category(ies): " + categoryEventCount + "\n" +
                "Percentage of Total Tasks With Selected Category: " + String.format("%.1f", percentage) + "%\n"
        );

    }

    public void computeEventStatistics(){
        int doneEvents = 0;
        int plannedEvents = 0;
        int totalEvents = 0;
        int totalLength = 0;

        int doneColumnIndex = eventTableModel.getColumnIndexByName("Done");
        int intervalColumnIndex = eventTableModel.getColumnIndexByName("Interval");

        for (int i = 0; i < eventTable.getRowCount(); i++) {
            if (eventTable.getValueAt(i, doneColumnIndex) != null) {
                if (eventTable.getValueAt(i, doneColumnIndex).equals(true)) {
                    doneEvents++;

                    //since table column is of string type not interval, need to get string value
                    String interval = (String) eventTable.getValueAt(i, intervalColumnIndex);
                    Pattern pattern = Pattern.compile("(\\d+)\\s+min");
                    Matcher matcher = pattern.matcher(interval);

                    if (matcher.find()) {
                        int number = Integer.parseInt(matcher.group(1));
                        totalLength += number;
                    }
                }
                else{
                    plannedEvents++;

                }
                totalEvents++;
            }
        }

        statisticsArea.setText(
              "Total No. of Done Events: " + doneEvents + "\n" +
              "Total No. of Planned Events: " + plannedEvents + "\n"
        );
        statisticsLengthArea.setText(
              "Total length of done events: " + totalLength + " min\n" +
              "Total number of events: " + totalEvents + "\n"
        );
    }
    //TODO: actual computing of statistics
    // can use this to display statistics between the different tabs, left alone for now
    //  i.e. use createStatisticsPanel to just create the panel, then make a 'changeDisplayedStatistics' function
    //  which sets the currently relevant statistics
    public JPanel createStatisticsPanel(){
        JPanel statisticsPanel = new JPanel(new BorderLayout());

        statisticsArea = new JTextArea();

        statisticsLengthArea = new JTextArea();

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
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        TableModel newModel;

        switch (selectedEntity) {
            case CATEGORIES -> {
                newModel = categoryTableModel;

                categoryStatisticsArea.setVisible(true);
                //TODO: statistics like this should be in its own function where they will be calculated
            }
            case TEMPLATES -> {
                newModel = templateTableModel;
                categoryStatisticsArea.setVisible(false);
                //TODO: statistics for used templates? for now leaving blank
                //statisticsArea.setText("");
            }
            case INTERVALS -> {
                newModel = timeUnitTableModel;
                categoryStatisticsArea.setVisible(false);
                //TODO: statistics for used intervals? for now leaving blank
                //statisticsArea.setText("");
            }
            default -> {
                newModel = timeUnitTableModel;
                categoryStatisticsArea.setVisible(false);
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

        importAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt I"));
        exportAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt E"));
        quitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt Q"));

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
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel fromLabel = new JLabel("From Date:");
        DatePicker fromDatePicker = new DatePicker();

        JLabel fromTimeLabel = new JLabel("Time:");
        TimePicker fromTimePicker = new TimePicker();

        JLabel toLabel = new JLabel("To Date:");
        DatePicker toDatePicker = new DatePicker();

        JLabel toTimeLabel = new JLabel("Time:");
        TimePicker toTimePicker = new TimePicker();

        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> {
            fromDatePicker.setDateToToday();
            toDatePicker.setDateToToday();
            fromTimePicker.setTime(LocalTime.MIN);
            toTimePicker.setTime(LocalTime.MAX);
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
        List<String> units = new ArrayList<String>();
        units.add(null);  // Add null as the first "no unit" option
        units.addAll(timeUnitCrudService.findAll().stream().map(TimeUnit::getName).toList());
        JComboBox<String> unitComboBox = new JComboBox<>(units.toArray(new String[0]));

        JLabel categoryLabel = new JLabel("Category:");
        List<String> categories = new ArrayList<String>();
        categories.add(null);  // Add null as the first "no category" option
        categories.addAll(categoryCrudService.findAll().stream().map(Category::getName).toList());
        JComboBox<String> categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));


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
            categoryComboBox.setSelectedIndex(0);
            unitComboBox.setSelectedIndex(0);

            filter.clear();
        });

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
            String selectedUnit = (String) unitComboBox.getSelectedItem();
            filter.setSelectedUnit(selectedUnit);
            eventTableModel.refetch(filter);
        });

        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            filter.setSelectedCategory(selectedCategory);
            eventTableModel.refetch(filter);
        });

        doneCheckBox.addActionListener(e -> {
            boolean done = doneCheckBox.isSelected();
            var isDone = done ? Boolean.TRUE : null;
            filter.setDone(isDone);
            plannedCheckBox.setSelected(false);
            eventTableModel.refetch(filter);
        });

        plannedCheckBox.addActionListener(e -> {
            boolean planned = plannedCheckBox.isSelected();
            var isPlanned = planned ? Boolean.FALSE : null;
            filter.setDone(isPlanned);
            doneCheckBox.setSelected(false);
            eventTableModel.refetch(filter);
        });

       List<Component> components = List.of(
            fromLabel, fromDatePicker, fromTimeLabel, fromTimePicker,
            toLabel, toDatePicker, toTimeLabel, toTimePicker,
            todayButton, thisWeekButton,
            unitLabel, unitComboBox,
            categoryLabel, categoryComboBox,
            statusLabel, doneCheckBox, plannedCheckBox, clearButton
       );

       for (Component component : components) {
            filterPanel.add(component);
       }

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
