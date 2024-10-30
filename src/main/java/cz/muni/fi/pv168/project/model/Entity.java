package cz.muni.fi.pv168.project.model;

import java.util.UUID;

public abstract class Entity {
    protected UUID id = UUID.randomUUID();

    public UUID getId() {
        return id;
    }
}
