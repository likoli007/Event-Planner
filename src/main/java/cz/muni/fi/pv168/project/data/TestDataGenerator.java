package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Color;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;

import java.time.LocalDateTime;
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
}
