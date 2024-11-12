package cz.muni.fi.pv168.project;

import com.formdev.flatlaf.FlatLightLaf;
import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacadeImpl;
import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.service.crud.CategoryCrudService;
import cz.muni.fi.pv168.project.service.crud.TemplateCrudService;
import cz.muni.fi.pv168.project.service.crud.TimeUnitCrudService;
import cz.muni.fi.pv168.project.service.crud.TodoEventCrudService;
import cz.muni.fi.pv168.project.service.export.GenericExportService;
import cz.muni.fi.pv168.project.service.export.GenericImportService;
import cz.muni.fi.pv168.project.service.export.JSONFileExporter;
import cz.muni.fi.pv168.project.service.export.JSONFileImporter;
import cz.muni.fi.pv168.project.storage.InMemoryRepository;
import cz.muni.fi.pv168.project.ui.MainWindow;

import javax.swing.UIManager;
import java.awt.EventQueue;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The entry point of the application.
 */
public class Main {

    public static void main(String[] args) {
        var testDataGenerator = new TestDataGenerator();

        var categoryRepository = new InMemoryRepository<>(testDataGenerator.createCategories());
        var templateRepository = new InMemoryRepository<>(testDataGenerator.createTemplates());
        var timeUnitRepository = new InMemoryRepository<>(testDataGenerator.createTimeUnits());
        var eventRepository = new InMemoryRepository<>(testDataGenerator.createTodoEvents());

        var categoryCrudService = new CategoryCrudService(categoryRepository);
        var templateCrudService = new TemplateCrudService(templateRepository);
        var timeUnitCrudService = new TimeUnitCrudService(timeUnitRepository);

        var eventCrudService = new TodoEventCrudService(eventRepository);
        var todoEventsServiceFacade = new TodoEventsServiceFacadeImpl(eventCrudService);

        var importService = new GenericImportService(categoryCrudService, timeUnitCrudService,
                templateCrudService, todoEventsServiceFacade, List.of(new JSONFileImporter()));

        var exportService = new GenericExportService(categoryCrudService, timeUnitCrudService,
                templateCrudService, todoEventsServiceFacade, List.of(new JSONFileExporter()));

        initFlatLafLookAndFeel();
        EventQueue.invokeLater(() -> new MainWindow(todoEventsServiceFacade, categoryCrudService,
                templateCrudService, timeUnitCrudService, importService, exportService).show());
    }

    private static void initFlatLafLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Layout initialization failed", ex);
        }
    }
}
