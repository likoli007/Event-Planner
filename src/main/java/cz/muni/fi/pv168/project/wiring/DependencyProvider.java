package cz.muni.fi.pv168.project.wiring;

import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacade;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.storage.sql.CategorySqlRepository;
import cz.muni.fi.pv168.project.storage.sql.TimeUnitSqlRepository;
import cz.muni.fi.pv168.project.business.service.crud.CrudService;
import cz.muni.fi.pv168.project.business.service.export.ExportService;
import cz.muni.fi.pv168.project.business.service.export.ImportService;
import cz.muni.fi.pv168.project.business.service.validation.Validator;
import cz.muni.fi.pv168.project.storage.sql.db.DatabaseManager;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionExecutor;

// TODO: add TodoEvent & Template
public interface DependencyProvider {

    DatabaseManager getDatabaseManager();

//    TodoEventSqlRepository getTodoEventRepository();
//
//    TemplateSqlRepository getTemplateRepository();

    CategorySqlRepository getCategoryRepository();

    TimeUnitSqlRepository getTimeUnitRepository();

    TransactionExecutor getTransactionExecutor();

//    CrudService<TodoEvent> getTodoEventCrudService();

//    CrudService<Template> getTemplateCrudService();

    CrudService<Category> getCategoryCrudService();

    CrudService<TimeUnit> getTimeUnitCrudService();

    ImportService getImportService();

    ExportService getExportService();

//    Validator<TodoEvent> getTodoEventValidator();
//
//    Validator<Template> getTemplateValidator();

    Validator<Category> getCategoryValidator();

    Validator<TimeUnit> getTimeUnitValidator();

    TodoEventsServiceFacade getTodoEventsServiceFacade();
}