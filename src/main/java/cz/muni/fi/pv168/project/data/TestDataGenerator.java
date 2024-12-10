package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.business.service.validation.CategoryValidator;
import cz.muni.fi.pv168.project.business.service.validation.TemplateValidator;
import cz.muni.fi.pv168.project.business.service.validation.TimeUnitValidator;
import cz.muni.fi.pv168.project.business.service.validation.TodoEventValidator;
import cz.muni.fi.pv168.project.model.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TestDataGenerator {
    // Predefined reusable categories
    private final Category workCategory = new Category("Work", Color.BLUE);
    private final Category personalCategory = new Category("Personal", Color.GREEN);
    private final Category healthCategory = new Category("Health", Color.ORANGE);
    private final Category recreationCategory = new Category("Recreation", Color.GREEN);
    private final Category hobbyCategory = new Category("Hobby", Color.YELLOW);

    private final TimeUnit teachingHour = new TimeUnit("Teaching hour", "th", 45);
    private final TimeUnit iceHockeyPeriod = new TimeUnit("Ice Hockey period", "ihp", 20);

    public List<TodoEvent> createTodoEvents() {
        List<TodoEvent> sampleTodoEvents = List.of(
                new TodoEvent("Team Meeting", "Discuss project roadmap", LocalDateTime.of(2024, 11, 3, 10, 0), 24, List.of(workCategory)),
                new TodoEvent("Doctor Appointment", "Annual check-up", LocalDateTime.of(2024, 11, 4, 15, 30), 31, List.of(personalCategory)),
                new TodoEvent("Yoga Class", "Weekly relaxation session", LocalDateTime.of(2024, 11, 5, 18, 0), 41, List.of(healthCategory)),
                new TodoEvent("Conference", "Tech Innovations 2024", LocalDateTime.of(2024, 11, 6, 9, 0), 28, List.of(workCategory)),
                new TodoEvent("Dentist Appointment", "Teeth cleaning", LocalDateTime.of(2024, 11, 7, 11, 0), 1, List.of(personalCategory)),
                new TodoEvent("Grocery Shopping", "Weekly supplies", LocalDateTime.of(2024, 11, 8, 16, 0), 1, List.of(personalCategory)),
                new TodoEvent("Project Review", "Finalize sprint tasks", LocalDateTime.of(2024, 11, 9, 14, 0), 2, List.of(workCategory)),
                new TodoEvent("Birthday Party", "Celebrate friend's birthday", LocalDateTime.of(2024, 11, 10, 19, 0), 3, List.of(personalCategory)),
                new TodoEvent("Webinar", "AI in Healthcare", LocalDateTime.of(2024, 11, 11, 13, 0), 2, List.of(personalCategory, healthCategory)),
                new TodoEvent("Date Night", "Dinner reservation at Luna's", LocalDateTime.of(2024, 11, 12, 20, 0), 40, List.of(personalCategory)),
                new TodoEvent("Client Call", "Discuss project requirements", LocalDateTime.of(2024, 11, 13, 10, 0), 120, List.of(workCategory)),
                new TodoEvent("Workshop", "Leadership Skills", LocalDateTime.of(2024, 11, 14, 9, 0), 60, List.of(workCategory, hobbyCategory, recreationCategory)),
                new TodoEvent("Hiking Trip", "Mountain trail with friends", LocalDateTime.of(2024, 11, 15, 8, 0), 5, List.of(recreationCategory)),
                new TodoEvent("Cooking Class", "Learn to make sushi", LocalDateTime.of(2024, 11, 16, 17, 0), 2, List.of(hobbyCategory)),
                new TodoEvent("Gym Session", "Strength training", LocalDateTime.of(2024, 11, 17, 7, 0), 1, List.of(hobbyCategory, healthCategory)),
                new TodoEvent("Java Programming Lecture", "Databases", LocalDateTime.of(2024, 11, 14, 16, 0), teachingHour, 2, List.of(hobbyCategory)),
                new TodoEvent("Ice Hockey Match", "Kometa vs Plzeň", LocalDateTime.of(2024, 11, 15, 18, 0), iceHockeyPeriod, 3, List.of(hobbyCategory, healthCategory))
        );

        for (TodoEvent todoEvent : sampleTodoEvents) {
            var validator = new TodoEventValidator();
            var result = validator.validate(todoEvent);
            if (!result.isValid()) {
                throw new AssertionError("Todo event contains invalid data: " + todoEvent.getName());
            }
        }

        return new ArrayList<>(sampleTodoEvents);
    }

    public List<Template> createTemplates() {
        List<Template> sampleTemplates = List.of(new Template("Yoga", "Yoga in Hotel Passage", LocalTime.now(), teachingHour, 1, List.of(healthCategory, hobbyCategory)));

        for (Template template : sampleTemplates) {
            var validator = new TemplateValidator();
            var result = validator.validate(template);
            if (!result.isValid()) {
                throw new AssertionError("Template contains invalid data: " + template.getName());
            }
        }

        return sampleTemplates;
    }

    public List<TimeUnit> createTimeUnits() {
        List<TimeUnit> sampleTimeUnits = List.of(teachingHour, iceHockeyPeriod);

        for (TimeUnit timeUnit : sampleTimeUnits) {
            var validator = new TimeUnitValidator();
            var result = validator.validate(timeUnit);
            if (!result.isValid()) {
                throw new AssertionError("Time unit contains invalid data: " + timeUnit.getName());
            }
        }

        return sampleTimeUnits;
    }

    public List<Category> createCategories() {
        List<Category> sampleCategories = List.of(workCategory, personalCategory, healthCategory, recreationCategory, hobbyCategory);

        for (Category category : sampleCategories) {
            var validator = new CategoryValidator();
            var result = validator.validate(category);
            if (!result.isValid()) {
                throw new AssertionError("Category contains invalid data: " + category.getName());
            }
        }

        return sampleCategories;
    }
}
