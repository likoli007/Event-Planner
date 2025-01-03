package cz.muni.fi.pv168.project.business.service.export;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.pv168.project.business.service.export.batch.SingleResult;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.business.service.export.batch.Batch;
import cz.muni.fi.pv168.project.business.service.export.batch.BatchImporter;
import cz.muni.fi.pv168.project.business.service.export.format.Format;



public class JSONFileImporter implements BatchImporter {
    ArrayList<Category> _categories = new ArrayList<>();
    ArrayList<TimeUnit> _intervals = new ArrayList<>();
    ArrayList<Template> _templates = new ArrayList<>();
    ArrayList<TodoEvent> _events = new ArrayList<>();
    ArrayList<UUID> intervalUUIDs = new ArrayList<>();
    ArrayList<UUID> categoryUUIDs = new ArrayList<>();

    @Override
    public Format getFormat() {
        return new Format("json", new ArrayList<String>(List.of("json")) {
        });
    }

    private SingleResult<Integer> validateColorPart(JsonNode parentNode, String colorPart) {
        SingleResult<Integer> result = new SingleResult<>();

        JsonNode node = parentNode.get(colorPart);
        if (node == null) {
            return result.setMessage("Missing a color value of '" + colorPart + "'.");
        }

        int value = node.asInt();
        if (value < 0 || value > 255) {
            return result.setMessage("Color value '" + value + "' of '" + colorPart + "' is not in interval <0;255>.");
        }

        return result.setData(value);
    }

    private SingleResult<Color> validateColor(JsonNode node) {
        SingleResult<Color> result = new SingleResult<>();

        JsonNode colorNode = node.get("color");
        if (colorNode == null) {
            return result.setMessage("Missing field: 'color'.");
        }

        List<String> colorParts = Arrays.asList("r", "g", "b");
        List<Integer> colorValues = new ArrayList<>();
        for (String colorPart : colorParts) {
            SingleResult<Integer> colorPartResult = validateColorPart(colorNode, colorPart);

            if (!colorPartResult.isSuccess()) {
                return result.setMessage("Color node has corrupted data. " + colorPartResult.getMessage());
            }

            colorValues.add(colorPartResult.getData());
        }

        return result.setData(new Color(colorValues.get(0), colorValues.get(1), colorValues.get(2)));
    }


    private SingleResult<String> validateStringField(String fieldName, JsonNode node) {
        SingleResult<String> result = new SingleResult<>();
        JsonNode nameNode = node.get(fieldName);
        if (nameNode == null) {
            return result.setMessage("Missing field: '" + fieldName + "'.");

        }
        String data = nameNode.asText();
        if (data == null ) {
            return result.setMessage("Field \"" + fieldName + "\" has an invalid value: 'null'.");
        }
        if (data.isEmpty()){
            return result.setMessage("Field \"" + fieldName + "\" has an empty string value.");
        }

        return result.setData(data);
    }

    private SingleResult<Boolean> validateBooleanField(String fieldName, JsonNode node) {
        SingleResult<Boolean> result = new SingleResult<>();
        JsonNode nameNode = node.get(fieldName);
        if (nameNode == null) {
            return result.setMessage("Missing field: '" + fieldName + "'.");
        }
        if (!nameNode.isBoolean()){
            return result.setMessage("Field \"" + fieldName + "\" is not a boolean value.");
        }

        Boolean data = nameNode.asBoolean();
        return result.setData(data);
    }

    private SingleResult<Integer> validateIntField(String fieldName, JsonNode node){
        SingleResult<Integer> result = new SingleResult<>();
        JsonNode nameNode = node.get(fieldName);
        if (nameNode == null) {
            return result.setMessage("Missing field: '" + fieldName + "'.");
        }

        if (!nameNode.isInt()) {
            return result.setMessage("Field '" + fieldName + "' has a non-integer value: '" + nameNode.asText() + "'.");
        }

        int data = nameNode.asInt();
        return result.setData(data);
    }

