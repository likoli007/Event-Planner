package cz.muni.fi.pv168.project.storage.sql.dao;

import cz.muni.fi.pv168.project.storage.sql.db.ConnectionHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class TemplateCategoryDao implements JoinTableDao<UUID, UUID> {
    private final Supplier<ConnectionHandler> connections;

    public TemplateCategoryDao(Supplier<ConnectionHandler> connections) {
        this.connections = connections;
    }

    @Override
    public void create(UUID templateId, UUID categoryId) {
        String sql = "INSERT INTO Template_Category (template_id, category_id) VALUES (?, ?);";

        try (var connection = connections.get();
             var statement = connection.use().prepareStatement(sql)) {

            statement.setString(1, templateId.toString());
            statement.setString(2, categoryId.toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to store category for Template: " + templateId, ex);
        }
    }

    @Override
    public List<UUID> findByParentId(UUID templateId) {
        String sql = "SELECT category_id FROM Template_Category WHERE template_id = ?;";

        try (var connection = connections.get();
             var statement = connection.use().prepareStatement(sql)) {

            statement.setString(1, templateId.toString());
            try (var resultSet = statement.executeQuery()) {
                List<UUID> categoryIds = new ArrayList<>();
                while (resultSet.next()) {
                    categoryIds.add(UUID.fromString(resultSet.getString("category_id")));
                }
                return categoryIds;
            }
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to fetch categories for Template: " + templateId, ex);
        }
    }

    @Override
    public void updateAssociations(UUID templateId, List<UUID> newCategoryIds) {
        String deleteSql = """
                    DELETE FROM Template_Category
                    WHERE template_id = ? AND category_id NOT IN (%s);
                """;

        String insertSql = """
                    INSERT INTO Template_Category (template_id, category_id)
                    SELECT ?, ? WHERE NOT EXISTS (
                        SELECT 1 FROM Template_Category WHERE template_id = ? AND category_id = ?
                    );
                """;

        try (var connection = connections.get();
             var deleteStatement = connection.use().prepareStatement(
                     String.format(deleteSql, newCategoryIds.isEmpty() ? "NULL" : "?".repeat(newCategoryIds.size()).replaceAll(".(?=.)", "?, "))
             );
             var insertStatement = connection.use().prepareStatement(insertSql)) {

            // Step 1: Remove outdated associations
            deleteStatement.setString(1, templateId.toString());
            for (int i = 0; i < newCategoryIds.size(); i++) {
                deleteStatement.setString(i + 2, newCategoryIds.get(i).toString());
            }
            deleteStatement.executeUpdate();

            // Step 2: Add new associations
            for (UUID categoryId : newCategoryIds) {
                insertStatement.setString(1, templateId.toString());
                insertStatement.setString(2, categoryId.toString());
                insertStatement.setString(3, templateId.toString());
                insertStatement.setString(4, categoryId.toString());
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();

        } catch (SQLException ex) {
            throw new DataStorageException("Failed to update categories for Template: " + templateId, ex);
        }
    }

    @Override
    public void deleteByParentId(UUID id) {
        String sql = "DELETE FROM Template_Category WHERE template_id = ?;";

        try (var connection = connections.get();
             var statement = connection.use().prepareStatement(sql)) {

            statement.setString(1, id.toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to delete categories for Template: " + id, ex);
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM Template_Category;";

        try (var connection = connections.get();
             var statement = connection.use().prepareStatement(sql)) {

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to delete all Template_Category associations", ex);
        }
    }

}
