package cz.muni.fi.pv168.project.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Template {
    private String name;
    private String details;
    private LocalDateTime date;
    private Interval interval;
    private List<Category> categories;

    public Template(String name, String details, LocalDateTime date, TimeUnit timeUnit, int timeUnitAmount, List<Category> categories) {
        this.name = name;
        this.details = details;
        this.date = date;
        this.interval = new Interval(timeUnit, timeUnitAmount);
        this.categories = categories;
    }

    public Template(String name, String details, LocalDateTime date, int minutes, List<Category> categories) {
        this.name = name;
        this.details = details;
        this.date = date;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    public String formatInterval() {
        return interval.format();
    }

    public TodoEvent toTodoEvent(){
            return new TodoEvent(name,details, date, getInterval().getTimeUnit(),
                    getInterval().getAmount(), categories);
    }

    @Override
    public String toString() {
        return name;
    }
}
