package cz.muni.fi.pv168.project.storage.sql.entity;

import cz.muni.fi.pv168.project.model.Category;

import java.util.Objects;
import java.util.UUID;

/**
 * Representation of {@link Category} entity in a SQL database.
 */
public record CategoryEntity(UUID id, String name, String color) {
    public CategoryEntity(UUID id, String name, String color) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.color = Objects.requireNonNull(color, "color must not be null");
    }
}
