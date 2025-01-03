package cz.muni.fi.pv168.project.storage.sql.entity.mapper;

import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.storage.sql.entity.TimeUnitEntity;

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
                timeUnitEntity.isSystemDefined()
        );
    }

    @Override
    public TimeUnitEntity mapEntityToDatabase(TimeUnit entity) {
        return new TimeUnitEntity(
                entity.getId(),
                entity.getName(),
                entity.getShortcut(),
                entity.getMinutes(),
                entity.isSystemDefined()
        );
    }
}
