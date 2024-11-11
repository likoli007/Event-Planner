package cz.muni.fi.pv168.project.service.export.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

public class DateTimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime dateTime, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("year", dateTime.getYear());
        gen.writeNumberField("month", dateTime.getMonthValue());
        gen.writeNumberField("day", dateTime.getDayOfMonth());
        gen.writeNumberField("hour", dateTime.getHour());
        gen.writeNumberField("minute", dateTime.getMinute());
        gen.writeEndObject();
    }
}