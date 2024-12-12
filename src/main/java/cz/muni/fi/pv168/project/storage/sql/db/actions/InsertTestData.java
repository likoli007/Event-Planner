package cz.muni.fi.pv168.project.storage.sql.db.actions;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.wiring.DependencyProvider;
import cz.muni.fi.pv168.project.wiring.ProductionDependencyProvider;

// TODO: Add TodoEvents and Templates
public class InsertTestData {
    public static void main(String[] args) {
        DependencyProvider provider = new ProductionDependencyProvider();

        var testDataGenerator = new TestDataGenerator();

//        var todoEventRepository = provider.getTodoEventRepository();
//        var templateRepository = provider.getTemplateRepository();
        var categoryRepository = provider.getCategoryRepository();
        var timeUnitRepository = provider.getTimeUnitRepository();

//        testDataGenerator.createTodoEvents().forEach(todoEventRepository::create);
//        testDataGenerator.createTemplates().forEach(templateRepository::create);
        testDataGenerator.createCategories().forEach(categoryRepository::create);
        testDataGenerator.createTimeUnits().forEach(timeUnitRepository::create);

        System.out.println("Test data inserted...");
    }

}
