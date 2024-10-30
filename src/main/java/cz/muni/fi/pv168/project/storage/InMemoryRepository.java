package cz.muni.fi.pv168.project.storage;

import cz.muni.fi.pv168.project.model.Entity;
import cz.muni.fi.pv168.project.repository.Repository;

import java.util.*;

/**
 * Generic implementation of {@link Repository} which persists entities in memory.
 * @param <T> entity type
 */
public class InMemoryRepository<T extends Entity> implements Repository<T> {

    private Map<UUID, T> data = new HashMap<>();

    public InMemoryRepository(Collection<T> initEntities) {
        initEntities.forEach(this::create);
    }

    private Optional<T> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<T> findAll() {
        return data.values().stream()
                .toList();
    }

    @Override
    public T create(T newEntity) {
        data.put(newEntity.getId(), newEntity);

        System.out.println("[InMemoryStorage] Created entity: " + newEntity);

        return newEntity;
    }

    @Override
    public void update(T entity) {
        var entityOptional = findById(entity.getId());
        if (entityOptional.isEmpty()) {
            throw new IllegalArgumentException("No existing entity found with given id: " + entity.getId());
        }
        data.put(entity.getId(), entity);

        System.out.println("[InMemoryStorage] Updated entity: " + entity);
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        data.remove(id);

        System.out.println("[InMemoryStorage] Deleted entity with id: " + id);
    }

    @Override
    public void deleteAll() {
        data = new HashMap<>();
    }
}
