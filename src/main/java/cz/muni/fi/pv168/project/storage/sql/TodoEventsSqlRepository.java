package cz.muni.fi.pv168.project.storage.sql;

import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.repository.Repository;
import cz.muni.fi.pv168.project.storage.sql.dao.DataAccessObject;
import cz.muni.fi.pv168.project.storage.sql.dao.DataStorageException;
import cz.muni.fi.pv168.project.storage.sql.entity.TodoEventEntity;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.EntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TodoEventsSqlRepository implements Repository<TodoEvent> {
    private final DataAccessObject<TodoEventEntity> todoEventDao;
    private final EntityMapper<TodoEventEntity, TodoEvent> todoEventMapper;

    public TodoEventsSqlRepository(
            DataAccessObject<TodoEventEntity> TodoEventDao,
            EntityMapper<TodoEventEntity, TodoEvent> TodoEventMapper) {
        this.todoEventDao = TodoEventDao;
        this.todoEventMapper = TodoEventMapper;
    }

    @Override
    public List<TodoEvent> findAll() {
        return todoEventDao
                .findAll()
                .stream()
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
        var updatedTodoEvent = todoEventMapper.mapEntityToDatabase(entity);

        todoEventDao.update(updatedTodoEvent);
    }

    @Override
    public void deleteById(UUID id) {
        todoEventDao.deleteById(id);
    }

    @Override
    public void deleteAll() {
        todoEventDao.deleteAll();
    }

    @Override
    public Optional<TodoEvent> findById(UUID id) {
        return todoEventDao
                .findById(id)
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