    public SingleResult<ArrayList<Category>> importCategories( JsonNode rootNode) {
        SingleResult<ArrayList<Category>> result = new SingleResult<>();
        String errorPrepend = "Error in Category importing: ";
        ArrayList<Category> categories = new ArrayList<>();


        JsonNode categoriesNode = rootNode.get("categories");
        if (categoriesNode == null) {
            return result.setMessage(errorPrepend + "No node named 'categories' exists.");
        }
        for (JsonNode c : categoriesNode) {
            SingleResult<Color> colorResult = validateColor(c);
            if (!colorResult.isSuccess()) {
                return result.setMessage(errorPrepend + colorResult.getMessage());
            }
            SingleResult<String> nameResult = validateStringField("name", c);
            SingleResult<String> idResult = validateStringField("id", c);

            if (!nameResult.isSuccess()) {
                return result.setMessage(errorPrepend + nameResult.getMessage());
            }
            if(!idResult.isSuccess()){
                return result.setMessage(errorPrepend + idResult.getMessage());
            }

            String name = nameResult.getData();
            String uuid = idResult.getData();
            Color color = colorResult.getData();

            Category category = new Category(name, color);
            categoryUUIDs.add(UUID.fromString(uuid));
            categories.add(category);
        }
        return result.setData(categories);
    }

    public SingleResult<ArrayList<TimeUnit>> importTimeUnits( JsonNode rootNode) {
        SingleResult<ArrayList<TimeUnit>> result = new SingleResult<>();
        ArrayList<TimeUnit> intervals = new ArrayList<>();
        String errorPrepend = "Error in TimeUnit importing: ";

        JsonNode timeUnitsNode = rootNode.get("timeUnits");
        if (timeUnitsNode == null) {
            return result.setMessage(errorPrepend + "No node named 'timeUnits' exists.");
        }

        for (JsonNode t : timeUnitsNode) {
            SingleResult<String> nameResult = validateStringField("name", t);
            SingleResult<String> shortcutResult = validateStringField("shortcut", t);
            SingleResult<Integer> minutesResult = validateIntField("minutes", t);
            SingleResult<String> idResult = validateStringField("id", t);

            if (!nameResult.isSuccess()) {
                return result.setMessage(errorPrepend + nameResult.getMessage());
            }
            if (!shortcutResult.isSuccess()) {
                return result.setMessage(errorPrepend + shortcutResult.getMessage());
            }
            if (!minutesResult.isSuccess()) {
                return result.setMessage(errorPrepend + minutesResult.getMessage());
            }
            if (!idResult.isSuccess()) {
                return result.setMessage(errorPrepend + idResult.getMessage());
            }

            String name = nameResult.getData();
            String shortcut = shortcutResult.getData();
            int minutes = minutesResult.getData();
            String uuid = idResult.getData();

            TimeUnit timeUnit = new TimeUnit(name, shortcut, minutes);

            intervalUUIDs.add(UUID.fromString(uuid));
            intervals.add(timeUnit);
        }

        return result.setData(intervals);
    }

    private SingleResult<LocalTime> validateLocalTimeField(String fieldName, JsonNode node) {
        SingleResult<LocalTime> result = new SingleResult<>();
        JsonNode nameNode = node.get(fieldName);
        if (nameNode == null) {
            return result.setMessage("Missing field: \"" + fieldName + "\"");
        }
        SingleResult<Integer> minutesResult = validateIntField("minute", nameNode);
        SingleResult<Integer> hoursResult = validateIntField("hour", nameNode);

        if (!minutesResult.isSuccess() ) {
            return result.setMessage("Local Time Minute field is invalid. " + minutesResult.getMessage());
        }
        if (!hoursResult.isSuccess()) {
            return result.setMessage("Local Time Hour field is invalid. " + hoursResult.getMessage());
        }

        int minute = minutesResult.getData();
        int hour = hoursResult.getData();

        LocalTime startTime = LocalTime.of(hour,minute,0, 0);

        return result.setData(startTime);
    }

