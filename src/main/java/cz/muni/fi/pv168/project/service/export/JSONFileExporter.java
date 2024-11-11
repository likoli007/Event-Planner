package cz.muni.fi.pv168.project.service.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.service.export.batch.Batch;
import cz.muni.fi.pv168.project.service.export.batch.BatchExporter;
import cz.muni.fi.pv168.project.service.export.format.Format;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONFileExporter implements BatchExporter {
    @Override
    public void exportBatch(Batch batch, String filePath) throws IOException {
        //FileWriter file = new FileWriter(filePath);
        //BufferedWriter bf = new BufferedWriter(file);
        File file = new File(filePath);

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode rootNode = mapper.createObjectNode();

        // Add each list under its respective key
        rootNode.set("categories", mapper.valueToTree(batch.categories()));
        rootNode.set("timeUnits", mapper.valueToTree(batch.timeUnits()));
        rootNode.set("templates", mapper.valueToTree(batch.templates()));
        rootNode.set("events", mapper.valueToTree(batch.events()));

        // Write the root object to the file
        mapper.writeValue(file, rootNode);

    }

    @Override
    public Format getFormat() {
        return new Format("json", new ArrayList<String>(List.of("json")) {
        });
    }
}
