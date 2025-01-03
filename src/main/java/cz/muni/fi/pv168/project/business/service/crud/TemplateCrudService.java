package cz.muni.fi.pv168.project.business.service.crud;

import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Crud operations for the {@link Template} entity.
 */
public class TemplateCrudService implements CrudService<Template> {
    private final Repository<Template> templateRepository;

    public TemplateCrudService(Repository<Template> templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public List<Template> findAll() {
        return templateRepository.findAll();
    }

    @Override
    public Optional<Template> findById(UUID id) {
        return templateRepository.findById(id);
    }

    @Override
    public Optional<Template> findDuplicate(Template entity) {
        return templateRepository.findDuplicate(entity);
    }

    @Override
    public void create(Template newEntity) {
        templateRepository.create(newEntity);
    }

    @Override
    public void update(Template entity) {
        templateRepository.update(entity);
    }

    @Override
    public void deleteById(UUID id) {
        templateRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        templateRepository.deleteAll();
    }
}