    private SingleResult<JsonNode> validateNestedNodeExistence(ArrayList<String> fields, JsonNode node) {
        SingleResult<JsonNode> result = new SingleResult<>();

        for (String field : fields) {
            node = node.get(field);
            if (node == null) {
                return result.setMessage("Missing field: \"" + field + "\"");
            }
        }
        return result.setData(node);
    }

    private SingleResult<ArrayList<Category>> validateCategoriesList(JsonNode rootNode) {
        SingleResult<ArrayList<Category>> result = new SingleResult<>();
        ArrayList<Category> categoriesList = new ArrayList<>();
        JsonNode categoriesNode = rootNode.get("categories");

        if (categoriesNode == null) {
            return result.setMessage("Missing field: 'categories'");
        }

        for (JsonNode c : categoriesNode) {
            SingleResult<String> idResult = validateStringField("id", c);
            if (!idResult.isSuccess()) {
                return result.setMessage(idResult.getMessage());
            }

            String id = idResult.getData();
            UUID categoryUUID = UUID.fromString(id);

            int idIndex = categoryUUIDs.indexOf(categoryUUID);
            if (idIndex == -1) {
                return result.setMessage("Category ID of a template not found.");
            }
            categoriesList.add(_categories.get(idIndex));
        }

        if (categoriesList.isEmpty()) {
            return result.setMessage("Template has no categories.");
        }

        return result.setData(categoriesList);
    }

    private SingleResult<ArrayList<Template>> importTemplates( JsonNode rootNode) {
        SingleResult<ArrayList<Template>> result = new SingleResult<>();

        ArrayList<Template> templates = new ArrayList<>();
        String errorPrepend =  "Error in TimeUnit importing: ";

        JsonNode templateNode = rootNode.get("templates");
        if (templateNode == null) {
            return result.setMessage(errorPrepend + "No node named 'templates' exists.");
        }
        for (JsonNode t : templateNode) {
            SingleResult<String> nameResult = validateStringField("name", t);
            SingleResult<String> detailsResult = validateStringField("details", t);
            SingleResult<LocalTime> timeResult = validateLocalTimeField("startTime", t);

            if (!nameResult.isSuccess()){
                return result.setMessage(errorPrepend + nameResult.getMessage());
            }
            if (!detailsResult.isSuccess()){
                return result.setMessage(errorPrepend + detailsResult.getMessage());
            }
            if (!timeResult.isSuccess()){
                return result.setMessage(errorPrepend + timeResult.getMessage());
            }

            ArrayList<String> idPath = new ArrayList<>();
            idPath.add("interval");
            idPath.add("timeUnit");
            SingleResult<JsonNode> idPathResult = validateNestedNodeExistence(idPath, t);

            if (!idPathResult.isSuccess()) {
                return result.setMessage(errorPrepend + idPathResult.getMessage());
            }

            JsonNode idNode = idPathResult.getData();
            SingleResult<String> idResult = validateStringField("id", idNode);

            String name = nameResult.getData();
            String details = detailsResult.getData();
            LocalTime startTime = timeResult.getData();
            String uuid = idResult.getData();

            UUID targetUUID = UUID.fromString(uuid);
            TimeUnit desiredTimeUnit;

            int idIndex = intervalUUIDs.indexOf(targetUUID);
            if (idIndex == -1) {
                desiredTimeUnit = null;
            }
            else {
                desiredTimeUnit = _intervals.get(idIndex);
            }

            SingleResult<ArrayList<Category>> categoriesResult = validateCategoriesList(t);
            if (!categoriesResult.isSuccess()) {
                return result.setMessage(errorPrepend + categoriesResult.getMessage());
            }

            ArrayList<Category> categoriesList = new ArrayList<>(categoriesResult.getData());

            ArrayList<String> intervalPath = new ArrayList<>();
            intervalPath.add("interval");
            SingleResult<JsonNode> amountPathResult = validateNestedNodeExistence(intervalPath, t);
            if (!amountPathResult.isSuccess()) {
                return result.setMessage(errorPrepend + amountPathResult.getMessage());
            }
            JsonNode amountNode = amountPathResult.getData();
            SingleResult<Integer> amountResult = validateIntField("amount", amountNode);

            if (!amountResult.isSuccess()) {
                return result.setMessage(errorPrepend + amountResult.getMessage());
            }
            int amount = amountResult.getData();

            Template template;
            if (desiredTimeUnit == null) {
                template = new Template(
                        name,
                        details,
                        startTime,
                        amount,
                        categoriesList
                );
            } else {
                template = new Template(
                        name,
                        details,
                        startTime,
                        desiredTimeUnit,
                        amount,
                        categoriesList
                );
            }
            templates.add(template);
        }
        return result.setData(templates);
    }

