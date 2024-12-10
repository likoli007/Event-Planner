package cz.muni.fi.pv168.project.storage.sql.entity.mapper;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.storage.sql.entity.TemplateEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TemplateMapper implements EntityMapper<TemplateEntity, Template> {

    @Override
    public Template mapToBusiness(TemplateEntity templateEntity) {
        return new Template(
                templateEntity.id(),
                templateEntity.name(),
                templateEntity.details(),
                templateEntity.startTime(),
                new TimeUnit("UUID.randomUUID()", "name", 30),
                templateEntity.timeUnitAmount(),
                List.of(new Category("name", Color.BLUE))
        );
    }

    @Override
    public TemplateEntity mapEntityToDatabase(Template entity) {
        return new TemplateEntity(
                entity.getId(),
                entity.getName(),
                entity.getDetails(),
                entity.getStartTime(),
                entity.getInterval().getTimeUnit().getId(),
                entity.getInterval().getTimeUnit().getMinutes(),
                entity.getCategories().stream().map(Category::getId).toList()
        );
    }

}
