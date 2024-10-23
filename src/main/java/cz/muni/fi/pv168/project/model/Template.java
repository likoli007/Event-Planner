package cz.muni.fi.pv168.project.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Template {
    private String name;
    private String details;
    private LocalDateTime date;
    private TimeUnit timeUnit;
    private int timeUnitAmount;
    private List<Category> categories;

    public Template(String name, String details, LocalDateTime date, TimeUnit timeUnit, int timeUnitAmount, List<Category> categories) {
        this.name = name;
        this.details = details;
        this.date = date;
        this.timeUnit = timeUnit;
        this.timeUnitAmount = timeUnitAmount;
        this.categories = categories;
    }

    public Template(String name, String details, LocalDateTime date, int minutes, List<Category> categories) {
        this.name = name;
        this.details = details;
        this.date = date;
        this.timeUnit = TimeUnit.minute();
        this.timeUnitAmount = minutes;
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

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getTimeUnitAmount() {
        return timeUnitAmount;
    }

    public void setTimeUnitAmount(int timeUnitAmount) {
        this.timeUnitAmount = timeUnitAmount;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String formatInterval() {
        StringBuilder sb = new StringBuilder(timeUnitAmount + " " + timeUnit.getShortcut());

        if (timeUnit != TimeUnit.minute()) {
            sb
                    .append(" (")
                    .append(timeUnit.getMinutes() * timeUnitAmount)
                    .append(" ")
                    .append(TimeUnit.minute().getShortcut())
                    .append(")");
        }

        return sb.toString();
    }
    public TodoEvent toTodoEvent(){
            return new TodoEvent(name,details, date,timeUnit, timeUnitAmount, categories );
    }

    @Override
    public String toString() {
        return name;
    }
}
