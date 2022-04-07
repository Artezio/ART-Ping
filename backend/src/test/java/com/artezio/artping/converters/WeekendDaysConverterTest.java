package com.artezio.artping.converters;

import com.artezio.artping.service.mapper.converter.WeekendDaysConverter;
import ma.glasnost.orika.metadata.TypeFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeekendDaysConverterTest {
    private final WeekendDaysConverter converter = new WeekendDaysConverter();

    @Test
    void testConvertTo() {
        String src = "-1;2";
        assertTrue(converter.canConvert(TypeFactory.valueOf(String.class), TypeFactory.valueOf(Integer[].class)));

        Integer[] result = converter.convertTo(src, TypeFactory.valueOf(Integer[].class), null);
        assertEquals(2, result.length);
        assertEquals(-1, result[0]);
        assertEquals(2, result[1]);

        result = converter.convertTo("", TypeFactory.valueOf(Integer[].class), null);
        assertNull(result);
        result = converter.convertTo(null, TypeFactory.valueOf(Integer[].class), null);
        assertNull(result);
    }

    @Test
    void testConvertFrom() {
        Integer[] src = new Integer[]{1,2,3};
        assertTrue(converter.canConvert(TypeFactory.valueOf(Integer[].class), TypeFactory.valueOf(String.class)));

        String result = converter.convertFrom(src, TypeFactory.valueOf(String.class), null);
        assertEquals("1;2;3", result);

        result = converter.convertFrom(null, TypeFactory.valueOf(String.class), null);
        assertEquals("", result);
    }
}
