package cz.muni.fi.pv168.project.business.service.crud;

import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.repository.Repository;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class TransactionalTemplateCrudService implements CrudService<Template> {
    TransactionExecutor transactionExecutor;
    private final Repository<Template> templateRepository;

    public TransactionalTemplateCrudService(TransactionExecutor transactionExecutor, Repository<Template> templateRepository) {
        this.transactionExecutor = transactionExecutor;
        this.templateRepository = templateRepository;
    }


    @Override
    public List<Template> findAll() {
        AtomicReference<List<Template>> ref = new AtomicReference<>();
        transactionExecutor.executeInTransaction( () -> {
                List<Template> found = templateRepository.findAll();
                ref.set(found);
        });

        return ref.get();
    }

    @Override
    public Optional<Template> findById(UUID id) {
        AtomicReference<Optional<Template>> ref = new AtomicReference<>();
        transactionExecutor.executeInTransaction( () -> {
            Optional<Template> found = templateRepository.findById(id);
            ref.set(found);
        });
        return ref.get();
    }

    @Override
    public Optional<Template> findDuplicate(Template entity) {
        AtomicReference<Optional<Template>> ref = new AtomicReference<>();
        transactionExecutor.executeInTransaction( () -> {
            Optional<Template> found = templateRepository.findDuplicate(entity);
            ref.set(found);
        });
        return ref.get();
    }

    @Override
    public void create(Template newEntity) {
        transactionExecutor.executeInTransaction( () -> {
            templateRepository.create(newEntity);
        });
    }

    @Override
    public void update(Template entity) {
        transactionExecutor.executeInTransaction( () -> {
            templateRepository.update(entity);
        });
    }

    @Override
    public void deleteById(UUID id) {
        transactionExecutor.executeInTransaction( () -> {
            templateRepository.deleteById(id);
        });
    }

    @Override
    public void deleteAll() {
        transactionExecutor.executeInTransaction( () -> {
            templateRepository.deleteAll();
        });
    }
}
