package cz.muni.fi.pv168.project.storage.sql.dao;

import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.storage.sql.db.ConnectionHandler;
import cz.muni.fi.pv168.project.storage.sql.entity.TemplateEntity;
import cz.muni.fi.pv168.project.storage.sql.entity.TimeUnitEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.*;
import java.util.function.Supplier;

public class TemplateDao implements DataAccessObject<TemplateEntity> {
    private final Supplier<ConnectionHandler> connections;

    public TemplateDao(Supplier<ConnectionHandler> connections) {
        this.connections = connections;
    }

    @Override
    public TemplateEntity create(TemplateEntity entity) {


        var sql = "INSERT INTO Template (id, name, details, startTime, timeUnit, timeUnitAmount) VALUES (?, ?, ?, ?, ?, ?);";
        var categorySQL = "INSERT INTO Template_Category (template_id, category_id) VALUES (?, ?);";
        var connection = connections.get();
        try (

                var statement = connection.use().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                var categoryStatement = connection.use().prepareStatement(categorySQL);
        ) {
            connection.use().setAutoCommit(false);

            statement.setString(1, String.valueOf(entity.id()));
            statement.setString(2, entity.name());
            statement.setString(3, entity.details());
            statement.setTime(4, Time.valueOf(entity.startTime()));
            statement.setString(5, String.valueOf(entity.timeUnitId()));
            statement.setInt(6, entity.timeUnitAmount());
            statement.executeUpdate();

            for (int i = 0; i < entity.categoryIds().size(); i++) {
                categoryStatement.setString(1, entity.id().toString());
                categoryStatement.setString(2, entity.categoryIds().get(i).toString());
                categoryStatement.executeUpdate();
            }

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
                connection.use().commit();
                return findById(templateId).orElseThrow();
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
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    @Override
    public Collection<TemplateEntity> findAll() {
        var sql = "SELECT * FROM Template;";
        var categorySQL = "SELECT * FROM Template_Category WHERE template_id=?;";
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql);
                var categoryStatement = connection.use().prepareStatement(categorySQL)
        ) {
            List<TemplateEntity> templates = new ArrayList<>();
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ArrayList<UUID> categoryIds = new ArrayList<>();
                    categoryStatement.setString(1, resultSet.getString("id"));
                    ResultSet categoryResultSet = categoryStatement.executeQuery();
                    while (categoryResultSet.next()) {
                        categoryIds.add(UUID.fromString(categoryResultSet.getString(2)));
                    }
                    System.out.println(categoryIds);
                    TemplateEntity template = new TemplateEntity(UUID.fromString(resultSet.getString("id")),
                            resultSet.getString("name"),
                            resultSet.getString("details"),
                            resultSet.getTime("startTime").toLocalTime(),
                            UUID.fromString(resultSet.getString("timeUnit")),
                            resultSet.getInt("timeUnitAmount"),
                            categoryIds);

                    templates.add(template);
                }
            }

