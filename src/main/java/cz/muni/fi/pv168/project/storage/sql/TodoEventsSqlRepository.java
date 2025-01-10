package cz.muni.fi.pv168.project.storage.sql;

import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.repository.Repository;
import cz.muni.fi.pv168.project.storage.sql.dao.DataAccessObject;
import cz.muni.fi.pv168.project.storage.sql.dao.DataStorageException;
import cz.muni.fi.pv168.project.storage.sql.dao.JoinTableDao;
import cz.muni.fi.pv168.project.storage.sql.entity.TodoEventEntity;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.EntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TodoEventsSqlRepository implements Repository<TodoEvent> {
    private final DataAccessObject<TodoEventEntity> todoEventDao;
    private final JoinTableDao<UUID, UUID> todoEventCategoryDao;
    private final EntityMapper<TodoEventEntity, TodoEvent> todoEventMapper;

    public TodoEventsSqlRepository(
            DataAccessObject<TodoEventEntity> TodoEventDao, JoinTableDao<UUID, UUID> TodoEventCategoryDao,
            EntityMapper<TodoEventEntity, TodoEvent> TodoEventMapper) {
        this.todoEventDao = TodoEventDao;
        this.todoEventCategoryDao = TodoEventCategoryDao;
        this.todoEventMapper = TodoEventMapper;
    }

    @Override
    public List<TodoEvent> findAll() {
        var todoEvents = todoEventDao.findAll();

        for (var todoEvent : todoEvents) {
            var categories = todoEventCategoryDao.findByParentId(todoEvent.id());
            todoEvent.categoryIds().addAll(categories);
        }

        return todoEvents.stream()
                .map(todoEventMapper::mapToBusiness)
                .toList();
    }

    @Override
    public TodoEvent create(TodoEvent newTodoEvent) {
        return todoEventMapper.mapToBusiness(todoEventDao.create(todoEventMapper.mapEntityToDatabase(newTodoEvent)));
    }

    @Override
    public void update(TodoEvent entity) {
        todoEventDao.findById(entity.getId())
                .orElseThrow(() -> new DataStorageException("TodoEvent not found, id: " + entity.getId()));
        var dbTodoEvent = todoEventMapper.mapEntityToDatabase(entity);

        var updated = todoEventDao.update(dbTodoEvent);
        todoEventCategoryDao.updateAssociations(entity.getId(), updated.categoryIds());
    }

    @Override
    public void deleteById(UUID id) {
        todoEventCategoryDao.deleteByParentId(id);
        todoEventDao.deleteById(id);
    }

    @Override
    public void deleteAll() {
        todoEventCategoryDao.deleteAll();
        todoEventDao.deleteAll();
    }

    @Override
    public Optional<TodoEvent> findById(UUID id) {
        var todoEvent = todoEventDao.findById(id);
        if (todoEvent.isEmpty()) {
            return Optional.empty();
        }
        var categories = todoEventCategoryDao.findByParentId(id);
        todoEvent.get().categoryIds().addAll(categories);
        return todoEvent
                .map(todoEventMapper::mapToBusiness);
    }

    @Override
    public Optional<TodoEvent> findDuplicate(TodoEvent entity) {
        for (TodoEvent possibleDuplicateEntity : todoEventDao.findAll().stream().map(todoEventMapper::mapToBusiness).toList()) {
            if (possibleDuplicateEntity.isDuplicate(entity)) {
                return Optional.of(possibleDuplicateEntity);
            }
        }

        return Optional.empty();
    }
}
