package cz.muni.fi.pv168.project.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.muni.fi.pv168.project.business.service.export.serialize.CategorySerializer;
import cz.muni.fi.pv168.project.business.service.export.serialize.IntervalSerializer;
import cz.muni.fi.pv168.project.business.service.export.serialize.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class Template extends Entity {
    private String name;
    private String details;

    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime startTime;
    @JsonSerialize(using = IntervalSerializer.class)
    private Interval interval;
    @JsonSerialize(contentUsing = CategorySerializer.class)
    private List<Category> categories;

    public Template(String name, String details, LocalTime startTime, TimeUnit timeUnit, int timeUnitAmount, List<Category> categories) {
        this.name = name;
        this.details = details;
        this.startTime = startTime;
        this.interval = new Interval(timeUnit, timeUnitAmount);
        this.categories = categories;
    }

    public Template(String name, String details, LocalTime startTime, int minutes, List<Category> categories) {
        this.name = name;
        this.details = details;
        this.startTime = startTime;
        this.interval = new Interval(TimeUnit.minute(), minutes);
        this.categories = categories;
    }

    public Template(Template template) {
        this.id = template.id;
        this.name = template.name;
        this.details = template.details;
        this.startTime = template.startTime;
        this.interval = template.interval;
        this.categories = template.categories;
    }

    @Override
    public void update(Entity e) {
        if (!(e instanceof Template template)) {
            throw new IllegalArgumentException("Cannot update object of different class");
        }

        this.id = template.id;
        this.name = template.name;
        this.details = template.details;
        this.startTime = template.startTime;
        this.interval = template.interval;
        this.categories = template.categories;
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
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

    public String formatInterval() {
        return interval.format();
    }

    public TodoEvent toTodoEvent(){
            return new TodoEvent(name,details, startTime.atDate(LocalDate.now()), getInterval().getTimeUnit(), getInterval().getAmount(), categories );
    }

    @Override
    public boolean isDuplicate(Entity e) {
        if (e == null || getClass() != e.getClass()) return false;
        Template template = (Template) e;
        return Objects.equals(name, template.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
