package cz.muni.fi.pv168.project.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TodoEventFilter {
    private LocalDate fromDate;
    private LocalTime fromTime;
    private LocalDate toDate;
    private LocalTime toTime;
    private List<String> selectedUnits;      // For selected time units (intervals)
    private List<String> selectedCategories; // For selected categories
    private Boolean isDone;


    public TodoEventFilter() {
        this.selectedUnits = new ArrayList<>();
        this.selectedCategories = new ArrayList<>();
    }



    public List<String> getSelectedUnits() {
        return selectedUnits;
    }

    public void setSelectedUnits(List<String> selectedUnits) {
        this.selectedUnits = selectedUnits;
    }

    public List<String> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(List<String> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }
}
