package cz.muni.fi.pv168.project.storage.sql.dao;

import cz.muni.fi.pv168.project.storage.sql.db.ConnectionHandler;
import cz.muni.fi.pv168.project.storage.sql.entity.CategoryEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Supplier;

/**
 * DAO for {@link CategoryEntity} entity.
 */
public final class CategoryDao implements DataAccessObject<CategoryEntity> {
    private final Supplier<ConnectionHandler> connections;

    public CategoryDao(Supplier<ConnectionHandler> connections) {
        this.connections = connections;
    }

    @Override
    public CategoryEntity create(CategoryEntity newCategory) {
        var sql = "INSERT INTO Category (id, name, color) VALUES (?, ?, ?);";

        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, String.valueOf(newCategory.id()));
            statement.setString(2, newCategory.name());
            statement.setString(3, newCategory.color());
            statement.executeUpdate();

            try (ResultSet keyResultSet = statement.getGeneratedKeys()) {
                UUID categoryId;

                if (keyResultSet.next()) {
                    categoryId = UUID.fromString(keyResultSet.getString(1));
                } else {
                    throw new DataStorageException("Failed to fetch generated key for: " + newCategory);
                }
                if (keyResultSet.next()) {
                    throw new DataStorageException("Multiple keys returned for: " + newCategory);
                }

                return findById(categoryId).orElseThrow();
            }
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to store: " + newCategory, ex);
        }
    }

    @Override
    public Collection<CategoryEntity> findAll() {
        var sql = """
                SELECT id,
                       name,
                       color
                FROM Category;
                """;
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql)
        ) {
            List<CategoryEntity> categories = new ArrayList<>();
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var category = categoryFromResultSet(resultSet);
                    categories.add(category);
                }
            }

            return categories;
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to load all categories", ex);
        }
    }

    @Override
    public Optional<CategoryEntity> findById(UUID id) {
        var sql = """
                SELECT id,
                       name,
                       color
                FROM Category
                WHERE id = ?;
                """;
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql)
        ) {
            statement.setString(1, String.valueOf(id));
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(categoryFromResultSet(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to load category by id: " + id, ex);
        }
    }

    @Override
    public CategoryEntity update(CategoryEntity entity) {
        var sql = """
                UPDATE Category
                SET name = ?,
                    color = ?
                WHERE id = ?;
                """;
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql)
        ) {
            statement.setString(1, entity.name());
            statement.setString(2, entity.color());
            statement.setString(3, String.valueOf(entity.id()));
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataStorageException("Category not found, id: " + entity.id());
            }
            if (rowsUpdated > 1) {
                throw new DataStorageException("More then 1 category (rows=%d) has been updated: %s"
                        .formatted(rowsUpdated, entity));
            }
            return entity;
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to update category: " + entity, ex);
        }
    }

    @Override
    public void deleteById(UUID id) {
        var sql = """
                DELETE FROM Category
                WHERE id = ?;
                """;
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql)
        ) {
            statement.setString(1, String.valueOf(id));
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataStorageException("Category not found, id: " + id);
            }
            if (rowsUpdated > 1) {
                throw new DataStorageException("More then 1 category (rows=%d) has been deleted: %s"
                        .formatted(rowsUpdated, id));
            }
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to delete category, id: " + id, ex);
        }
    }

    @Override
    public void deleteAll() {
        var sql = "DELETE FROM Category;";
        try (
                var connection = connections.get();
                var statement = connection.use().prepareStatement(sql)
        ) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataStorageException("Failed to delete all categories", ex);
        }
    }

    private static CategoryEntity categoryFromResultSet(ResultSet resultSet) throws SQLException {
        return new CategoryEntity(
                UUID.fromString(resultSet.getString("id")),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
    }
}

