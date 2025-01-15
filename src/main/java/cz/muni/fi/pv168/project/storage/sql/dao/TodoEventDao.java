package cz.muni.fi.pv168.project.storage.sql.dao;

import cz.muni.fi.pv168.project.storage.sql.db.ConnectionHandler;
import cz.muni.fi.pv168.project.storage.sql.entity.TemplateEntity;
import cz.muni.fi.pv168.project.storage.sql.entity.TodoEventEntity;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class TodoEventDao implements DataAccessObject<TodoEventEntity> {
    private final Supplier<ConnectionHandler> connections;

    public TodoEventDao(Supplier<ConnectionHandler> connections) {
        this.connections = connections;
    }

    @Override
    public TodoEventEntity create(TodoEventEntity entity) {
        var sql = "INSERT INTO TodoEvent (id, name, details, start, timeUnit, timeUnitAmount, done) VALUES (?, ?, ?, ?, ?, ?,?);";
        AtomicReference<TodoEventEntity> todoEventEntity = new AtomicReference<>();
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setString(1, String.valueOf(entity.id()));
            statement.setString(2, entity.name());
            statement.setString(3, entity.details());
            statement.setTime(4, Time.valueOf(entity.startTime().toLocalTime()));
            statement.setString(5, String.valueOf(entity.timeUnitId()));
            statement.setInt(6, entity.timeUnitAmount());
            statement.setString(7, String.valueOf(entity.done()));
            statement.executeUpdate();

            try (ResultSet keyResultSet = statement.getGeneratedKeys()) {
                UUID todoEventId;

                if (keyResultSet.next()) {
                    todoEventId = UUID.fromString(keyResultSet.getString(1));
                } else {
                    throw new DataStorageException("Failed to fetch generated key for: " + entity);
                }

                if (keyResultSet.next()) {
                    throw new DataStorageException("Multiple keys returned for: " + entity);
                }
                todoEventEntity.set(findById(todoEventId).orElseThrow());
            }
        } catch (SQLException ex) {
            System.out.println("Transaction rolled back.");
            throw new DataStorageException("Failed to store: " + entity, ex);
        }

        return todoEventEntity.get();
    }

    @Override
    public Collection<TodoEventEntity> findAll() {
        var sql = "SELECT * FROM TodoEvent;";

        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql);
        ) {
            List<TodoEventEntity> TodoEvents = new ArrayList<>();
            var resultSet = statement.executeQuery();
                while (resultSet.next()) {

                    TodoEventEntity TodoEvent = new TodoEventEntity(UUID.fromString(resultSet.getString("id")),
                            resultSet.getString("name"),
                            resultSet.getString("details"),
                            resultSet.getTimestamp("start").toLocalDateTime(),
                            UUID.fromString(resultSet.getString("timeUnit")),
                            resultSet.getInt("timeUnitAmount"),
                            new ArrayList<>(),
                            resultSet.getString("done"));

                    TodoEvents.add(TodoEvent);
                }
            return TodoEvents;
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to load all TodoEvents", ex);
        }
    }

    @Override
    public Optional<TodoEventEntity> findById(UUID id) {
        var sql = "SELECT id, name, details, start, timeUnit, timeUnitAmount, done FROM TodoEvent WHERE id = ?;";

        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql);
        ) {
            statement.setString(1, String.valueOf(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new TodoEventEntity(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("name"),
                        resultSet.getString("details"),
                        resultSet.getTimestamp("start").toLocalDateTime(),
                        UUID.fromString(resultSet.getString("timeUnit")),
                        resultSet.getInt("timeUnitAmount"),
                        new ArrayList<>(),
                        resultSet.getString("done")
                ));
            }
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to retrieve TodoEvent with id: " + id, ex);
        }
        return Optional.empty();
    }
    @Override
    public TodoEventEntity update(TodoEventEntity entity) {
        var sql = """
                UPDATE TodoEvent
                SET name = ?,
                    details = ?,
                    start = ?,
                    timeUnit = ?,
                    timeUnitAmount = ?,
                    done = ?
                WHERE id = ?;
                """;

        AtomicReference<TodoEventEntity> todoEventEntity = new AtomicReference<>();


        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql);
        ) {
            statement.setString(1, entity.name());
            statement.setString(2, entity.details());
            statement.setTime(3, Time.valueOf(entity.startTime().toLocalTime()));
            statement.setString(4, entity.timeUnitId().toString());
            statement.setInt(5, entity.timeUnitAmount());
            statement.setString(6, entity.id().toString());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 1) {
                throw new DataStorageException("More than 1 time unit (rows=%d) has been updated: %s"
                        .formatted(rowsUpdated, entity));
            }

            todoEventEntity.set(findById(entity.id()).orElseThrow());
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to update template: " + entity, ex);
        }

        return todoEventEntity.get();
    }


    @Override
    public void deleteById(UUID id) {
        var sql = """
                DELETE FROM TodoEvent
                WHERE id = ?;
                """;

        var connection = connections.get();
        try (
                var statement = connection.use().prepareStatement(sql);
        ) {
            connection.use().setAutoCommit(false);
            statement.setString(1, String.valueOf(id));
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DataStorageException("TodoEvent not found, id: " + id);
            }
            if (rowsUpdated > 1) {
                throw new DataStorageException("More then 1 TodoEvent (rows=%d) has been deleted: %s"
                        .formatted(rowsUpdated, id));
            }

            connection.use().commit();
        } catch (SQLException ex) {
            try {
                connection.use().rollback();
                throw new DataStorageException("Failed to delete TodoEvent, id: " + id, ex);
            } catch (SQLException rollbackEx) {
                throw new DataStorageException("Failed to rollback: " + id, rollbackEx);
            }
        } finally {
            try {
                connection.use().setAutoCommit(true);
                connection.close();
            } catch (SQLException closeEx) {
                throw new DataStorageException("Failed to revert to autocommit.", closeEx);
            }
        }
    }

    @Override
    public void deleteAll() {
        var sql = "DELETE FROM TodoEvent;";

        var connection = connections.get();
        try (
                var statement = connection.use().prepareStatement(sql);
        ) {
            connection.use().setAutoCommit(false);
            statement.executeUpdate();
            connection.use().commit();
        } catch (SQLException ex) {
            try {
                connection.use().rollback();
                throw new DataStorageException("Failed to delete all TodoEvents", ex);
            } catch (SQLException rollbackEx) {
                throw new DataStorageException("Failed to rollback deletion of all TodoEvents", rollbackEx);
            }
        } finally {
            try {
                connection.use().setAutoCommit(true);
                connection.close();
            } catch (SQLException closeEx) {
                throw new DataStorageException("Failed to revert to autocommit.", closeEx);
            }
        }
    }

}
