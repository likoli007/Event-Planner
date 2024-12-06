package cz.muni.fi.pv168.project.model;

import java.util.UUID;

public abstract class Entity {
    protected UUID id = UUID.randomUUID();

    public UUID getId() {
        return id;
    }

    public abstract boolean isDuplicate(Entity e);

    public abstract boolean isMeaningfullyDifferent(Entity e);

    public abstract void update(Entity e);
}
