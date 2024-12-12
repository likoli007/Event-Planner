package cz.muni.fi.pv168.project.wiring;

import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacade;
import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacadeImpl;
import cz.muni.fi.pv168.project.business.service.crud.*;
import cz.muni.fi.pv168.project.business.service.export.*;
import cz.muni.fi.pv168.project.business.service.validation.CategoryValidator;
import cz.muni.fi.pv168.project.business.service.validation.TemplateValidator;
import cz.muni.fi.pv168.project.business.service.validation.TimeUnitValidator;
import cz.muni.fi.pv168.project.business.service.validation.TodoEventValidator;
import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.storage.InMemoryRepository;
import cz.muni.fi.pv168.project.storage.sql.CategorySqlRepository;
import cz.muni.fi.pv168.project.storage.sql.TimeUnitSqlRepository;
import cz.muni.fi.pv168.project.storage.sql.dao.CategoryDao;
import cz.muni.fi.pv168.project.storage.sql.dao.TimeUnitDao;
import cz.muni.fi.pv168.project.storage.sql.db.*;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.CategoryMapper;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.TimeUnitMapper;

// TODO: add TodoEvent and Template
/**
 * Common dependency provider for both production and test environment.
 */
public class CommonDependencyProvider implements DependencyProvider {

//    private final TodoEventSqlRepository todoEvents;
//    private final TemplateSqlRepository templates;
    private final CategorySqlRepository categories;
    private final TimeUnitSqlRepository timeUnits;
    private final DatabaseManager databaseManager;
    private final TransactionExecutor transactionExecutor;
//    private final CrudService<TodoEvent> todoEventCrudService;
//    private final CrudService<Template> templateCrudService;
    private final CrudService<Category> categoryCrudService;
    private final CrudService<TimeUnit> timeUnitCrudService;
    private final TodoEventsServiceFacade todoEventsServiceFacade;
    private final ImportService importService = null; // TODO: remove null
    private final ExportService exportService = null; // TODO: remove null
//    private final TodoEventValidator todoEventValidator;
//    private final TemplateValidator templateValidator;
    private final CategoryValidator categoryValidator;
    private final TimeUnitValidator timeUnitValidator;

    public CommonDependencyProvider(DatabaseManager databaseManager) {
//        todoEventValidator = new TodoEventValidator();
//        templateValidator = new TemplateValidator();
        categoryValidator = new CategoryValidator();
        timeUnitValidator = new TimeUnitValidator();

        this.databaseManager = databaseManager;
        var transactionManager = new TransactionManagerImpl(databaseManager);
        this.transactionExecutor = new TransactionExecutorImpl(transactionManager::beginTransaction);
        var transactionConnectionSupplier = new TransactionConnectionSupplier(transactionManager, databaseManager);

//        var todoEventMapper = new TodoEventMapper();
//        var todoEventDao = new TodoEventDao(transactionConnectionSupplier);
//
//        var templateMapper = new TemplateMapper();
//        var templatetDao = new TemplateDao(transactionConnectionSupplier);

        var categoryMapper = new CategoryMapper();
        var categoryDao = new CategoryDao(transactionConnectionSupplier);

        var timeUnitMapper = new TimeUnitMapper();
        var timeUnitDao = new TimeUnitDao(transactionConnectionSupplier);

//        this.todoEvents = new TodoEventSqlRepository(
//                todoEventDao,
//                todoEventMapper
//        );
//        this.templates = new TemplateSqlRepository(
//                templatetDao,
//                templateMapper
//        );
        this.categories = new CategorySqlRepository(
                categoryDao,
                categoryMapper
        );
        this.timeUnits = new TimeUnitSqlRepository(
                timeUnitDao,
                timeUnitMapper
        );

        var testDataGenerator = new TestDataGenerator();
        var todoEventRepository = new InMemoryRepository<>(testDataGenerator.createTodoEvents());
        var todoEventCrudService = new TodoEventCrudService(todoEventRepository);
        this.todoEventsServiceFacade = new TodoEventsServiceFacadeImpl(todoEventCrudService);


//        todoEventCrudService = new TodoEventCrudService(todoEvents);
//        templateCrudService = new TemplateCrudService(templates);
        categoryCrudService = new CategoryCrudService(categories);
        timeUnitCrudService = new TimeUnitCrudService(timeUnits);
//        exportService = new GenericExportService(
//                categoryCrudService,
//                timeUnitCrudService,
//                templateCrudService,
//                todoEventCrudService,
//                List.of(new JSONFileExporter())
//        );
//        var genericImportService = new GenericImportService(
//                categoryCrudService,
//                timeUnitCrudService,
//                templateCrudService,
//                todoEventCrudService,
//                List.of(new JSONFileImporter())
//        );
//        importService = new TransactionalImportService(genericImportService, transactionExecutor);
    }

    @Override
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

//    @Override
//    public TodoEventSqlRepository getTodoEventRepository() {
//        return todoEvents;
//    }
//
//    @Override
//    public TemplateSqlRepository getTemplateRepository() {
//        return templates;
//    }

    @Override
    public CategorySqlRepository getCategoryRepository() {
        return categories;
    }

    @Override
    public TimeUnitSqlRepository getTimeUnitRepository() {
        return timeUnits;
    }

    @Override
    public TransactionExecutor getTransactionExecutor() {
        return transactionExecutor;
    }

//    @Override
//    public CrudService<TodoEvent> getTodoEventCrudService() {
//        return todoEventCrudService;
//    }
//
//    @Override
//    public CrudService<Template> getTemplateCrudService() {
//        return templateCrudService;
//    }

    @Override
    public CrudService<Category> getCategoryCrudService() {
        return categoryCrudService;
    }

    @Override
    public CrudService<TimeUnit> getTimeUnitCrudService() {
        return timeUnitCrudService;
    }

    @Override
    public ImportService getImportService() {
        return importService;
    }

    @Override
    public ExportService getExportService() {
        return exportService;
    }

//    @Override
//    public TodoEventValidator getTodoEventValidator() {
//        return todoEventValidator;
//    }
//
//    @Override
//    public TemplateValidator getTemplateValidator() {
//        return templateValidator;
//    }

    @Override
    public CategoryValidator getCategoryValidator() {
        return categoryValidator;
    }

    @Override
    public TimeUnitValidator getTimeUnitValidator() {
        return timeUnitValidator;
    }

    @Override
    public TodoEventsServiceFacade getTodoEventsServiceFacade() {
        return todoEventsServiceFacade;
    }
}
