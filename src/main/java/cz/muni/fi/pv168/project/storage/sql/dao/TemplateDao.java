package cz.muni.fi.pv168.project.storage.sql.dao;

import cz.muni.fi.pv168.project.storage.sql.db.ConnectionHandler;
import cz.muni.fi.pv168.project.storage.sql.entity.TemplateEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class TemplateDao implements DataAccessObject<TemplateEntity> {
    private final Supplier<ConnectionHandler> connections;

    public TemplateDao(Supplier<ConnectionHandler> connections) {
        this.connections = connections;
    }

    @Override
    public TemplateEntity create(TemplateEntity entity) {

        var sql = "INSERT INTO Template (id, name, details, startTime, timeUnit, timeUnitAmount) VALUES (?, ?, ?, ?, ?, ?);";

        try (

                var connection = connections.get();
                var statement =connection.use().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
//                connection.use().setAutoCommit(false);
            statement.setString(1, String.valueOf(entity.id()));
            statement.setString(2, entity.name());
            statement.setString(3, entity.details());
            statement.setTime(4, Time.valueOf(entity.startTime()));
            statement.setString(5, String.valueOf(entity.timeUnitId()));
            statement.setInt(6, entity.timeUnitAmount());

            statement.executeUpdate();

            try (ResultSet keyResultSet = statement.getGeneratedKeys()) {
                UUID templateId;

                if (keyResultSet.next()) {
                    templateId = UUID.fromString(keyResultSet.getString(1));
                } else {
                    throw new DataStorageException("Failed to fetch generated key for: " + entity);
                }

                if (keyResultSet.next()) {
                    throw new DataStorageException("Multiple keys returned for: " + entity);
                }

                return findById(templateId).orElseThrow();
            }
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to store: " + entity, ex);
        }

    }

    @Override
    public Collection<TemplateEntity> findAll() {
        return List.of();
    }

    @Override
    public Optional<TemplateEntity> findById(UUID id) {
//        var sql = "SELECT id, name, details, startTime, timeUnit, timeUnitAmount FROM Template WHERE id = ?;";
//
//        try (
//                var connection = connections.get();
//                var statement = connection.use().prepareStatement(sql)
//        ) {
//            statement.setString(1, String.valueOf(id));
//
//            try (ResultSet resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    return Optional.of(new TemplateEntity(
//                            UUID.fromString(resultSet.getString("id")),
//                            resultSet.getString("name"),
//                            resultSet.getString("details"),
//                            resultSet.getTime("startTime").toLocalTime(),
//                            UUID.fromString(resultSet.getString("timeUnit")),
//                            resultSet.getInt("timeUnitAmount")
//                    ));
//                }
//            }
//        } catch (SQLException ex) {
//            throw new DataStorageException("Failed to retrieve template with id: " + templateId, ex);
//        }
        return Optional.empty();

    }

    @Override
    public TemplateEntity update(TemplateEntity entity) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteAll() {

    }
}
