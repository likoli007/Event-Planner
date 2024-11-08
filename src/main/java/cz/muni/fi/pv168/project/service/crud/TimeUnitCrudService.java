package cz.muni.fi.pv168.project.service.crud;

import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Crud operations for the {@link TimeUnit} entity.
 */
public class TimeUnitCrudService implements CrudService<TimeUnit> {
    private final Repository<TimeUnit> timeUnitRepository;

    public TimeUnitCrudService(Repository<TimeUnit> timeUnitRepository) {
        this.timeUnitRepository = timeUnitRepository;
    }

    @Override
    public Optional<TimeUnit> findById(UUID id) {
        return timeUnitRepository.findById(id);
    }

    @Override
    public Optional<TimeUnit> findDuplicate(TimeUnit entity) {
        return timeUnitRepository.findDuplicate(entity);
    }

    @Override
    public List<TimeUnit> findAll() {
        return timeUnitRepository.findAll();
    }

    @Override
    public void create(TimeUnit newEntity) {
        timeUnitRepository.create(newEntity);
    }

    @Override
    public void update(TimeUnit entity) {
        timeUnitRepository.update(entity);
    }

    @Override
    public void deleteById(UUID id) {
        timeUnitRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        timeUnitRepository.deleteAll();
    }
}
