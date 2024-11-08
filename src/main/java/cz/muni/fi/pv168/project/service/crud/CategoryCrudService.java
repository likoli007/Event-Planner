package cz.muni.fi.pv168.project.service.crud;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Crud operations for the {@link Category} entity.
 */
public class CategoryCrudService implements CrudService<Category> {
    private final Repository<Category> categoryRepository;

    public CategoryCrudService(Repository<Category> categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<Category> findDuplicate(Category entity) {
        return categoryRepository.findDuplicate(entity);
    }

    @Override
    public void create(Category newEntity) {
        categoryRepository.create(newEntity);
    }

    @Override
    public void update(Category entity) {
        categoryRepository.update(entity);
    }

    @Override
    public void deleteById(UUID id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        categoryRepository.deleteAll();
    }
}
