package cz.muni.fi.pv168.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.muni.fi.pv168.project.business.service.export.serialize.CategorySerializer;
import cz.muni.fi.pv168.project.business.service.export.serialize.DateTimeSerializer;
import cz.muni.fi.pv168.project.business.service.export.serialize.IntervalSerializer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TodoEvent extends Entity {
    private String name;
    private String details;

    @JsonSerialize(using = DateTimeSerializer.class)
    private LocalDateTime start;

    @JsonSerialize(using = IntervalSerializer.class)
    private Interval interval;

    @JsonSerialize(contentUsing = CategorySerializer.class)
    private List<Category> categories;
    private boolean done = false;

    public TodoEvent(String name, String details, LocalDateTime start, TimeUnit timeUnit, int timeUnitAmount, List<Category> categories, boolean done) {
        this(name, details, start, timeUnit, timeUnitAmount, categories);
        this.done = done;
    }
    public TodoEvent(String name, String details, LocalDateTime start, TimeUnit timeUnit, int timeUnitAmount, List<Category> categories) {
        this.name = name;
        this.details = details;
        this.start = start;
        this.interval = new Interval(timeUnit, timeUnitAmount);
        this.categories = categories;
    }

    public TodoEvent(String name, String details, LocalDateTime start, int minutes, List<Category> categories) {
        this(name, details, start, TimeUnit.minute(), minutes, categories);
    }

    public TodoEvent(TodoEvent todoEvent) {
        this.id = todoEvent.id;
        this.name = todoEvent.name;
        this.details = todoEvent.details;
        this.start = todoEvent.start;
        this.interval = todoEvent.interval;
        this.categories = todoEvent.categories;
        this.done = todoEvent.done;
    }

    public TodoEvent(UUID id, String name, String details, LocalDateTime start, TimeUnit timeUnit, int timeUnitAmount, List<Category> categories, boolean done) {
        this(name, details, start, timeUnit, timeUnitAmount, categories, done);
        this.id = id;
    }

    //Done so that there is no 'ID' field in exported JSON file
    @Override
    @JsonIgnore
    public UUID getId() {
        return id;
    }

    @Override
    public void update(Entity e) {
        if (!(e instanceof TodoEvent todoEvent)) {
            throw new IllegalArgumentException("Cannot update object of different class");
        }

        this.id = todoEvent.id;
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
        return categories;
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
    public boolean isDuplicate(Entity e) {
        if (e == null || getClass() != e.getClass()) return false;
        TodoEvent todoEvent = (TodoEvent) e;
        return Objects.equals(name, todoEvent.name) && Objects.equals(start, todoEvent.start);
    }

    @Override
    public boolean isMeaningfullyDifferent(Entity e){
        if (e == null || getClass() != e.getClass()) return true;
        TodoEvent todoEvent = (TodoEvent) e;
        if (Objects.equals(name, todoEvent.name) &&
            Objects.equals(todoEvent.getStart().toString(), getStart().toString()) &&
            Objects.equals(details, todoEvent.details) &&
            !(getInterval().getTimeUnit().isMeaningfullyDifferent(todoEvent.getInterval().getTimeUnit())) &&
            getInterval().getAmount() == todoEvent.getInterval().getAmount() && done == todoEvent.isDone()){
                for (Category category : categories) {
                    boolean isPresent = false;
                    for (Category otherCategory : todoEvent.categories) {
                        if (category.isDuplicate(otherCategory)) {
                            isPresent = true;
                        }
                    }
                    if (!isPresent) return true;
                }
                return false;
        }

        return true;
    }

    public String formatInterval() {
        return interval.format();
    }
}
