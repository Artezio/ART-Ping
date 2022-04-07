package com.artezio.artping.converters;

import com.artezio.artping.service.mapper.converter.StringToUuidConverter;
import ma.glasnost.orika.metadata.TypeFactory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StringToUuidConverterTest {
    private final StringToUuidConverter converter = new StringToUuidConverter();

    @Test
    void testConvertTo() {
        String strUuid = "578151d5-e85f-4109-80f6-ab940d004a55";
        assertTrue(converter.canConvert(TypeFactory.valueOf(String.class), TypeFactory.valueOf(UUID.class)));

        UUID result = converter.convertTo(strUuid, TypeFactory.valueOf(UUID.class), null);
        assertEquals(strUuid, result.toString());
    }

    @Test
    void testConvertFrom() {
        UUID uuid = UUID.randomUUID();
        assertTrue(converter.canConvert(TypeFactory.valueOf(UUID.class), TypeFactory.valueOf(String.class)));

        String result = converter.convertFrom(uuid, TypeFactory.valueOf(String.class), null);
        assertEquals(uuid.toString(), result);
    }
}
