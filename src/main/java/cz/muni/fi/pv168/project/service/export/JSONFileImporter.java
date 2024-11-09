package cz.muni.fi.pv168.project.service.export;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.service.export.batch.Batch;
import cz.muni.fi.pv168.project.service.export.batch.BatchImporter;
import cz.muni.fi.pv168.project.service.export.format.Format;

public class JSONFileImporter implements BatchImporter {
    @Override
    public Format getFormat() {
        return new Format("json", new ArrayList<String>(List.of("json")) {
        });
    }

    @Override
    public Batch importBatch(String filePath) throws IOException {
        FileReader file = new FileReader(filePath);
        BufferedReader br = new BufferedReader(file);

        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<TimeUnit> intervals = new ArrayList<>();
        ArrayList<Template> templates = new ArrayList<>();
        ArrayList<TodoEvent> events = new ArrayList<>();
        ArrayList<UUID> intervalUUIDs = new ArrayList<>();
        ArrayList<UUID> categoryUUIDs = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(new File(filePath));

        JsonNode categoriesNode = rootNode.get("categories");
        for (JsonNode c : categoriesNode) {
            Color color = new Color(
                    c.get("color").get("r").asInt(),
                    c.get("color").get("g").asInt(),
                    c.get("color").get("b").asInt()
            );
            Category category = new Category(c.get("name").asText(), color);

            categoryUUIDs.add(UUID.fromString(c.get("id").asText()));

            categories.add(category);
        }

        JsonNode timeUnitsNode = rootNode.get("timeUnits");
        for (JsonNode t : timeUnitsNode) {
            TimeUnit timeUnit = new TimeUnit(t.get("name").asText(), t.get("shortcut").asText(), t.get("minutes").asInt());

            intervalUUIDs.add(UUID.fromString(t.get("id").asText()));

            intervals.add(timeUnit);
        }

        JsonNode templateNode = rootNode.get("templates");
        for (JsonNode t : templateNode) {
            LocalTime startTime =  LocalTime.of(
                    t.get("startTime").get("hour").asInt(),
                    t.get("startTime").get("minute").asInt(),
                    0, 0);

            UUID targetUUID = UUID.fromString(t.get("interval").get("timeUnit").get("id").asText());
            TimeUnit desiredTimeUnit;
            try{
                desiredTimeUnit = intervals.get(intervalUUIDs.indexOf(targetUUID));
            }
            catch (Exception e){
                desiredTimeUnit = null;
            }

            //Interval interval = new Interval(desiredTimeUnit, t.get("interval").get("amount").asInt());

            ArrayList<Category> categoriesList = new ArrayList<>();
            for (JsonNode c : t.get("categories")) {
                UUID categoryUUID = UUID.fromString(c.get("id").asText());
                categoriesList.add(categories.get(categoryUUIDs.indexOf(categoryUUID)));
            }

            Template template;
            if (desiredTimeUnit == null) {
                template = new Template(
                        t.get("name").asText(),
                        t.get("details").asText(),
                        startTime,
                        t.get("interval").get("amount").asInt(),
                        categoriesList
                );
            }
            else{
                template = new Template(
                        t.get("name").asText(),
                        t.get("details").asText(),
                        startTime,
                        desiredTimeUnit,
                        t.get("interval").get("amount").asInt(),
                        categoriesList
                );
            }
            templates.add(template);
        }

        JsonNode eventsNode = rootNode.get("events");
        for (JsonNode e : eventsNode) {
            String name = e.get("name").asText();
            String details = e.get("details").asText();
            LocalDateTime startDate = LocalDateTime.of(e.get("start").get("year").asInt(),
                            e.get("start").get("month").asInt(),
                            e.get("start").get("day").asInt(),
                            e.get("start").get("hour").asInt(),
                            e.get("start").get("minute").asInt());

            UUID targetUUID = UUID.fromString(e.get("interval").get("timeUnit").get("id").asText());
            TimeUnit desiredTimeUnit;
            int amount = e.get("interval").get("amount").asInt();
            try{
                desiredTimeUnit = intervals.get(intervalUUIDs.indexOf(targetUUID));
            }
            catch (Exception ex){
                desiredTimeUnit = null;
            }

            ArrayList<Category> categoriesList = new ArrayList<>();
            for (JsonNode c : e.get("categories")) {
                UUID categoryUUID = UUID.fromString(c.get("id").asText());
                categoriesList.add(categories.get(categoryUUIDs.indexOf(categoryUUID)));
            }

           TodoEvent event;

            if (desiredTimeUnit == null) {
                event = new TodoEvent(
                        name, details, startDate, amount, categoriesList
                );
            }
            else{
                event = new TodoEvent(
                        name, details, startDate, desiredTimeUnit, amount, categoriesList
                );

            }
            events.add(event);
        }

        System.out.println("Events count: " + events.size() + "Categories count: " + categories.size()
                + "Templates count: " + templates.size() + "Intervals count: " + intervals.size());

        Batch result = new Batch(categories, intervals, templates, events);
        return result;
    }
}
