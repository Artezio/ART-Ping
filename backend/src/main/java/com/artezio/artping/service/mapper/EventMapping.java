package com.artezio.artping.service.mapper;

import com.artezio.artping.dto.calendar.request.EventRequest;
import com.artezio.artping.dto.calendar.request.UpdateEventRequest;
import com.artezio.artping.dto.calendar.response.EventResponse;
import com.artezio.artping.entity.calendar.Event;
import com.artezio.artping.service.mapper.converter.LocalDateToStringConverter;
import com.artezio.artping.service.mapper.converter.StringToUuidConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

@Component
public class EventMapping implements OrikaMapperFactoryConfigurer {

    private static final String STR_TO_UUID = "stringToUuidConverter";
    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";

    @Override
    public void configure(MapperFactory orikaMapperFactory) {
        ConverterFactory converterFactory = orikaMapperFactory.getConverterFactory();

        converterFactory.registerConverter(STR_TO_UUID, new StringToUuidConverter());
        converterFactory.registerConverter("dateConverter1", new LocalDateToStringConverter());

        orikaMapperFactory.classMap(Event.class, EventResponse.class)
                .fieldMap("id", "id").converter(STR_TO_UUID).add()
                .fieldMap("date", "date").converter("dateConverter1").add()
                .field("type", "type")
                .field(DESCRIPTION, TITLE)
                .register();

        orikaMapperFactory.classMap(EventRequest.class, Event.class)
                .field("date", "date")
                .field("type", "type")
                .field(TITLE, DESCRIPTION)
                .register();

        orikaMapperFactory.classMap(UpdateEventRequest.class, Event.class)
                .fieldMap("id", "id").converter(STR_TO_UUID).add()
                .field("type", "type")
                .field("date", "date")
                .field(TITLE, DESCRIPTION)
                .register();
    }
}
