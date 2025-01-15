package cz.muni.fi.pv168.project.storage.sql;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.repository.Repository;
import cz.muni.fi.pv168.project.storage.sql.dao.DataAccessObject;
import cz.muni.fi.pv168.project.storage.sql.dao.DataStorageException;
import cz.muni.fi.pv168.project.storage.sql.entity.CategoryEntity;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.EntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link Repository} for {@link Category} entity using SQL database.
 */
public class CategorySqlRepository implements Repository<Category> {
    private final DataAccessObject<CategoryEntity> categoryDao;
    private final EntityMapper<CategoryEntity, Category> categoryMapper;

    public CategorySqlRepository(
            DataAccessObject<CategoryEntity> categoryDao,
            EntityMapper<CategoryEntity, Category> categoryMapper) {
        this.categoryDao = categoryDao;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> findAll() {
        return categoryDao
                .findAll()
                .stream()
                .map(categoryMapper::mapToBusiness)
                .toList();
    }

    @Override
    public Category create(Category newEntity) {
        Optional<CategoryEntity> duplicate = categoryDao.findById(newEntity.getId());
        while (duplicate.isPresent()) {
            newEntity.refreshId();
            duplicate = categoryDao.findById(newEntity.getId());
        }
        return categoryMapper.mapToBusiness(categoryDao.create(categoryMapper.mapEntityToDatabase(newEntity)));
    }

    @Override
    public void update(Category entity) {
        categoryDao.findById(entity.getId())
                .orElseThrow(() -> new DataStorageException("Category not found, id: " + entity.getId()));
        var updatedCategory = categoryMapper.mapEntityToDatabase(entity);

        categoryDao.update(updatedCategory);
    }

    @Override
    public void deleteById(UUID id) {
        categoryDao.deleteById(id);
    }

    @Override
    public void deleteAll() {
        categoryDao.deleteAll();
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryDao
                .findById(id)
                .map(categoryMapper::mapToBusiness);
    }

    @Override
    public Optional<Category> findDuplicate(Category entity) {
        for (Category possibleDuplicateEntity : categoryDao.findAll().stream().map(categoryMapper::mapToBusiness).toList()) {
            if (possibleDuplicateEntity.isDuplicate(entity)) {
                return Optional.of(possibleDuplicateEntity);
            }
        }

        return Optional.empty();
    }
}