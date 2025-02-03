package cz.muni.fi.pv168.project.wiring;

import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacade;
import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacadeImpl;
import cz.muni.fi.pv168.project.business.service.crud.*;
import cz.muni.fi.pv168.project.business.service.export.*;
import cz.muni.fi.pv168.project.business.service.validation.CategoryValidator;
import cz.muni.fi.pv168.project.business.service.validation.TemplateValidator;
import cz.muni.fi.pv168.project.business.service.validation.TimeUnitValidator;
import cz.muni.fi.pv168.project.business.service.validation.TodoEventValidator;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.storage.sql.*;
import cz.muni.fi.pv168.project.storage.sql.dao.*;
import cz.muni.fi.pv168.project.storage.sql.db.*;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.CategoryMapper;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.TemplateMapper;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.TimeUnitMapper;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.TodoEventMapper;

import java.util.List;

/**
 * Common dependency provider for both production and test environment.
 */
public class CommonDependencyProvider implements DependencyProvider {

    private final TodoEventsSqlRepository todoEvents;
    private final TemplateSqlRepository templates;
    private final CategorySqlRepository categories;
    private final TimeUnitSqlRepository timeUnits;
    private final DatabaseManager databaseManager;
    private final TransactionExecutor transactionExecutor;
    private final TodoEventsServiceFacade todoEventsServiceFacade;
    private final CrudService<Template> templateCrudService;
    private final CrudService<Category> categoryCrudService;
    private final CrudService<TimeUnit> timeUnitCrudService;
    private final ImportService importService;
    private final ExportService exportService;
    private final TodoEventValidator todoEventValidator;
    private final TemplateValidator templateValidator;
    private final CategoryValidator categoryValidator;
    private final TimeUnitValidator timeUnitValidator;

    public CommonDependencyProvider(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        var transactionManager = new TransactionManagerImpl(databaseManager);
        this.transactionExecutor = new TransactionExecutorImpl(transactionManager::beginTransaction);
        var transactionConnectionSupplier = new TransactionConnectionSupplier(transactionManager, databaseManager);

        todoEventValidator = new TodoEventValidator();
        templateValidator = new TemplateValidator();
        categoryValidator = new CategoryValidator();
        timeUnitValidator = new TimeUnitValidator();

        var todoEventDao = new TodoEventDao(transactionConnectionSupplier);
        var templateDao = new TemplateDao(transactionConnectionSupplier);
        var categoryDao = new CategoryDao(transactionConnectionSupplier);
        var timeUnitDao = new TimeUnitDao(transactionConnectionSupplier);

        var templateCategoryDao = new TemplateCategoryDao(transactionConnectionSupplier);
        var todoEventCategoryDao = new TodoEventCategoryDao(transactionConnectionSupplier);

        var categoryMapper = new CategoryMapper();
        this.categories = new CategorySqlRepository(categoryDao, categoryMapper);

        var timeUnitMapper = new TimeUnitMapper();
        this.timeUnits = new TimeUnitSqlRepository(timeUnitDao, timeUnitMapper);

        var todoEventMapper = new TodoEventMapper(timeUnits, categories);
        var templateMapper = new TemplateMapper(timeUnits, categories);

        this.todoEvents = new TodoEventsSqlRepository(todoEventDao, todoEventCategoryDao, todoEventMapper);
        this.templates = new TemplateSqlRepository(templateDao, templateCategoryDao, templateMapper);

        var todoEventCrudService = new TodoEventCrudService(todoEvents);
        var todoEventsServiceFacade = new TodoEventsServiceFacadeImpl(todoEventCrudService);
        var templateCrudService = new TemplateCrudService(templates);
        var categoryCrudService = new CategoryCrudService(categories);
        var timeUnitCrudService = new TimeUnitCrudService(timeUnits);

        this.todoEventsServiceFacade = new TodoEventsServiceFacadeImpl(new TransactionalCrudService<>(transactionExecutor, todoEventCrudService));
        this.templateCrudService = new TransactionalCrudService<>(transactionExecutor, templateCrudService);
        this.categoryCrudService = new TransactionalCrudService<>(transactionExecutor, categoryCrudService);
        this.timeUnitCrudService = new TransactionalCrudService<>(transactionExecutor, timeUnitCrudService);

        this.exportService = new GenericExportService(
                this.categoryCrudService,
                this.timeUnitCrudService,
                this.templateCrudService,
                this.todoEventsServiceFacade,
                List.of(new JSONFileExporter())
        );

        var genericImportService = new GenericImportService(
                categoryCrudService,
                timeUnitCrudService,
                templateCrudService,
                todoEventsServiceFacade,
                List.of(new JSONFileImporter())
        );
        this.importService = new TransactionalImportService(genericImportService, transactionExecutor);
    }

    @Override
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public TodoEventsSqlRepository getTodoEventRepository() {
        return todoEvents;
    }

    @Override
    public TemplateSqlRepository getTemplateRepository() {
        return templates;
    }

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

    @Override
    public TodoEventsServiceFacade getTodoEventsServiceFacade() {
        return todoEventsServiceFacade;
    }

    @Override
    public CrudService<Template> getTemplateCrudService() {
        return templateCrudService;
    }

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

    @Override
    public TodoEventValidator getTodoEventValidator() {
        return todoEventValidator;
    }

    @Override
    public TemplateValidator getTemplateValidator() {
        return templateValidator;
    }

    @Override
    public CategoryValidator getCategoryValidator() {
        return categoryValidator;
    }

    @Override
    public TimeUnitValidator getTimeUnitValidator() {
        return timeUnitValidator;
    }
}
