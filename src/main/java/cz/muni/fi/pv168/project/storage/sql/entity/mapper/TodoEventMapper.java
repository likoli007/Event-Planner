package cz.muni.fi.pv168.project.storage.sql.entity.mapper;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.storage.sql.CategorySqlRepository;
import cz.muni.fi.pv168.project.storage.sql.TimeUnitSqlRepository;
import cz.muni.fi.pv168.project.storage.sql.entity.TodoEventEntity;

import java.util.ArrayList;
import java.util.Optional;

public class TodoEventMapper implements EntityMapper<TodoEventEntity, TodoEvent> {

    TimeUnitSqlRepository timeUnitSqlRepository;
    CategorySqlRepository categorySqlRepository;


    public TodoEventMapper(TimeUnitSqlRepository timeUnitRepository, CategorySqlRepository categoryRepository) {
        this.timeUnitSqlRepository = timeUnitRepository;
        this.categorySqlRepository = categoryRepository;
    }


    @Override
    public TodoEvent mapToBusiness(TodoEventEntity TodoEventEntity) {
        Optional<TimeUnit> timeUnitResult = timeUnitSqlRepository.findById(TodoEventEntity.timeUnitId());
        TimeUnit timeUnit = timeUnitResult.orElse(null);
        //TODO: null check?

        ArrayList<Category> categories = new ArrayList<>();

        for (int i = 0; i < TodoEventEntity.categoryIds().size(); i++) {
            Optional<Category> categoryResult = categorySqlRepository.findById(TodoEventEntity.categoryIds().get(i));
            Category category = categoryResult.orElse(null);
            categories.add(category);
        }

        return new TodoEvent(
                TodoEventEntity.id(),
                TodoEventEntity.name(),
                TodoEventEntity.details(),
                TodoEventEntity.startTime(),
                timeUnit,
                TodoEventEntity.timeUnitAmount(),
                categories,
                TodoEventEntity.done()
        );
    }

    @Override
    public TodoEventEntity mapEntityToDatabase(TodoEvent entity) {
        return new TodoEventEntity(
                entity.getId(),
                entity.getName(),
                entity.getDetails(),
                entity.getStart(),
                entity.getInterval().getTimeUnit().getId(),
                entity.getInterval().getAmount(),
                entity.getCategories().stream().map(Category::getId).toList(),
                entity.isDone()
        );
    }
}
