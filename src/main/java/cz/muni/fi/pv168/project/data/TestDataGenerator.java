package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TestDataGenerator {
    public TodoEvent createTodoEvent() {
        return new TodoEvent("a", "b", LocalDateTime.now(), 6, new Category("a", Color.BLUE));
    }

    public List<TodoEvent> createTodoEvents(int count) {
        return Stream
                .generate(this::createTodoEvent)
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Template> createTemplates() {
        List<Template> templates = new ArrayList<>();
        templates.add(new Template("Yoga", "Yoga in Hotel Passage", LocalDateTime.now(), 30, new Category("self care", Color.RED)));
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