    private SingleResult<LocalDateTime> validateLocalDateTimeField(String fieldName, JsonNode rootNode) {
        SingleResult<LocalDateTime> result = new SingleResult<>();
        rootNode = rootNode.get(fieldName);
        if (rootNode == null) {
            return result.setMessage("Missing field: " + fieldName);
        }
        SingleResult<Integer> yearResult = validateIntField("year", rootNode);
        SingleResult<Integer> monthResult = validateIntField("month", rootNode);
        SingleResult<Integer> dayResult = validateIntField("day", rootNode);
        SingleResult<Integer> hourResult = validateIntField("hour", rootNode);
        SingleResult<Integer> minuteResult = validateIntField("minute", rootNode);

        if (!yearResult.isSuccess()){
            return  result.setMessage("Invalid year. " + yearResult.getMessage());
        }
        if (!monthResult.isSuccess()){
            return  result.setMessage("Invalid month. " + monthResult.getMessage());
        }
        if (!dayResult.isSuccess()){
            return  result.setMessage("Invalid day. " + dayResult.getMessage());
        }
        if (!hourResult.isSuccess()){
            return  result.setMessage("Invalid hour. " + hourResult.getMessage());
        }
        if (!minuteResult.isSuccess()){
            return  result.setMessage("Invalid minute. " + minuteResult.getMessage());
        }

        int year = yearResult.getData();
        int month = monthResult.getData();
        int day = dayResult.getData();
        int hour = hourResult.getData();
        int minute = minuteResult.getData();

        LocalDateTime startDate = LocalDateTime.of(year, month, day, hour, minute);
        return result.setData(startDate);
    }

