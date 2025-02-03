package cz.muni.fi.pv168.project.model;

import java.util.Objects;
import java.util.UUID;

public class TimeUnit extends Entity {
    private String name;
    private String shortcut;
    private int minutes;
    private boolean isFixed = false;

    private static final TimeUnit MINUTE = new TimeUnit(UUID.fromString("fce37f43-01d1-43b2-9099-094b0eaf56bd"),
            "Minute", "min", 1, true);

    public TimeUnit(String name, String shortcut, int minutes) {
        this.name = name;
        this.shortcut = shortcut;
        this.minutes = minutes;
    }

    public TimeUnit(String name, String shortcut, int minutes, boolean isFixed) {
        this(name, shortcut, minutes);
        this.isFixed = isFixed;
    }

    public TimeUnit(UUID uuid, String name, String shortcut, int minutes, boolean isFixed) {
        this(name, shortcut, minutes, isFixed);
        this.id = uuid;
    }

    public TimeUnit(UUID uuid, String name, String shortcut, int minutes) {
        this(uuid, name, shortcut, minutes, false);
    }

    public TimeUnit(TimeUnit timeUnit) {
        this.id = timeUnit.id;
        this.name = timeUnit.name;
        this.shortcut = timeUnit.shortcut;
        this.minutes = timeUnit.minutes;
        this.isFixed = timeUnit.isFixed;
    }


    @Override
    public void update(Entity e) {
        if (!(e instanceof TimeUnit timeUnit)) {
            throw new IllegalArgumentException("Cannot update object of different class");
        }

        this.id = timeUnit.id;
        this.name = timeUnit.name;
        this.shortcut = timeUnit.shortcut;
        this.minutes = timeUnit.minutes;
    }

    public static TimeUnit minute() {
        return MINUTE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public boolean isDuplicate(Entity e) {
        if (e == null || getClass() != e.getClass()) return false;
        TimeUnit timeUnit = (TimeUnit) e;
        return Objects.equals(name, timeUnit.name) || Objects.equals(shortcut, timeUnit.shortcut);
    }

    @Override
    public boolean isMeaningfullyDifferent(Entity e) {
        if (e == null || getClass() != e.getClass()) return true;
        TimeUnit timeUnit = (TimeUnit) e;
        if (Objects.equals(name, timeUnit.name) && Objects.equals(shortcut, timeUnit.shortcut) &&
            minutes == timeUnit.getMinutes()) return false;
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isFixed() {
        return isFixed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TimeUnit timeUnit = (TimeUnit) obj;
        return Objects.equals(id, timeUnit.id);
    }
}
