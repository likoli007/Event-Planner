package cz.muni.fi.pv168.project.storage.sql.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record TodoEventEntity(UUID id, String name, String details, LocalDateTime startTime, UUID timeUnitId,
                              int timeUnitAmount, List<UUID> categoryIds, String done) {
    public static final String IS_DONE = "IS_DONE";
    public static final String IS_NOT_DONE = "IS_NOT_DONE";

    public TodoEventEntity(UUID id, String name, String details, LocalDateTime startTime, UUID timeUnitId, int timeUnitAmount, List<UUID> categoryIds, String done) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.details = Objects.requireNonNull(details, "details must not be null");
        this.startTime = Objects.requireNonNull(startTime, "startTime must not be null");
        this.timeUnitId = timeUnitId;
        this.timeUnitAmount = timeUnitAmount;
        this.categoryIds = categoryIds;
        this.done = done;
    }
}