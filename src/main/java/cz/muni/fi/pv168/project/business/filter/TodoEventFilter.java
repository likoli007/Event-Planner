package cz.muni.fi.pv168.project.business.filter;

import cz.muni.fi.pv168.project.model.TodoEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TodoEventFilter implements Filter<TodoEvent> {
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

    public void addSelectedCategory(String selectedCategory){

        this.selectedCategories.add(selectedCategory);
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }

    public void clear(){
        fromDate = null;
        fromTime = null;
        toDate = null;
        toTime = null;
        selectedUnits.clear();
        selectedCategories.clear();
        isDone = null;
    }

    @Override
    public Boolean isMatch(TodoEvent entity){
        return (isDone == null || isDone == entity.isDone())
                && (fromDate == null || !fromDate.isAfter(entity.getStart().toLocalDate()))
                && (fromTime == null || !fromTime.isAfter(entity.getStart().toLocalTime()))
                && (toDate == null || !toDate.isBefore(entity.getStart().toLocalDate()))
                && (toTime == null || !toTime.isBefore(entity.getStart().toLocalTime()))
                && (selectedUnits.isEmpty() || selectedUnits.contains(entity.getInterval().getTimeUnit().getName()))
                && (selectedCategories.isEmpty() || entity.getCategories().stream()
                    .map(x -> x.getCategory().getName())
                    .anyMatch(selectedCategories::contains));
}
}
