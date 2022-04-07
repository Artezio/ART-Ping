package com.artezio.artping.service.mapper.converter;

import java.util.UUID;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class StringToUuidConverter extends BidirectionalConverter<String, UUID> {

    @Override
    public UUID convertTo(String source, Type<UUID> destinationType, MappingContext mappingContext) {
        return UUID.fromString(source);
    }

    @Override
    public String convertFrom(UUID source, Type<String> destinationType, MappingContext mappingContext) {
        return source.toString();
    }
}