            return templates;
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to load all templates", ex);
        }
    }

    @Override
    public Optional<TemplateEntity> findById(UUID id) {
        var sql = "SELECT id, name, details, startTime, timeUnit, timeUnitAmount FROM Template WHERE id = ?;";
        var categorySQL = "SELECT * FROM Template_Category WHERE template_id = ?;";

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

                    return Optional.of(new TemplateEntity(
                            UUID.fromString(resultSet.getString("id")),
                            resultSet.getString("name"),
                            resultSet.getString("details"),
                            resultSet.getTime("startTime").toLocalTime(),
                            UUID.fromString(resultSet.getString("timeUnit")),
                            resultSet.getInt("timeUnitAmount"),
                            categoryList
                    ));
                }
            }
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to retrieve template with id: " + id, ex);
        }
        return Optional.empty();
    }

    @Override
    public TemplateEntity update(TemplateEntity entity) {
        var sql = """
                UPDATE Template
                SET name = ?,
                    details = ?,
                    startTime = ?,
                    timeUnit = ?,
                    timeUnitAmount = ?
                WHERE id = ?;
                """;
        var categoryResetSQL = """
                DELETE FROM Template_Category
                WHERE template_id = ?;
                """;
        var categorySQL = "INSERT INTO Template_Category (template_id, category_id) VALUES (?, ?)";
        var connection = connections.get();
        try (
                var statement = connection.use().prepareStatement(sql);
                var categoryStatement = connection.use().prepareStatement(categorySQL);
                var categoryDeleteStatement = connection.use().prepareStatement(categoryResetSQL);
        ) {
            connection.use().setAutoCommit(false);
            statement.setString(1, entity.name());
            statement.setString(2, entity.details());
            statement.setTime(3, Time.valueOf(entity.startTime()));
            statement.setString(4, entity.timeUnitId().toString());
            statement.setInt(5, entity.timeUnitAmount());
            statement.setString(6, entity.id().toString());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataStorageException("Template not found, id: " + entity.id());
            }
            if (rowsUpdated > 1) {
                throw new DataStorageException("More than 1 time unit (rows=%d) has been updated: %s"
                        .formatted(rowsUpdated, entity));
            }
            if (rowsUpdated == 1){
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
                throw new DataStorageException("Failed to update template: " + entity, ex);
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
                throw new DataStorageException("Failed to rollback: " + entity, rollbackEx);
            }
        }finally {
            try{
                connection.use().setAutoCommit(true);
            }catch(SQLException closeEx){
                throw new DataStorageException("Failed to revert autocommit.", closeEx);
            }
        }
    }


    @Override
    public void deleteById(UUID id) {
        var sql = """
                DELETE FROM Template
                WHERE id = ?;
                """;

        var categoryResetSQL = """
                DELETE FROM Template_Category
                WHERE template_id = ?;
                """;
        var connection = connections.get();
        try (
                var statement = connection.use().prepareStatement(sql);
                var categoryResetStatement = connection.use().prepareStatement(categoryResetSQL);
        ) {
            connection.use().setAutoCommit(false);
            statement.setString(1, String.valueOf(id));
            categoryResetStatement.setString(1, String.valueOf(id));

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DataStorageException("Template not found, id: " + id);
            }
            if (rowsUpdated > 1) {
                throw new DataStorageException("More then 1 template (rows=%d) has been deleted: %s"
                        .formatted(rowsUpdated, id));
            }
            categoryResetStatement.executeUpdate();
            connection.use().commit();
        } catch (SQLException ex) {
            try {
                connection.use().rollback();
                throw new DataStorageException("Failed to delete template, id: " + id, ex);
            } catch (SQLException rollbackEx) {
                throw new DataStorageException("Failed to rollback: " + id, rollbackEx);
            }
        } finally {
            try{
                connection.use().setAutoCommit(true);
            } catch (SQLException closeEx){
                throw new DataStorageException("Failed to revert to autocommit.", closeEx);
            }
        }
    }

    @Override
    public void deleteAll() {
        var sql = "DELETE FROM TimeUnit;";
        var categoryResetSQL = "DELETE FROM Template_Category;";
        var connection = connections.get();
        try (
                var statement = connection.use().prepareStatement(sql);
                var categoryResetStatement = connection.use().prepareStatement(categoryResetSQL);
        ) {
            connection.use().setAutoCommit(false);
            statement.executeUpdate();
            categoryResetStatement.executeUpdate();
            connection.use().commit();
        } catch (SQLException ex) {
            try {
                connection.use().rollback();
                throw new DataStorageException("Failed to delete all templates", ex);
            } catch (SQLException rollbackEx) {
                throw new DataStorageException("Failed to rollback deletion of all templates" , rollbackEx);
            }
        } finally {
            try{
                connection.use().setAutoCommit(true);
            } catch (SQLException closeEx){
                throw new DataStorageException("Failed to revert to autocommit.", closeEx);
            }
        }
    }

}
