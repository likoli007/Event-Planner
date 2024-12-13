package cz.muni.fi.pv168.project.storage.sql.dao;

import cz.muni.fi.pv168.project.storage.sql.db.ConnectionHandler;
import cz.muni.fi.pv168.project.storage.sql.entity.TodoEventEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Supplier;

public class TodoEventDao implements DataAccessObject<TodoEventEntity> {
    private final Supplier<ConnectionHandler> connections;

    public TodoEventDao(Supplier<ConnectionHandler> connections) {
        this.connections = connections;
    }

    @Override
    public TodoEventEntity create(TodoEventEntity entity) {


        var sql = "INSERT INTO TodoEvent (id, name, details, start, timeUnit, timeUnitAmount, done) VALUES (?, ?, ?, ?, ?, ?,?);";
        var categorySQL = "INSERT INTO TodoEvent_Category (todoEvent_id, category_id) VALUES (?, ?);";
        var connection = connections.get();
        try (

                var statement = connection.use().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                var categoryStatement = connection.use().prepareStatement(categorySQL);
        ) {
            connection.use().setAutoCommit(false);
            System.out.println(entity.timeUnitAmount());
            statement.setString(1, String.valueOf(entity.id()));
            statement.setString(2, entity.name());
            statement.setString(3, entity.details());
            statement.setTimestamp(4, Timestamp.valueOf(entity.startTime()));
            statement.setString(5, String.valueOf(entity.timeUnitId()));
            statement.setInt(6, entity.timeUnitAmount());
            statement.setBoolean(7, entity.done());
            statement.executeUpdate();

            for (int i = 0; i < entity.categoryIds().size(); i++) {
                categoryStatement.setString(1, entity.id().toString());
                categoryStatement.setString(2, entity.categoryIds().get(i).toString());
                categoryStatement.executeUpdate();
            }

            try (ResultSet keyResultSet = statement.getGeneratedKeys()) {
                UUID TodoEventId;

                if (keyResultSet.next()) {
                    TodoEventId = UUID.fromString(keyResultSet.getString(1));
                } else {
                    throw new DataStorageException("Failed to fetch generated key for: " + entity);
                }

                if (keyResultSet.next()) {
                    throw new DataStorageException("Multiple keys returned for: " + entity);
                }
                connection.use().commit();
                return findById(TodoEventId).orElseThrow();
            }
        } catch (SQLException ex) {
            // Rollback if an error occurs
            try {
                connection.use().rollback();
                System.out.println("Transaction rolled back.");
                throw new DataStorageException("Failed to store: " + entity, ex);
            } catch (SQLException rollbackEx) {
                throw new DataStorageException("Failed to rollback: " + entity, rollbackEx);
            }
        } finally {
            try {
                if (connection != null) {
                    connection.use().setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    @Override
    public Collection<TodoEventEntity> findAll() {
        var sql = "SELECT * FROM TodoEvent;";
        var categorySQL = "SELECT * FROM TodoEvent_Category WHERE todoEvent_id=?;";
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql);
                var categoryStatement = connection.use().prepareStatement(categorySQL)
        ) {
            List<TodoEventEntity> TodoEvents = new ArrayList<>();
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ArrayList<UUID> categoryIds = new ArrayList<>();
                    categoryStatement.setString(1, resultSet.getString("id"));
                    ResultSet categoryResultSet = categoryStatement.executeQuery();
                    while (categoryResultSet.next()) {
                        categoryIds.add(UUID.fromString(categoryResultSet.getString(2)));
                    }
                    TodoEventEntity TodoEvent = new TodoEventEntity(UUID.fromString(resultSet.getString("id")),
                            resultSet.getString("name"),
                            resultSet.getString("details"),
                            resultSet.getTimestamp("start").toLocalDateTime(),
                            UUID.fromString(resultSet.getString("timeUnit")),
                            resultSet.getInt("timeUnitAmount"),
                            categoryIds,
                            resultSet.getBoolean("done"));

                    TodoEvents.add(TodoEvent);
                }
            }

            return TodoEvents;
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to load all TodoEvents", ex);
        }
    }

