package cz.muni.fi.pv168.project.business.service.export.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.muni.fi.pv168.project.model.Interval;

import java.io.IOException;

public class IntervalSerializer extends JsonSerializer<Interval> {
    @Override
    public void serialize(Interval interval, JsonGenerator gen, SerializerProvider serializers) throws IOException
    {
        gen.writeStartObject();
        gen.writeNumberField("amount", interval.getAmount());

        gen.writeObjectFieldStart("timeUnit");
        gen.writeStringField("id", interval.getTimeUnit().getId().toString());
        gen.writeEndObject();

        gen.writeEndObject();
    }
}