package cz.muni.fi.pv168.project.storage.sql.entity.mapper;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.storage.sql.entity.CategoryEntity;

import java.awt.*;

/**
 * Mapper from the {@link CategoryEntity} to {@link Category}.
 */
public final class CategoryMapper implements EntityMapper<CategoryEntity, Category> {

    @Override
    public Category mapToBusiness(CategoryEntity categoryEntity) {
        return new Category (
                categoryEntity.id(),
                categoryEntity.name(),
                Color.GREEN // TODO deserialize color
        );
    }

    @Override
    public CategoryEntity mapEntityToDatabase(Category entity) {
        return new CategoryEntity(
                entity.getId(),
                entity.getName(),
                "#00FF00" // TODO serialize color
        );
    }
}
