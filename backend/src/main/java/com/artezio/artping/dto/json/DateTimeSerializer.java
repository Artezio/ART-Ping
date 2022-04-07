package com.artezio.artping.dto.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeSerializer extends StdSerializer<LocalDateTime> {
    private static final long serialVersionUID = 1576856867085481517L;

    public DateTimeSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(LocalDateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
