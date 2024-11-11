package cz.muni.fi.pv168.project.business.facades;

import cz.muni.fi.pv168.project.business.filter.Filter;
import cz.muni.fi.pv168.project.model.TodoEvent;

import java.util.List;
import java.util.UUID;

public interface TodoEventsServiceFacade {
        void create(TodoEvent entity);
        List<TodoEvent> findAll();
        void update(TodoEvent entity);
        void deleteById(UUID id);
        List<TodoEvent> getEventsByFilter(Filter<TodoEvent> filter);
}
