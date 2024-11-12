package cz.muni.fi.pv168.project.business.service.export.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.awt.*;
import java.io.IOException;

public class ColorSerializer extends JsonSerializer<Color> {
    @Override
    public void serialize(Color color, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("r", color.getRed());
        gen.writeNumberField("g", color.getGreen());
        gen.writeNumberField("b", color.getBlue());
        gen.writeEndObject();
    }
}