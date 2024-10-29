package cz.muni.fi.pv168.project.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class TodoEvent {
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

    public String formatInterval() {
        return interval.format();
    }
}
