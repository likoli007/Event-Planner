package cz.muni.fi.pv168.project.storage.sql;

import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.repository.Repository;
import cz.muni.fi.pv168.project.storage.sql.dao.DataAccessObject;
import cz.muni.fi.pv168.project.storage.sql.dao.DataStorageException;
import cz.muni.fi.pv168.project.storage.sql.dao.JoinTableDao;
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
    private final JoinTableDao<UUID, UUID> templateCategoryDao;

    private final EntityMapper<TemplateEntity, Template> TemplateMapper;

    public TemplateSqlRepository(
            DataAccessObject<TemplateEntity> TemplateDao, JoinTableDao<UUID, UUID> templateCategoryDao,
            EntityMapper<TemplateEntity, Template> TemplateMapper) {
        this.TemplateDao = TemplateDao;
        this.templateCategoryDao = templateCategoryDao;
        this.TemplateMapper = TemplateMapper;
    }

    @Override
    public List<Template> findAll() {
        var templates = TemplateDao.findAll();

        for (var template : templates) {
            var categories = templateCategoryDao.findByParentId(template.id());
            template.categoryIds().addAll(categories);
        }

        return templates.stream()
                .map(TemplateMapper::mapToBusiness)
                .toList();
    }

    @Override
    public Template create(Template newTemplate) {
        var dbTemplate = TemplateMapper.mapEntityToDatabase(newTemplate);
        var created = TemplateDao.create(dbTemplate);
        templateCategoryDao.updateAssociations(newTemplate.getId(), dbTemplate.categoryIds());
        return TemplateMapper.mapToBusiness(created);
    }

    @Override
    public void update(Template entity) {
        TemplateDao.findById(entity.getId())
                .orElseThrow(() -> new DataStorageException("Template not found, id: " + entity.getId()));

        var dbTemplate = TemplateMapper.mapEntityToDatabase(entity);

        var updated = TemplateDao.update(dbTemplate);
        templateCategoryDao.updateAssociations(entity.getId(), dbTemplate.categoryIds());
    }

    @Override
    public void deleteById(UUID id) {
        templateCategoryDao.deleteByParentId(id);
        TemplateDao.deleteById(id);
    }

    @Override
    public void deleteAll() {
        TemplateDao.deleteAll();
    }

    @Override
    public Optional<Template> findById(UUID id) {
        var template = TemplateDao.findById(id);
        if (template.isEmpty()) {
            return Optional.empty();
        }
        var categories = templateCategoryDao.findByParentId(id);
        template.get().categoryIds().addAll(categories);
        return template
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