package cz.muni.fi.pv168.project.wiring;

import cz.muni.fi.pv168.project.business.service.crud.CategoryCrudService;
import cz.muni.fi.pv168.project.business.service.crud.CrudService;
import cz.muni.fi.pv168.project.business.service.crud.TimeUnitCrudService;
import cz.muni.fi.pv168.project.business.service.export.*;
import cz.muni.fi.pv168.project.business.service.validation.CategoryValidator;
import cz.muni.fi.pv168.project.business.service.validation.TimeUnitValidator;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.TimeUnit;
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

    private final CategorySqlRepository categories;
    private final TimeUnitSqlRepository timeUnits;
    private final DatabaseManager databaseManager;
    private final TransactionExecutor transactionExecutor;
    private final CrudService<Category> categoryCrudService;
    private final CrudService<TimeUnit> timeUnitCrudService;
    private final ImportService importService = null; // TODO: remove null
    private final ExportService exportService = null; // TODO: remove null
    private final CategoryValidator categoryValidator;
    private final TimeUnitValidator timeUnitValidator;

    public CommonDependencyProvider(DatabaseManager databaseManager) {
        categoryValidator = new CategoryValidator();
        timeUnitValidator = new TimeUnitValidator();

        this.databaseManager = databaseManager;
        var transactionManager = new TransactionManagerImpl(databaseManager);
        this.transactionExecutor = new TransactionExecutorImpl(transactionManager::beginTransaction);
        var transactionConnectionSupplier = new TransactionConnectionSupplier(transactionManager, databaseManager);

        var categoryMapper = new CategoryMapper();
        var categoryDao = new CategoryDao(transactionConnectionSupplier);

        var timeUnitMapper = new TimeUnitMapper();
        var timeUnitDao = new TimeUnitDao(transactionConnectionSupplier);

        this.categories = new CategorySqlRepository(
                categoryDao,
                categoryMapper
        );
        this.timeUnits = new TimeUnitSqlRepository(
                timeUnitDao,
                timeUnitMapper
        );
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
    public CategoryValidator getCategoryValidator() {
        return categoryValidator;
    }

    @Override
    public TimeUnitValidator getTimeUnitValidator() {
        return timeUnitValidator;
    }
}
