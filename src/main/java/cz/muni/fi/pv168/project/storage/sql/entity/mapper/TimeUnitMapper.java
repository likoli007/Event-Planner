package cz.muni.fi.pv168.project.storage.sql.entity.mapper;

import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.storage.sql.entity.TimeUnitEntity;

import java.util.Objects;

/**
 * Mapper from the {@link TimeUnitEntity} to {@link TimeUnit}.
 */
public final class TimeUnitMapper implements EntityMapper<TimeUnitEntity, TimeUnit> {

    @Override
    public TimeUnit mapToBusiness(TimeUnitEntity timeUnitEntity) {
        return new TimeUnit (
                timeUnitEntity.id(),
                timeUnitEntity.name(),
                timeUnitEntity.shortcut(),
                timeUnitEntity.minutes(),
                Objects.equals(timeUnitEntity.fixed(), TimeUnitEntity.IS_FIXED)
        );
    }

    @Override
    public TimeUnitEntity mapEntityToDatabase(TimeUnit entity) {
        String fixed;
        if (entity.isFixed()) {
            fixed = TimeUnitEntity.IS_FIXED;
        } else {
            fixed = TimeUnitEntity.IS_NOT_FIXED;
        }

        return new TimeUnitEntity(
                entity.getId(),
                entity.getName(),
                entity.getShortcut(),
                entity.getMinutes(),
                fixed
        );
    }
}
