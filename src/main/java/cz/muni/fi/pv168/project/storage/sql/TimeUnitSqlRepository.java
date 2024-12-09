package cz.muni.fi.pv168.project.storage.sql;

import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.repository.Repository;
import cz.muni.fi.pv168.project.storage.sql.dao.DataAccessObject;
import cz.muni.fi.pv168.project.storage.sql.dao.DataStorageException;
import cz.muni.fi.pv168.project.storage.sql.entity.TimeUnitEntity;
import cz.muni.fi.pv168.project.storage.sql.entity.mapper.EntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link Repository} for {@link TimeUnit} entity using SQL database.
 */
public class TimeUnitSqlRepository implements Repository<TimeUnit> {
    private final DataAccessObject<TimeUnitEntity> timeUnitDao;
    private final EntityMapper<TimeUnitEntity, TimeUnit> timeUnitMapper;

    public TimeUnitSqlRepository(
            DataAccessObject<TimeUnitEntity> timeUnitDao,
            EntityMapper<TimeUnitEntity, TimeUnit> timeUnitMapper) {
        this.timeUnitDao = timeUnitDao;
        this.timeUnitMapper = timeUnitMapper;
    }

    @Override
    public List<TimeUnit> findAll() {
        return timeUnitDao
                .findAll()
                .stream()
                .map(timeUnitMapper::mapToBusiness)
                .toList();
    }

    @Override
    public TimeUnit create(TimeUnit newTimeUnit) {
        return timeUnitMapper.mapToBusiness(timeUnitDao.create(timeUnitMapper.mapEntityToDatabase(newTimeUnit)));
    }

    @Override
    public void update(TimeUnit entity) {
        timeUnitDao.findById(entity.getId())
                .orElseThrow(() -> new DataStorageException("Time unit not found, id: " + entity.getId()));
        var updatedTimeUnit = timeUnitMapper.mapEntityToDatabase(entity);

        timeUnitDao.update(updatedTimeUnit);
    }

    @Override
    public void deleteById(UUID id) {
        timeUnitDao.deleteById(id);
    }

    @Override
    public void deleteAll() {
        timeUnitDao.deleteAll();
    }

    @Override
    public Optional<TimeUnit> findById(UUID id) {
        return timeUnitDao
                .findById(id)
                .map(timeUnitMapper::mapToBusiness);
    }

    @Override
    public Optional<TimeUnit> findDuplicate(TimeUnit entity) {
        for (TimeUnit possibleDuplicateEntity : timeUnitDao.findAll().stream().map(timeUnitMapper::mapToBusiness).toList()) {
            if (possibleDuplicateEntity.isDuplicate(entity)) {
                return Optional.of(possibleDuplicateEntity);
            }
        }

        return Optional.empty();
    }
}