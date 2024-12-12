package cz.muni.fi.pv168.project.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.muni.fi.pv168.project.business.service.export.serialize.CategorySerializer;
import cz.muni.fi.pv168.project.business.service.export.serialize.IntervalSerializer;
import cz.muni.fi.pv168.project.business.service.export.serialize.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    public Template(UUID id, String name, String details, LocalTime startTime, TimeUnit timeUnit, int timeUnitAmount, List<Category> categories) {

        this.id = id;
        this.name = name;
        this.details = details;
        this.startTime = startTime;
        this.interval = new Interval(timeUnit, timeUnitAmount);
        this.categories = categories;
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
    public boolean isMeaningfullyDifferent(Entity e) {
        if (e == null || getClass() != e.getClass()) return true;
        Template template = (Template) e;

        //since inside the system datetime is granular all the way to nanoseconds, need to only take hours and minutes
        //i.e. a template is meaningfully different when the hour and minute of its start time differs, not nanoseconds
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeString = getStartTime().format(timeFormatter);

        if (Objects.equals(name, template.name) && Objects.equals(details, template.details) &&
            Objects.equals(template.getStartTime().toString(), timeString) &&
            !(getInterval().getTimeUnit().isMeaningfullyDifferent(template.getInterval().getTimeUnit())) &&
            getInterval().getAmount() == template.getInterval().getAmount()) {
                for (Category category : categories){
                    boolean isPresent = false;
                    for (Category otherCategory : template.categories) {
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

    @Override
    public String toString() {
        return name;
    }
}
