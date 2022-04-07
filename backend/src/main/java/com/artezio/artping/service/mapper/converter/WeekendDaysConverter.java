package com.artezio.artping.service.mapper.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.util.StringUtils;

import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class WeekendDaysConverter extends BidirectionalConverter<String, Integer[]> {

    @Override
    public Integer[] convertTo(String str, Type<Integer[]> type, MappingContext mappingContext) {
        if (isBlank(str)) return null;

        return Stream.of(str.split(";"))
                .mapToInt(Integer::parseInt)
                .boxed().toArray(Integer[]::new);
    }

    @Override
    public String convertFrom(Integer[] integers, Type<String> type, MappingContext mappingContext) {
        return StringUtils.arrayToDelimitedString(integers, ";");
    }
}
