package cz.muni.fi.pv168.project.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TodoEvent extends Entity {
    private String name;
    private String details;
    private LocalDateTime start;
    private Interval interval;
    private List<Category> categories;
    private boolean done = false;

    public TodoEvent(String name, String details, LocalDateTime start, TimeUnit timeUnit, int timeUnitAmount, List<Category> categories) {
        this.name = name;
        this.details = details;
        this.start = start;
        this.interval = new Interval(timeUnit, timeUnitAmount);
        this.categories = categories;
    }

    public TodoEvent(String name, String details, LocalDateTime start, int minutes, List<Category> categories) {
        this.name = name;
        this.details = details;
        this.start = start;
        this.interval = new Interval(TimeUnit.minute(), minutes);
        this.categories = categories;
    }

    public TodoEvent(TodoEvent todoEvent) {
        this.name = todoEvent.name;
        this.details = todoEvent.details;
        this.start = todoEvent.start;
        this.interval = todoEvent.interval;
        this.categories = todoEvent.categories;
        this.done = todoEvent.done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoEvent todoEvent = (TodoEvent) o;
        return Objects.equals(name, todoEvent.name) && Objects.equals(start, todoEvent.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, start);
    }

    public String formatInterval() {
        return interval.format();
    }
}
