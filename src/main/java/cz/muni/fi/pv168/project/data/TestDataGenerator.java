package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.model.*;

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
                new TodoEvent("Gym Session", "Strength training", LocalDateTime.of(2024, 11, 17, 7, 0), 1, List.of(hobbyCategory, healthCategory))
        );
        return new ArrayList<>(sampleTodoEvents);
    }

    public List<Template> createTemplates() {
        List<Template> templates = new ArrayList<>();
        templates.add(new Template("Yoga", "Yoga in Hotel Passage", LocalTime.now(), 30,
                List.of(new Category("self care", Color.RED), new Category("fitness", Color.GREEN))));
        return templates;
    }

    public List<TimeUnit> createTimeUnits() {
        List<TimeUnit> timeUnits = new ArrayList<>();
        timeUnits.add(new TimeUnit("Teaching hour", "th", 45));
        timeUnits.add(new TimeUnit("Ice Hockey period", "ihp", 20));
        return timeUnits;
    }

    public List<Category> createCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Holiday", Color.BLUE));
        categories.add(new Category("Work", Color.RED));
        categories.add(new Category("Family", Color.GREEN));
        return categories;
    }
}
