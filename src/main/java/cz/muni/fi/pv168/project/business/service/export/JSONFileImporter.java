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
import cz.muni.fi.pv168.project.business.service.export.batch.BatchResult;
import cz.muni.fi.pv168.project.business.service.export.batch.SingleResult;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.business.service.export.batch.Batch;
import cz.muni.fi.pv168.project.business.service.export.batch.BatchImporter;
import cz.muni.fi.pv168.project.business.service.export.format.Format;



public class JSONFileImporter implements BatchImporter {
    ArrayList<Category> categories = new ArrayList<>();
    ArrayList<TimeUnit> intervals = new ArrayList<>();
    ArrayList<Template> templates = new ArrayList<>();
    ArrayList<TodoEvent> events = new ArrayList<>();
    ArrayList<UUID> intervalUUIDs = new ArrayList<>();
    ArrayList<UUID> categoryUUIDs = new ArrayList<>();

    @Override
    public Format getFormat() {
        return new Format("json", new ArrayList<String>(List.of("json")) {
        });
    }

    private SingleResult<Color> validateColor(JsonNode node) {
        SingleResult<Color> result = new SingleResult<>();
        JsonNode colorNode = node.get("color");
        if (colorNode == null) {
            result.setMessage("Category node has no color.");
            return result;
        }
        JsonNode redNode = colorNode.get("r");
        JsonNode greenNode = colorNode.get("g");
        JsonNode blueNode = colorNode.get("b");

        if (redNode == null || greenNode == null || blueNode == null) {
            result.setMessage("Category color is missing a color value.");
            return result;
        }

        if (!redNode.isInt() || !blueNode.isInt() || !greenNode.isInt()){
            result.setMessage("Category color is not an integer.");
            return result;
        }

        int red = redNode.asInt();
        int green = greenNode.asInt();
        int blue = blueNode.asInt();

        if (red < 0 || green < 0 || blue < 0 || red > 255 || green > 255 || blue > 255){
            result.setMessage("Category color is invalid.");
        }
        Color color = new Color(red, green, blue);
        result.setData(color);
        return result;
    }

    private SingleResult<String> validateStringField(String fieldName, JsonNode node) {
        SingleResult<String> result = new SingleResult<>();
        JsonNode nameNode = node.get(fieldName);
        if (nameNode == null) {
            result.setMessage("Missing field: \"" + fieldName + "\"");
            return result;
        }
        String data = nameNode.asText();
        if (data == null || data.isEmpty()) {
            result.setMessage("Field \"" + fieldName + "\" has an invalid value.");
            return result;
        }

        result.setData(data);

        return result;
    }

    private SingleResult<Integer> validateIntField(String fieldName, JsonNode node){
        SingleResult<Integer> result = new SingleResult<>();
        JsonNode nameNode = node.get(fieldName);
        if (nameNode == null) {
            result.setMessage("Missing field: \"" + fieldName + "\"");
            return result;
        }

        if (!nameNode.isInt()) {
            result.setMessage("Field \"" + fieldName + "\" has an invalid value.");
            return result;
        }

        int data = nameNode.asInt();
        result.setData(data);

        return result;
    }

    public boolean importCategories(BatchResult result, JsonNode rootNode) {
        JsonNode categoriesNode = rootNode.get("categories");
        if (categoriesNode == null) {
            result.setSuccess(false);
            result.setMessage("Root node has no categories.");
            return false;
        }
        for (JsonNode c : categoriesNode) {
            SingleResult<Color> colorResult = validateColor(c);
            if (!colorResult.isSuccess()) {
                return propagateFault(colorResult, result);
            }
            SingleResult<String> nameResult = validateStringField("name", c);
            SingleResult<String> idResult = validateStringField("id", c);

            if (!nameResult.isSuccess()) {
                return propagateFault(nameResult, result);
            }
            if(!idResult.isSuccess()){
                return propagateFault(idResult, result);
            }

            String name = nameResult.getData();
            String uuid = idResult.getData();
            Color color = colorResult.getData();

            Category category = new Category(name, color);
            categoryUUIDs.add(UUID.fromString(uuid));
            categories.add(category);
        }
        return true;
    }

    public boolean importTimeUnits(BatchResult result, JsonNode rootNode) {
        JsonNode timeUnitsNode = rootNode.get("timeUnits");
        if (timeUnitsNode == null) {
            result.setSuccess(false);
            result.setMessage("Root node has no timeUnits.");
            return false;
        }

        for (JsonNode t : timeUnitsNode) {

            SingleResult<String> nameResult = validateStringField("name", t);
            SingleResult<String> shortcutResult = validateStringField("shortcut", t);
            SingleResult<Integer> minutesResult = validateIntField("minutes", t);
            SingleResult<String> idResult = validateStringField("id", t);

            if (!nameResult.isSuccess()) {
                return propagateFault(nameResult, result);
            }
            if (!shortcutResult.isSuccess()) {
                return propagateFault(shortcutResult, result);
            }
            if (!minutesResult.isSuccess()) {
                return propagateFault(minutesResult, result);
            }
            if (!idResult.isSuccess()) {
                return propagateFault(idResult, result);
            }

            String name = nameResult.getData();
            String shortcut = shortcutResult.getData();
            int minutes = minutesResult.getData();
            String uuid = idResult.getData();

            TimeUnit timeUnit = new TimeUnit(name, shortcut, minutes);

            intervalUUIDs.add(UUID.fromString(uuid));
            intervals.add(timeUnit);
        }
        return true;
    }

