package cz.muni.fi.pv168.project.storage.sql;

import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.repository.Repository;
import cz.muni.fi.pv168.project.storage.sql.dao.DataAccessObject;
import cz.muni.fi.pv168.project.storage.sql.dao.DataStorageException;
import cz.muni.fi.pv168.project.storage.sql.entity.TemplateEntity;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.EntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link Repository} for {@link Template} entity using SQL database.
 */
public class TemplateSqlRepository implements Repository<Template> {
    private final DataAccessObject<TemplateEntity> TemplateDao;
    private final EntityMapper<TemplateEntity, Template> TemplateMapper;

    public TemplateSqlRepository(
            DataAccessObject<TemplateEntity> TemplateDao,
            EntityMapper<TemplateEntity, Template> TemplateMapper) {
        this.TemplateDao = TemplateDao;
        this.TemplateMapper = TemplateMapper;
    }

    @Override
    public List<Template> findAll() {
        return TemplateDao
                .findAll()
                .stream()
                .map(TemplateMapper::mapToBusiness)
                .toList();
    }

    @Override
    public Template create(Template newTemplate) {
        return TemplateMapper.mapToBusiness(TemplateDao.create(TemplateMapper.mapEntityToDatabase(newTemplate)));
    }

    @Override
    public void update(Template entity) {
        TemplateDao.findById(entity.getId())
                .orElseThrow(() -> new DataStorageException("Template not found, id: " + entity.getId()));
        var updatedTemplate = TemplateMapper.mapEntityToDatabase(entity);

        TemplateDao.update(updatedTemplate);
    }

    @Override
    public void deleteById(UUID id) {
        TemplateDao.deleteById(id);
    }

    @Override
    public void deleteAll() {
        TemplateDao.deleteAll();
    }

    @Override
    public Optional<Template> findById(UUID id) {
        return TemplateDao
                .findById(id)
                .map(TemplateMapper::mapToBusiness);
    }

    @Override
    public Optional<Template> findDuplicate(Template entity) {
        for (Template possibleDuplicateEntity : TemplateDao.findAll().stream().map(TemplateMapper::mapToBusiness).toList()) {
            if (possibleDuplicateEntity.isDuplicate(entity)) {
                return Optional.of(possibleDuplicateEntity);
            }
        }

        return Optional.empty();
    }
}