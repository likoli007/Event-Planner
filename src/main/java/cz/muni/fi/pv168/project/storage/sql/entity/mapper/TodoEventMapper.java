package cz.muni.fi.pv168.project.storage.sql.entity.mapper;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.storage.sql.CategorySqlRepository;
import cz.muni.fi.pv168.project.storage.sql.TimeUnitSqlRepository;
import cz.muni.fi.pv168.project.storage.sql.entity.TodoEventEntity;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class TodoEventMapper implements EntityMapper<TodoEventEntity, TodoEvent> {

    TimeUnitSqlRepository timeUnitSqlRepository;
    CategorySqlRepository categorySqlRepository;


    public TodoEventMapper(TimeUnitSqlRepository timeUnitRepository, CategorySqlRepository categoryRepository) {
        this.timeUnitSqlRepository = timeUnitRepository;
        this.categorySqlRepository = categoryRepository;
    }


    @Override
    public TodoEvent mapToBusiness(TodoEventEntity todoEventEntity) {
        Optional<TimeUnit> timeUnitResult = timeUnitSqlRepository.findById(todoEventEntity.timeUnitId());
        TimeUnit timeUnit = timeUnitResult.orElse(null);
        //TODO: null check?

        ArrayList<Category> categories = new ArrayList<>();

        for (UUID categoryId : todoEventEntity.categoryIds()) {
            Optional<Category> categoryResult = categorySqlRepository.findById(categoryId);
            Category category = categoryResult.orElse(null);
            categories.add(category);
        }

        return new TodoEvent(
                todoEventEntity.id(),
                todoEventEntity.name(),
                todoEventEntity.details(),
                todoEventEntity.startTime(),
                timeUnit,
                todoEventEntity.timeUnitAmount(),
                categories,
                Objects.equals(todoEventEntity.done(), TodoEventEntity.IS_DONE)
        );
    }

    @Override
    public TodoEventEntity mapEntityToDatabase(TodoEvent entity) {
        String done;
        if (entity.isDone()) {
            done = TodoEventEntity.IS_DONE;
        } else {
            done = TodoEventEntity.IS_NOT_DONE;
        }

        return new TodoEventEntity(
                entity.getId(),
                entity.getName(),
                entity.getDetails(),
                entity.getStart(),
                entity.getInterval().getTimeUnit().getId(),
                entity.getInterval().getAmount(),
                entity.getCategories().stream().map(Category::getId).toList(),
                done
        );
    }
}