    private SingleResult<LocalTime> validateLocalTimeField(String fieldName, JsonNode node) {
        SingleResult<LocalTime> result = new SingleResult<>();
        JsonNode nameNode = node.get(fieldName);
        if (nameNode == null) {
            result.setMessage("Missing field: \"" + fieldName + "\"");
            return result;
        }
        SingleResult<Integer> minutesResult = validateIntField("minute", nameNode);
        SingleResult<Integer> hoursResult = validateIntField("hour", nameNode);

        if (!minutesResult.isSuccess() || !hoursResult.isSuccess()) {
            result.setMessage("Minute and Hour fields are invalid.");
            return result;
        }


        int minute = minutesResult.getData();
        int hour = hoursResult.getData();

        LocalTime startTime = LocalTime.of(hour,minute,0, 0);

        result.setData(startTime);
        return result;
    }

    private SingleResult<JsonNode> validateNestedNodeExistence(ArrayList<String> fields, JsonNode node) {
        SingleResult<JsonNode> result = new SingleResult<>();

        for (String field : fields) {
            node = node.get(field);
            if (node == null) {
                result.setMessage("Missing field: \"" + field + "\"");
                return result;
            }
        }
        result.setData(node);
        return result;
    }

    private SingleResult<ArrayList<Category>> validateCategoriesList(JsonNode rootNode) {
        SingleResult<ArrayList<Category>> result = new SingleResult<>();
        ArrayList<Category> categoriesList = new ArrayList<>();
        JsonNode categoriesNode = rootNode.get("categories");
        if (categoriesNode == null) {
            result.setMessage("Root node has no categories.");
            return result;
        }

        for (JsonNode c : categoriesNode) {
            SingleResult<String> idResult = validateStringField("id", c);
            if (!idResult.isSuccess()) {
                result.setMessage(idResult.getMessage());
                return result;
            }

            String id = idResult.getData();
            UUID categoryUUID = UUID.fromString(id);

            int idIndex = categoryUUIDs.indexOf(categoryUUID);
            if (idIndex == -1) {
                result.setMessage("Category ID of a template not found.");
                return result;
            }
            categoriesList.add(categories.get(idIndex));
        }

        if (categoriesList.isEmpty()) {
            result.setMessage("Template has no categories.");
            return result;
        }

        result.setData(categoriesList);
        return result;
    }

