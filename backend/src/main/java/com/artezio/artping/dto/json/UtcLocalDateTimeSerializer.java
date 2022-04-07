package com.artezio.artping.dto.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UtcLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

    public static final ZoneId systemDefault = ZoneId.systemDefault();

    public UtcLocalDateTimeSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(localDateTime.atZone(systemDefault).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
