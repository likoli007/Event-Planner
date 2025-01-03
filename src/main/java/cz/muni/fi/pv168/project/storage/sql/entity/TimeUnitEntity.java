package cz.muni.fi.pv168.project.storage.sql.entity;

import cz.muni.fi.pv168.project.model.TimeUnit;

import java.util.Objects;
import java.util.UUID;

/**
 * Representation of {@link TimeUnit} entity in a SQL database.
 */
public record TimeUnitEntity(UUID id, String name, String shortcut, int minutes, boolean isSystemDefined) {
    public TimeUnitEntity(UUID id, String name, String shortcut, int minutes, boolean isSystemDefined) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.shortcut = Objects.requireNonNull(shortcut, "shortcut must not be null");
        this.minutes = Objects.requireNonNull(minutes, "minutes must not be null");
        this.isSystemDefined = isSystemDefined;
    }
}
