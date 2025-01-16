package cz.muni.fi.pv168.project.storage.sql;

import cz.muni.fi.pv168.project.business.service.crud.CrudService;
import cz.muni.fi.pv168.project.model.Entity;
import cz.muni.fi.pv168.project.repository.Repository;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Transactional CRUD operations for {@link Entity} subclasses.
 */
public class TransactionalCrudService<T extends Entity> implements CrudService<T> {
    TransactionExecutor transactionExecutor;
    private final CrudService<T> entityCrudService;

    public TransactionalCrudService(TransactionExecutor transactionExecutor, CrudService<T> entityCrudService) {
        this.transactionExecutor = transactionExecutor;
        this.entityCrudService = entityCrudService;
    }

    @Override
    public List<T> findAll() {
        return entityCrudService.findAll();
    }

    @Override
    public Optional<T> findById(UUID id) {
        return entityCrudService.findById(id);
    }

    @Override
    public Optional<T> findDuplicate(T entity) {
        return entityCrudService.findDuplicate(entity);
    }

    @Override
    public void create(T newEntity) {
        Optional<T> duplicate = entityCrudService.findById(newEntity.getId());
        while (duplicate.isPresent()) {
            newEntity.refreshId();
            duplicate = entityCrudService.findById(newEntity.getId());
        }
        transactionExecutor.executeInTransaction( () -> {
            entityCrudService.create(newEntity);
        });
    }

    @Override
    public void update(T entity) {
        transactionExecutor.executeInTransaction( () -> {
            entityCrudService.update(entity);
        });
    }

    @Override
    public void deleteById(UUID id) {
        transactionExecutor.executeInTransaction( () -> {
            entityCrudService.deleteById(id);
        });
    }

    @Override
    public void deleteAll() {
        transactionExecutor.executeInTransaction(entityCrudService::deleteAll);
    }
}
