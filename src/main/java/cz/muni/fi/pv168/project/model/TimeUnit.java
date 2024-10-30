package cz.muni.fi.pv168.project.model;

import java.util.Objects;

public class TimeUnit extends Entity {
    private String name;
    private String shortcut;
    private int minutes;

    private static final TimeUnit MINUTE = new TimeUnit("Minute", "min", 1);

    public TimeUnit(String name, String shortcut, int minutes) {
        this.name = name;
        this.shortcut = shortcut;
        this.minutes = minutes;
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
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeUnit timeUnit = (TimeUnit) o;
        return minutes == timeUnit.minutes && Objects.equals(name, timeUnit.name) && Objects.equals(shortcut, timeUnit.shortcut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shortcut, minutes);
    }
}
