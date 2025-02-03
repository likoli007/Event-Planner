package cz.muni.fi.pv168.project.storage.sql.entity;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record TemplateEntity(UUID id, String name, String details, LocalTime startTime, UUID timeUnitId, int timeUnitAmount, List<UUID> categoryIds) {
    public TemplateEntity(UUID id, String name, String details, LocalTime startTime, UUID timeUnitId, int timeUnitAmount, List<UUID> categoryIds) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.details = Objects.requireNonNull(details, "details must not be null");
        this.startTime = Objects.requireNonNull(startTime, "startTime must not be null");
        this.timeUnitId = timeUnitId;
        this.timeUnitAmount = timeUnitAmount;
        this.categoryIds = categoryIds;
    }


}
