package cz.muni.fi.pv168.project.storage.sql.dao;

import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.storage.sql.db.ConnectionHandler;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class TodoEventDao implements DataAccessObject<TodoEvent> {
    private final Supplier<ConnectionHandler> connections;

    public TodoEventDao(Supplier<ConnectionHandler> connections) {
        this.connections = connections;
    }

    @Override
    public TodoEvent create(TodoEvent entity) {
        return null;
    }

    @Override
    public Collection<TodoEvent> findAll() {
        return List.of();
    }

    @Override
    public Optional<TodoEvent> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public TodoEvent update(TodoEvent entity) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteAll() {

    }
}
