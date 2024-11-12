package cz.muni.fi.pv168.project.business.service.export.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Interval;

import java.io.IOException;

public class CategorySerializer extends JsonSerializer<Category> {
    @Override
    public void serialize(Category category, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", category.getId().toString());
        gen.writeEndObject();
    }
}