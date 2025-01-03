package cz.muni.fi.pv168.project.business.facades;

import cz.muni.fi.pv168.project.business.filter.Filter;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.business.service.crud.CrudService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoEventsServiceFacade {
        void create(TodoEvent entity);
        List<TodoEvent> findAll();
        void update(TodoEvent entity);
        void deleteById(UUID id);
        Optional<TodoEvent> findDuplicate(TodoEvent entity);
        List<TodoEvent> getEventsByFilter(Filter<TodoEvent> filter);
        List<TodoEvent> getFilteredEvents();
        CrudService<TodoEvent> getTodoEventCrudService();
}
