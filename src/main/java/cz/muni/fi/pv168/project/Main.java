package cz.muni.fi.pv168.project;

import com.formdev.flatlaf.FlatLightLaf;
import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacadeImpl;
import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.business.service.crud.CategoryCrudService;
import cz.muni.fi.pv168.project.business.service.crud.TemplateCrudService;
import cz.muni.fi.pv168.project.business.service.crud.TimeUnitCrudService;
import cz.muni.fi.pv168.project.business.service.crud.TodoEventCrudService;
import cz.muni.fi.pv168.project.business.service.export.GenericExportService;
import cz.muni.fi.pv168.project.business.service.export.GenericImportService;
import cz.muni.fi.pv168.project.business.service.export.JSONFileExporter;
import cz.muni.fi.pv168.project.business.service.export.JSONFileImporter;
import cz.muni.fi.pv168.project.storage.InMemoryRepository;
import cz.muni.fi.pv168.project.storage.sql.CategorySqlRepository;
import cz.muni.fi.pv168.project.storage.sql.TemplateSqlRepository;
import cz.muni.fi.pv168.project.storage.sql.TimeUnitSqlRepository;
import cz.muni.fi.pv168.project.storage.sql.dao.CategoryDao;
import cz.muni.fi.pv168.project.storage.sql.dao.TemplateDao;
import cz.muni.fi.pv168.project.storage.sql.dao.TimeUnitDao;
import cz.muni.fi.pv168.project.storage.sql.db.DatabaseManager;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionConnectionSupplier;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionExecutorImpl;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionManagerImpl;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.CategoryMapper;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.TemplateMapper;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.TimeUnitMapper;
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

        DatabaseManager databaseManager = DatabaseManager.createProductionInstance();
        databaseManager.initSchema();

        var transactionManager = new TransactionManagerImpl(databaseManager);
        var transactionExecutor = new TransactionExecutorImpl(transactionManager::beginTransaction);
        var transactionConnectionSupplier = new TransactionConnectionSupplier(transactionManager, databaseManager);

        var categoryDao = new CategoryDao(transactionConnectionSupplier);
        var categoryMapper = new CategoryMapper();
        var categoryRepository = new CategorySqlRepository(categoryDao, categoryMapper);

        var timeUnitDao = new TimeUnitDao(transactionConnectionSupplier);
        var timeUnitMapper = new TimeUnitMapper();
        var timeUnitRepository = new TimeUnitSqlRepository(timeUnitDao, timeUnitMapper);

        var templateDao = new TemplateDao(transactionConnectionSupplier, transactionExecutor);
        var templateMapper = new TemplateMapper(timeUnitRepository, categoryRepository);
        var templateRepository = new TemplateSqlRepository(templateDao, templateMapper);
//        var templateRepository = new InMemoryRepository<>(testDataGenerator.createTemplates());
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