    @Override
    public Optional<TodoEventEntity> findById(UUID id) {
        var sql = "SELECT id, name, details, start, timeUnit, timeUnitAmount, done FROM TodoEvent WHERE id = ?;";
        var categorySQL = "SELECT * FROM TodoEvent_Category WHERE todoEvent_id = ?;";

        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql);
                var categoryStatement = connection.use().prepareStatement(categorySQL);
        ) {
            statement.setString(1, String.valueOf(id));
            categoryStatement.setString(1, String.valueOf(id));
            try (
                    ResultSet resultSet = statement.executeQuery();
                    ResultSet categoryResultSet = categoryStatement.executeQuery();
            ) {
                if (resultSet.next()) {
                    ArrayList<UUID> categoryList = new ArrayList<>();

                    while (categoryResultSet.next()) {
                        categoryList.add(UUID.fromString(categoryResultSet.getString(2)));
                    }

                    return Optional.of(new TodoEventEntity(
                            UUID.fromString(resultSet.getString("id")),
                            resultSet.getString("name"),
                            resultSet.getString("details"),
                            resultSet.getTimestamp("start").toLocalDateTime(),
                            UUID.fromString(resultSet.getString("timeUnit")),
                            resultSet.getInt("timeUnitAmount"),
                            categoryList,
                            resultSet.getBoolean("done")
                    ));
                }
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
        var categoryResetSQL = """
                DELETE FROM TodoEvent_Category
                WHERE todoEvent_id = ?;
                """;
        var categorySQL = "INSERT INTO TodoEvent_Category (todoEvent_id, category_id) VALUES (?, ?)";
        var connection = connections.get();
        try (
                var statement = connection.use().prepareStatement(sql);
                var categoryStatement = connection.use().prepareStatement(categorySQL);
                var categoryDeleteStatement = connection.use().prepareStatement(categoryResetSQL);
        ) {
            connection.use().setAutoCommit(false);
            statement.setString(1, entity.name());
            statement.setString(2, entity.details());
            statement.setTimestamp(3, Timestamp.valueOf(entity.startTime()));
            statement.setString(4, entity.timeUnitId().toString());
            statement.setInt(5, entity.timeUnitAmount());
            statement.setBoolean(6, entity.done());
            statement.setString(7, entity.id().toString());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataStorageException("TodoEvent not found, id: " + entity.id());
            }
            if (rowsUpdated > 1) {
                throw new DataStorageException("More than 1 time unit (rows=%d) has been updated: %s"
                        .formatted(rowsUpdated, entity));
            }
            if (rowsUpdated == 1) {
                categoryDeleteStatement.setString(1, entity.id().toString());
                categoryDeleteStatement.executeUpdate();
                for (int i = 0; i < entity.categoryIds().size(); i++) {
                    categoryStatement.setString(1, entity.id().toString());
                    categoryStatement.setString(2, entity.categoryIds().get(i).toString());
                    categoryStatement.executeUpdate();
                }
                connection.use().commit();
            }
            return entity;
        } catch (SQLException ex) {
            try {
                connection.use().rollback();
                throw new DataStorageException("Failed to update TodoEvent: \n" + entity, ex);
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
                throw new DataStorageException("Failed to rollback: " + entity, rollbackEx);
            }
        } finally {
            try {
                connection.use().setAutoCommit(true);
                connection.close();
            } catch (SQLException closeEx) {
                throw new DataStorageException("Failed to revert autocommit.", closeEx);
            }
        }
    }


    @Override
    public void deleteById(UUID id) {
        var sql = """
                DELETE FROM TodoEvent
                WHERE id = ?;
                """;

        var categoryResetSQL = """
                DELETE FROM TodoEvent_Category
                WHERE todoEvent_id = ?;
                """;
        var connection = connections.get();
        try (
                var statement = connection.use().prepareStatement(sql);
                var categoryResetStatement = connection.use().prepareStatement(categoryResetSQL);
        ) {
            connection.use().setAutoCommit(false);
            statement.setString(1, String.valueOf(id));
            categoryResetStatement.setString(1, String.valueOf(id));

            categoryResetStatement.executeUpdate();
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
        var categoryResetSQL = "DELETE FROM TodoEvent_Category;";
        var connection = connections.get();
        try (
                var statement = connection.use().prepareStatement(sql);
                var categoryResetStatement = connection.use().prepareStatement(categoryResetSQL);
        ) {
            connection.use().setAutoCommit(false);
            categoryResetStatement.executeUpdate();
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
