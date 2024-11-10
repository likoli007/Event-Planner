package cz.muni.fi.pv168.project.service.crud;

import cz.muni.fi.pv168.project.model.Entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for creation, read, update, and delete operations.
 *
 * @param <T> entity type.
 */
public interface CrudService<T extends Entity> {

    /**
     * Find all entities.
     */
    List<T> findAll();

    /**
     * Find entity with given {@code id}.
     */
    Optional<T> findById(UUID id);

    /**
     * Find entity which is duplicate of given {@code entity}.
     */
    Optional<T> findDuplicate(T entity);

    /**
     * Store the given {@code newEntity}.
     */
    void create(T newEntity);

    /**
     * Updates the given {@code entity}.
     */
    void update(T entity);

    /**
     * Delete entity with given {@code id}.
     */
    void deleteById(UUID id);

    /**
     * Delete all entities.
     */
    void deleteAll();
}