    private SingleResult<ArrayList<TodoEvent>> importTodoEvents(JsonNode rootNode){
        JsonNode eventsNode = rootNode.get("events");

        ArrayList<TodoEvent> events = new ArrayList<>();
        String errorPrepend = "Error during event import: ";
        SingleResult<ArrayList<TodoEvent>> result = new SingleResult<>();

        if (eventsNode == null) {
            return result.setMessage(errorPrepend + "No node named 'events' exists.");
        }

        for (JsonNode e : eventsNode) {
            SingleResult<String> nameResult = validateStringField("name", e);
            SingleResult<String> detailsResult = validateStringField("details", e);
            SingleResult<LocalDateTime> startTimeResult = validateLocalDateTimeField("start", e);
            if (!nameResult.isSuccess()) {
                return result.setMessage(errorPrepend + nameResult.getMessage());
            }

            if(!detailsResult.isSuccess()) {
                return result.setMessage(errorPrepend + detailsResult.getMessage());
            }
            if(!startTimeResult.isSuccess()) {
                return result.setMessage(errorPrepend + startTimeResult.getMessage());
            }

            String name = nameResult.getData();
            String details = detailsResult.getData();
            LocalDateTime startDate = startTimeResult.getData();

            ArrayList<String> idPath = new ArrayList<>();
            idPath.add("interval");
            idPath.add("timeUnit");
            SingleResult<JsonNode> idPathResult = validateNestedNodeExistence(idPath, e);
            if (!idPathResult.isSuccess()) {
                return result.setMessage(errorPrepend + idPathResult.getMessage());
            }
            JsonNode idNode = idPathResult.getData();
            SingleResult<String> idResult = validateStringField("id", idNode);

            if (!idResult.isSuccess()) {
                return result.setMessage(errorPrepend + idResult.getMessage());
            }
            String id = idResult.getData();
            UUID targetUUID = UUID.fromString(id);

            TimeUnit desiredTimeUnit;

            ArrayList<String> intervalPath = new ArrayList<>();
            intervalPath.add("interval");
            SingleResult<JsonNode> amountPathResult = validateNestedNodeExistence(intervalPath, e);
            if (!amountPathResult.isSuccess()) {
                return result.setMessage(errorPrepend + amountPathResult.getMessage());
            }
            JsonNode amountNode = amountPathResult.getData();
            SingleResult<Integer> amountResult = validateIntField("amount", amountNode);

            if (!amountResult.isSuccess()) {
                return result.setMessage(errorPrepend + amountResult.getMessage());
            }
            int amount = amountResult.getData();

            int interval = intervalUUIDs.indexOf(targetUUID);
            if (interval == -1) {
                desiredTimeUnit = null;
            }
            else {
                desiredTimeUnit = _intervals.get(interval);
            }

            SingleResult<ArrayList<Category>> categoriesResult = validateCategoriesList(e);

            if (!categoriesResult.isSuccess()) {
                return result.setMessage(errorPrepend + categoriesResult.getMessage());
            }

            ArrayList<Category> categoriesList = new ArrayList<>(categoriesResult.getData());

            SingleResult<Boolean> isDoneResult = validateBooleanField("done", e);
            if (!isDoneResult.isSuccess()){
                return result.setMessage(errorPrepend + isDoneResult.getMessage());
            }


            TodoEvent event;
            if (desiredTimeUnit == null) {
                event = new TodoEvent(
                        name, details, startDate, amount, categoriesList
                );
                event.setDone(isDoneResult.getData());
            } else {
                event = new TodoEvent(
                        name, details, startDate, desiredTimeUnit, amount, categoriesList
                );
                event.setDone(isDoneResult.getData());

            }
            events.add(event);
        }
        return result.setData(events);
    }

    @Override
    public SingleResult<Batch> importBatch(String filePath){
            _categories = new ArrayList<>();
            _intervals = new ArrayList<>();
            _templates = new ArrayList<>();
            _events = new ArrayList<>();
            intervalUUIDs = new ArrayList<>();
            categoryUUIDs = new ArrayList<>();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = null;
            SingleResult<Batch> result = new SingleResult<>();

            try{
                File file = new File(filePath);
                rootNode = objectMapper.readTree(file);
            }
            catch (IOException e){
                return result.setMessage(e.getMessage());
            }

            if (rootNode == null) {
                return result.setMessage("Root node is null.");
            }

            SingleResult<ArrayList<Category>> categoryImportResult;
            SingleResult<ArrayList<TimeUnit>> timeImportResult;
            SingleResult<ArrayList<Template>> templateImportResult;
            SingleResult<ArrayList<TodoEvent>> eventImportResult;

            categoryImportResult = importCategories(rootNode);
            if (!categoryImportResult.isSuccess()) {
                return result.setMessage(categoryImportResult.getMessage());
            }
            _categories = categoryImportResult.getData();

            timeImportResult = importTimeUnits(rootNode);
            if (!timeImportResult.isSuccess()) {
                return result.setMessage(timeImportResult.getMessage());
            }
            _intervals = timeImportResult.getData();

            templateImportResult = importTemplates(rootNode);
            if (!templateImportResult.isSuccess()) {
                return result.setMessage(templateImportResult.getMessage());
            }
            _templates = templateImportResult.getData();

            eventImportResult = importTodoEvents(rootNode);
            if (!eventImportResult.isSuccess()) {
                return result.setMessage(eventImportResult.getMessage());
            }
            _events = eventImportResult.getData();

            Batch resultBatch = new Batch(_categories, _intervals, _templates, _events);
            result.setData(resultBatch);
            return result;
    }
}
