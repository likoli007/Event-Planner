package cz.muni.fi.pv168.project.business.filter;

import cz.muni.fi.pv168.project.model.TodoEvent;

import java.time.LocalDate;
import java.time.LocalTime;

public class TodoEventFilter implements Filter<TodoEvent> {
    private LocalDate fromDate;
    private LocalTime fromTime;
    private LocalDate toDate;
    private LocalTime toTime;
    private String selectedUnit;      // For selected time units (intervals)
    private String selectedCategory; // For selected categories
    private Boolean isDone;

    public void setSelectedUnit(String selectedUnit) {
        this.selectedUnit = selectedUnit;
    }
    public void setSelectedCategory(String selectedCategory){
        this.selectedCategory = selectedCategory;
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
        selectedUnit = null;
        selectedCategory = null;
        isDone = null;
    }

    @Override
    public Boolean isMatch(TodoEvent entity){
        LocalDate eventDate = entity.getStart().toLocalDate();
        LocalTime eventTime = entity.getStart().toLocalTime();

        return (isDone == null || isDone == entity.isDone())
                && (fromDate == null || !fromDate.isAfter(eventDate))
                && (fromTime == null || !fromTime.isAfter(eventTime))
                && (toDate == null || !toDate.isBefore(eventDate))
                && (toTime == null || !toTime.isBefore(eventTime))
                && (selectedUnit == null || selectedUnit.equals(entity.getInterval().getTimeUnit().getName()))
                && (selectedCategory == null || entity.getCategories().stream()
                    .map(x -> x.getCategory().getName())
                    .anyMatch(selectedCategory::equals));
}
}
