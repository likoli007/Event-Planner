package cz.muni.fi.pv168.project.model;

import java.time.LocalDateTime;

public class Template {
    private String name;
    private String details;
    private LocalDateTime date;
    private TimeUnit timeUnit;
    private int timeUnitAmount;
    private Category category;

    public Template(String name, String details, LocalDateTime date, TimeUnit timeUnit, int timeUnitAmount, Category category) {
        this.name = name;
        this.details = details;
        this.date = date;
        this.timeUnit = timeUnit;
        this.timeUnitAmount = timeUnitAmount;
        this.category = category;
    }

    public Template(String name, String details, LocalDateTime date, int minutes, Category category) {
        this.name = name;
        this.details = details;
        this.date = date;
        this.timeUnit = TimeUnit.minute();
        this.timeUnitAmount = minutes;
        this.category = category;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
}
