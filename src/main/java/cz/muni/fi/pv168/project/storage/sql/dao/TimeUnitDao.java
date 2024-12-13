package cz.muni.fi.pv168.project.storage.sql.dao;

import cz.muni.fi.pv168.project.storage.sql.db.ConnectionHandler;
import cz.muni.fi.pv168.project.storage.sql.entity.TimeUnitEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Supplier;

/**
 * DAO for {@link TimeUnitEntity} entity.
 */
public final class TimeUnitDao implements DataAccessObject<TimeUnitEntity> {
    private final Supplier<ConnectionHandler> connections;

    public TimeUnitDao(Supplier<ConnectionHandler> connections) {
        this.connections = connections;
    }

    @Override
    public TimeUnitEntity create(TimeUnitEntity newTimeUnit) {
        var sql = "INSERT INTO TimeUnit (id, name, shortcut, minutes, isSystemDefined) VALUES (?, ?, ?, ?,?);";

        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, String.valueOf(newTimeUnit.id()));
            statement.setString(2, newTimeUnit.name());
            statement.setString(3, newTimeUnit.shortcut());
            statement.setInt(4, newTimeUnit.minutes());
            statement.setBoolean(5, newTimeUnit.isSystemDefined());
            statement.executeUpdate();

            try (ResultSet keyResultSet = statement.getGeneratedKeys()) {
                UUID timeUnitId;

                if (keyResultSet.next()) {
                    timeUnitId = UUID.fromString(keyResultSet.getString(1));
                } else {
                    throw new DataStorageException("Failed to fetch generated key for: " + newTimeUnit);
                }
                if (keyResultSet.next()) {
                    throw new DataStorageException("Multiple keys returned for: " + newTimeUnit);
                }

                return findById(timeUnitId).orElseThrow();
            }
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to store: " + newTimeUnit, ex);
        }
    }

    @Override
    public Collection<TimeUnitEntity> findAll() {
        var sql = """
                SELECT id,
                       name,
                       shortcut,
                       minutes,
                      isSystemDefined
                FROM TimeUnit;
                """;
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql)
        ) {
            List<TimeUnitEntity> timeUnits = new ArrayList<>();
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var timeUnit = timeUnitFromResultSet(resultSet);
                    timeUnits.add(timeUnit);
                }
            }

            return timeUnits;
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to load all time units", ex);
        }
    }

    @Override
    public Optional<TimeUnitEntity> findById(UUID id) {
        var sql = """
                SELECT id,
                       name,
                       shortcut,
                       minutes,
                       isSystemDefined
                FROM TimeUnit
                WHERE id = ?;
                """;
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql)
        ) {
            statement.setString(1, String.valueOf(id));
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(timeUnitFromResultSet(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to load time unit by id: " + id, ex);
        }
    }

    @Override
    public TimeUnitEntity update(TimeUnitEntity entity) {
        var sql = """
                UPDATE TimeUnit
                SET name = ?,
                    shortcut = ?,
                    minutes = ?
                WHERE id = ?;
                """;
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql)
        ) {
            statement.setString(1, entity.name());
            statement.setString(2, entity.shortcut());
            statement.setInt(3, entity.minutes());
            statement.setString(4, String.valueOf(entity.id()));
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataStorageException("Time unit not found, id: " + entity.id());
            }
            if (rowsUpdated > 1) {
                throw new DataStorageException("More then 1 time unit (rows=%d) has been updated: %s"
                        .formatted(rowsUpdated, entity));
            }
            return entity;
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to update time unit: " + entity, ex);
        }
    }

    @Override
    public void deleteById(UUID id) {
        var sql = """
                DELETE FROM TimeUnit
                WHERE id = ? AND isSystemDefined = FALSE;
                """;
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql)
        ) {
            statement.setString(1, String.valueOf(id));
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataStorageException("Time unit not found or is System defined, id: " + id);
            }
            if (rowsUpdated > 1) {
                throw new DataStorageException("More then 1 time unit (rows=%d) has been deleted: %s"
                        .formatted(rowsUpdated, id));
            }
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to delete time unit, id: " + id, ex);
        }
    }

    @Override
    public void deleteAll() {
        var sql = "DELETE FROM TimeUnit WHERE isSystemDefined = FALSE;";
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql)
        ) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to delete all time units", ex);
        }
    }

    private static TimeUnitEntity timeUnitFromResultSet(ResultSet resultSet) throws SQLException {
        return new TimeUnitEntity(
                UUID.fromString(resultSet.getString("id")),
                resultSet.getString("name"),
                resultSet.getString("shortcut"),
                resultSet.getInt("minutes"),
                resultSet.getBoolean("isSystemDefined")
        );
    }
}

