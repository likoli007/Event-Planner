package cz.muni.fi.pv168.project.service.crud;

import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.model.TodoEventFilter;
import cz.muni.fi.pv168.project.repository.Repository;

import java.util.List;
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
    // this cannot be here since the Interface thing
    public List<TodoEvent> getEventsByFilter(TodoEventFilter filter){
        return todoEventRepository.findAll().stream().filter(

                event -> filter.getDone() == null || event.isDone() == filter.getDone()
        ).filter(
                event-> filter.getFromDate() == null ||  event.getStart().toLocalDate().isBefore(filter.getFromDate())
                ).filter(
                event-> filter.getToDate() == null ||  event.getStart().toLocalDate().isAfter(filter.getToDate())
                )
                .toList();
    }
}
