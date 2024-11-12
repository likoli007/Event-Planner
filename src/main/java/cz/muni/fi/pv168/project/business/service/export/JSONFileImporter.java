package cz.muni.fi.pv168.project.business.service.export;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.business.service.export.batch.Batch;
import cz.muni.fi.pv168.project.business.service.export.batch.BatchImporter;
import cz.muni.fi.pv168.project.business.service.export.format.Format;
import cz.muni.fi.pv168.project.validation.Validator;

import javax.swing.*;

public class JSONFileImporter implements BatchImporter {
    @Override
    public Format getFormat() {
        return new Format("json", new ArrayList<String>(List.of("json")) {
        });
    }

    @Override
    public Batch importBatch(String filePath) throws IOException {
        try {
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

                String name = c.get("name").asText();
                String uuid = c.get("id").asText();
                Validator.validateNonemptyString("Category Name", name);
                Validator.validateNonemptyString("Category UUID", uuid);

                Category category = new Category(name, color);

                categoryUUIDs.add(UUID.fromString(uuid));

                categories.add(category);
            }

            JsonNode timeUnitsNode = rootNode.get("timeUnits");
            for (JsonNode t : timeUnitsNode) {
                String name = t.get("name").asText();
                String shortcut = t.get("shortcut").asText();
                int minutes = t.get("minutes").asInt();
                String uuid = t.get("id").asText();

                Validator.validateNonemptyString("Time Unit Name", name);
                Validator.validateNonemptyString("Time Unit Shortcut", shortcut);
                Validator.validateNonemptyString("Time Unit UUID", uuid);

                TimeUnit timeUnit = new TimeUnit(name, shortcut, minutes);

                intervalUUIDs.add(UUID.fromString(uuid));
                intervals.add(timeUnit);
            }

            JsonNode templateNode = rootNode.get("templates");
            for (JsonNode t : templateNode) {
                String name = t.get("name").asText();
                String details = t.get("details").asText();

                LocalTime startTime = LocalTime.of(
                        t.get("startTime").get("hour").asInt(),
                        t.get("startTime").get("minute").asInt(),
                        0, 0);

                String uuid = t.get("interval").get("timeUnit").get("id").asText();
                Validator.validateNonemptyString("Template UUID", uuid);

                UUID targetUUID = UUID.fromString(uuid);
                TimeUnit desiredTimeUnit;

                try {
                    desiredTimeUnit = intervals.get(intervalUUIDs.indexOf(targetUUID));
                } catch (Exception e) {
                    desiredTimeUnit = null;
                }

                ArrayList<Category> categoriesList = new ArrayList<>();
                for (JsonNode c : t.get("categories")) {
                    UUID categoryUUID = UUID.fromString(c.get("id").asText());
                    categoriesList.add(categories.get(categoryUUIDs.indexOf(categoryUUID)));
                }

                Validator.validateNonemptyString("Template Name", name);
                Validator.validateCategoryList(categoriesList);


                Template template;
                if (desiredTimeUnit == null) {
                    template = new Template(
                            t.get("name").asText(),
                            t.get("details").asText(),
                            startTime,
                            t.get("interval").get("amount").asInt(),
                            categoriesList
                    );
                } else {
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
                Validator.validateNonemptyString("Event Name", name);

                String details = e.get("details").asText();
                LocalDateTime startDate = LocalDateTime.of(e.get("start").get("year").asInt(),
                        e.get("start").get("month").asInt(),
                        e.get("start").get("day").asInt(),
                        e.get("start").get("hour").asInt(),
                        e.get("start").get("minute").asInt());

                UUID targetUUID = UUID.fromString(e.get("interval").get("timeUnit").get("id").asText());
                TimeUnit desiredTimeUnit;
                int amount = e.get("interval").get("amount").asInt();
                try {
                    desiredTimeUnit = intervals.get(intervalUUIDs.indexOf(targetUUID));
                } catch (Exception ex) {
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
                } else {
                    event = new TodoEvent(
                            name, details, startDate, desiredTimeUnit, amount, categoriesList
                    );

                }
                events.add(event);
            }

            Batch result = new Batch(categories, intervals, templates, events);
            return result;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                JOptionPane.showMessageDialog(null, "An error occured while importing!\n" +
                        "Skipping import!\n" + "Please ensure there are no faulty fields in the file.",
                        "Alert", JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }
    }
}
