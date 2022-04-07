package com.artezio.artping.service.mapper.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateToStringConverter extends BidirectionalConverter<String, LocalDate> {
    private DateTimeFormatter getDateFormat() {
        return DateTimeFormatter.ISO_DATE;
    }

    public LocalDateToStringConverter() {
    }

    @Override
    public LocalDate convertTo(String s, Type<LocalDate> type, MappingContext mappingContext) {
        return LocalDate.parse(s, getDateFormat());
    }

    @Override
    public String convertFrom(LocalDate localDate, Type<String> type, MappingContext mappingContext) {
        return localDate.format(getDateFormat());
    }
}
