package cz.muni.fi.pv168.project.storage.sql.entity.mapper;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.storage.sql.CategorySqlRepository;
import cz.muni.fi.pv168.project.storage.sql.TimeUnitSqlRepository;
import cz.muni.fi.pv168.project.storage.sql.entity.TemplateEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TemplateMapper implements EntityMapper<TemplateEntity, Template> {
    TimeUnitSqlRepository timeUnitSqlRepository;
    CategorySqlRepository categorySqlRepository;


    public TemplateMapper(TimeUnitSqlRepository timeUnitRepository, CategorySqlRepository categoryRepository) {
        this.timeUnitSqlRepository = timeUnitRepository;
        this.categorySqlRepository = categoryRepository;
    }

    @Override
    public Template mapToBusiness(TemplateEntity templateEntity) {
        Optional<TimeUnit> timeUnitResult = timeUnitSqlRepository.findById(templateEntity.timeUnitId());
        TimeUnit timeUnit = timeUnitResult.orElse(null);
        //TODO: null check?

        ArrayList<Category> categories = new ArrayList<>();

        for (int i = 0; i < templateEntity.categoryIds().size(); i++) {
            Optional<Category> categoryResult = categorySqlRepository.findById(templateEntity.categoryIds().get(i));
            Category category = categoryResult.orElse(null);
            categories.add(category);
        }

        return new Template(
                templateEntity.id(),
                templateEntity.name(),
                templateEntity.details(),
                templateEntity.startTime(),
                timeUnit,
                templateEntity.timeUnitAmount(),
                categories
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
                entity.getInterval().getAmount(),
                entity.getCategories().stream().map(Category::getId).toList()
        );
    }

}
