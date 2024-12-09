package cz.muni.fi.pv168.project.storage.sql.db.actions;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.storage.sql.CategorySqlRepository;
import cz.muni.fi.pv168.project.storage.sql.TimeUnitSqlRepository;
import cz.muni.fi.pv168.project.storage.sql.dao.CategoryDao;
import cz.muni.fi.pv168.project.storage.sql.dao.TimeUnitDao;
import cz.muni.fi.pv168.project.storage.sql.db.DatabaseManager;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionConnectionSupplier;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionExecutorImpl;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionManagerImpl;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.CategoryMapper;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.TimeUnitMapper;

public class InsertTestData {
    public static void main(String[] args) {
        // TODO replace this with wiring
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

        testDataGenerator.createCategories().forEach(categoryRepository::create);
        testDataGenerator.createTimeUnits().forEach(timeUnitRepository::create);
        // testDataGenerator.createTemplates().forEach(templateRepository::create);
        // testDataGenerator.createTodoEvents().forEach(todoEventRepository::create);

        System.out.println("Test data inserted...");
    }

}
