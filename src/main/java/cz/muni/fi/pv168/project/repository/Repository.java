package cz.muni.fi.pv168.project.repository;

import cz.muni.fi.pv168.project.model.Entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a repository for any entity.
 *
 * @param <T> the type of the entity.
 */
public interface Repository<T extends Entity> {

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
     * Persist given {@code newEntity}.
     *
     * @return the persisted entity.
     */
    T create(T newEntity);

    /**
     * Update given {@code entity}.
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
