package cz.muni.fi.pv168.project.service.crud;

import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Crud operations for the {@link TodoEvent} entity.
 */
public class TodoEventCrudService implements CrudService<TodoEvent> {
    private final Repository<TodoEvent> todoEventRepository;

    public TodoEventCrudService(Repository<TodoEvent> todoEventRepository) {
        this.todoEventRepository = todoEventRepository;
    }

    @Override
    public List<TodoEvent> findAll() {
        return todoEventRepository.findAll();
    }

    @Override
    public Optional<TodoEvent> findById(UUID id) {
        return todoEventRepository.findById(id);
    }

    @Override
    public Optional<TodoEvent> findDuplicate(TodoEvent entity) {
        return todoEventRepository.findDuplicate(entity);
    }

    @Override
    public void create(TodoEvent newEntity) {
        todoEventRepository.create(newEntity);
    }

    @Override
    public void update(TodoEvent entity) {
        todoEventRepository.update(entity);
    }

    @Override
    public void deleteById(UUID id) {
        todoEventRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        todoEventRepository.deleteAll();
    }
}