    private boolean importTemplates(BatchResult result, JsonNode rootNode) {
        JsonNode templateNode = rootNode.get("templates");
        if (templateNode == null) {
            result.setSuccess(false);
            result.setMessage("Root node has no templates.");
            return false;
        }
        for (JsonNode t : templateNode) {
            SingleResult<String> nameResult = validateStringField("name", t);
            SingleResult<String> detailsResult = validateStringField("details", t);
            SingleResult<LocalTime> timeResult = validateLocalTimeField("startTime", t);

            if (!nameResult.isSuccess()){
                return propagateFault(nameResult, result);
            }
            if (!detailsResult.isSuccess()){
                return propagateFault(detailsResult, result);
            }
            if (!timeResult.isSuccess()){
                return propagateFault(timeResult, result);
            }

            ArrayList<String> idPath = new ArrayList<>();
            idPath.add("interval");
            idPath.add("timeUnit");
            SingleResult<JsonNode> idPathResult = validateNestedNodeExistence(idPath, t);

            if (!idPathResult.isSuccess()) {
                return propagateFault(idPathResult, result);
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
                desiredTimeUnit = intervals.get(idIndex);
            }

            SingleResult<ArrayList<Category>> categoriesResult = validateCategoriesList(t);
            if (!categoriesResult.isSuccess()) {
                return propagateFault(categoriesResult, result);
            }

            ArrayList<Category> categoriesList = new ArrayList<>(categoriesResult.getData());

            ArrayList<String> intervalPath = new ArrayList<>();
            intervalPath.add("interval");
            SingleResult<JsonNode> amountPathResult = validateNestedNodeExistence(intervalPath, t);
            if (!amountPathResult.isSuccess()) {
                return propagateFault(amountPathResult, result);
            }
            JsonNode amountNode = amountPathResult.getData();
            SingleResult<Integer> amountResult = validateIntField("amount", amountNode);

            if (!amountResult.isSuccess()) {
                return propagateFault(amountResult, result);
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
        return true;
    }

    private SingleResult<LocalDateTime> validateLocalDateTimeField(String fieldName, JsonNode rootNode) {
        SingleResult<LocalDateTime> result = new SingleResult<>();
        rootNode = rootNode.get(fieldName);
        if (rootNode == null) {
            result.setMessage("Root node has no field: " + fieldName);
            return result;
        }
        SingleResult<Integer> yearResult = validateIntField("year", rootNode);
        SingleResult<Integer> monthResult = validateIntField("month", rootNode);
        SingleResult<Integer> dayResult = validateIntField("day", rootNode);
        SingleResult<Integer> hourResult = validateIntField("hour", rootNode);
        SingleResult<Integer> minuteResult = validateIntField("minute", rootNode);

        if (!(yearResult.isSuccess() && monthResult.isSuccess() && dayResult.isSuccess()
                && hourResult.isSuccess() && minuteResult.isSuccess())) {
            result.setMessage("Invalid start date for event.");
            return result;
        }

        int year = yearResult.getData();
        int month = monthResult.getData();
        int day = dayResult.getData();
        int hour = hourResult.getData();
        int minute = minuteResult.getData();

        LocalDateTime startDate = LocalDateTime.of(year, month, day, hour, minute);
        result.setData(startDate);
        return result;
    }

    private boolean importTodoEvents(BatchResult result, JsonNode rootNode){
        JsonNode eventsNode = rootNode.get("events");
        if (eventsNode == null) {
            result.setSuccess(false);
            result.setMessage("Root node has no events.");
            return false;
        }

        for (JsonNode e : eventsNode) {
            SingleResult<String> nameResult = validateStringField("name", e);
            SingleResult<String> detailsResult = validateStringField("details", e);
            SingleResult<LocalDateTime> startTimeResult = validateLocalDateTimeField("start", e);
            if (!nameResult.isSuccess()) {
                return propagateFault(nameResult, result);
            }

            if(!detailsResult.isSuccess()) {
                return propagateFault(detailsResult, result);
            }
            if(!startTimeResult.isSuccess()) {
                return propagateFault(startTimeResult, result);
            }

            String name = nameResult.getData();
            String details = detailsResult.getData();
            LocalDateTime startDate = startTimeResult.getData();

            ArrayList<String> idPath = new ArrayList<>();
            idPath.add("interval");
            idPath.add("timeUnit");
            SingleResult<JsonNode> idPathResult = validateNestedNodeExistence(idPath, e);
            if (!idPathResult.isSuccess()) {
                return propagateFault(idPathResult, result);
            }
            JsonNode idNode = idPathResult.getData();
            SingleResult<String> idResult = validateStringField("id", idNode);

            if (!idResult.isSuccess()) {
                return propagateFault(idResult, result);
            }
            String id = idResult.getData();
            UUID targetUUID = UUID.fromString(id);

            TimeUnit desiredTimeUnit;

            ArrayList<String> intervalPath = new ArrayList<>();
            intervalPath.add("interval");
            SingleResult<JsonNode> amountPathResult = validateNestedNodeExistence(intervalPath, e);
            if (!amountPathResult.isSuccess()) {
                return propagateFault(amountPathResult, result);
            }
            JsonNode amountNode = amountPathResult.getData();
            SingleResult<Integer> amountResult = validateIntField("amount", amountNode);

            if (!amountResult.isSuccess()) {
                return propagateFault(amountResult, result);
            }
            int amount = amountResult.getData();

            int interval = intervalUUIDs.indexOf(targetUUID);
            if (interval == -1) {
                desiredTimeUnit = null;
            }
            else {
                desiredTimeUnit = intervals.get(interval);
            }

            SingleResult<ArrayList<Category>> categoriesResult = validateCategoriesList(e);

            if (!categoriesResult.isSuccess()) {
                return propagateFault(categoriesResult, result);
            }

            ArrayList<Category> categoriesList = new ArrayList<>(categoriesResult.getData());

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
        return true;
    }

    private <T> boolean propagateFault(SingleResult<T> fault, BatchResult result) {
        result.setSuccess(false);
        result.setMessage(fault.getMessage());
        return false;
    }

    @Override
    public BatchResult importBatch(String filePath){
            categories = new ArrayList<>();
            intervals = new ArrayList<>();
            templates = new ArrayList<>();
            events = new ArrayList<>();
            intervalUUIDs = new ArrayList<>();
            categoryUUIDs = new ArrayList<>();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = null;
            BatchResult result = new BatchResult();

            try{
                File file = new File(filePath);
                rootNode = objectMapper.readTree(file);
            }
            catch (IOException e){
                result.setSuccess(false);
                result.setMessage(e.getMessage());
                return result;
            }

            if (rootNode == null) {
                result.setSuccess(false);
                result.setMessage("Root node is null.");
                return result;
            }

            boolean importResult;

            importResult = importCategories(result, rootNode);
            if (!importResult) {
                return result;
            }

            importResult = importTimeUnits(result, rootNode);
            if (!importResult) {
                return result;
            }

            importResult = importTemplates(result, rootNode);
            if (!importResult) {
                return result;
            }

            importResult = importTodoEvents(result, rootNode);
            if (!importResult) {
                return result;
            }

            result.setSuccess(true);
            Batch resultBatch = new Batch(categories, intervals, templates, events);
            result.setBatch(resultBatch);
            return result;
    }
}
