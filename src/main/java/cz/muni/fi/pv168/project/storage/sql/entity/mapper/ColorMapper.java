package cz.muni.fi.pv168.project.storage.sql.entity.mapper;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.storage.sql.entity.CategoryEntity;

import java.awt.*;

/**
 * Mapper from the {@link String} to {@link Color}.
 */
public final class ColorMapper implements EntityMapper<String, Color> {
    @Override
    public Color mapToBusiness(String stringRepresentation) {
        return Color.decode(stringRepresentation);
    }

    @Override
    public String mapEntityToDatabase(Color entity) {
        return String.format("#%02x%02x%02x", entity.getRed(), entity.getGreen(), entity.getBlue());
    }
}
