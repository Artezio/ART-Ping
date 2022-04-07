package com.artezio.artping.dto.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeToLocalTimeSerializer extends StdSerializer<ZonedDateTime> {

    public static final ZoneId systemDefault = ZoneId.systemDefault();

    public ZonedDateTimeToLocalTimeSerializer() {
        super(ZonedDateTime.class);
    }

    @Override
    public void serialize(ZonedDateTime time, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(time.format(DateTimeFormatter.ISO_LOCAL_TIME));
    }
}
