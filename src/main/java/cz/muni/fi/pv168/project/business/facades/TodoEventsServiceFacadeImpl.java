package cz.muni.fi.pv168.project.business.facades;

import cz.muni.fi.pv168.project.business.filter.Filter;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.business.service.crud.CrudService;

import java.util.List;
import java.util.UUID;

public class TodoEventsServiceFacadeImpl implements TodoEventsServiceFacade {
    private final CrudService<TodoEvent> todoEventCrudService;
    private Filter<TodoEvent> filter;


    public TodoEventsServiceFacadeImpl(CrudService<TodoEvent> todoEventCrudService) {
        this.todoEventCrudService = todoEventCrudService;
    }

    @Override
    public void create(TodoEvent entity) {
        todoEventCrudService.create(entity);
    }

    @Override
    public List<TodoEvent> findAll() {
        return todoEventCrudService.findAll();
    }

    @Override
    public void update(TodoEvent entity) {
        todoEventCrudService.update(entity);
    }

    @Override
    public void deleteById(UUID id) {
        todoEventCrudService.deleteById(id);
    }

    @Override
    public List<TodoEvent> getEventsByFilter(Filter<TodoEvent> filter) {
        this.filter = filter;

        return getFilteredEvents();

    }

    public List<TodoEvent> getFilteredEvents(){
        if(filter != null){
            return todoEventCrudService.findAll().stream().filter(filter::isMatch).toList();
        }
        return findAll();
    }

    @Override
    public CrudService<TodoEvent> getTodoEventCrudService(){
        return this.todoEventCrudService;
    }
}
