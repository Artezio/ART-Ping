package com.artezio.artping.service.mapper;

import com.artezio.artping.dto.calendar.response.CalendarResponse;
import com.artezio.artping.dto.calendar.response.CalendarResponseDto;
import com.artezio.artping.dto.calendar.response.CreatedCalendarResponse;
import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.service.mapper.converter.StringToUuidConverter;
import com.artezio.artping.service.mapper.converter.WeekendDaysConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

@Component
public class CalendarMapping implements OrikaMapperFactoryConfigurer {

    public static final String STRING_TO_UUID_CONVERTER = "stringToUuidConverter";
    public static final String WEEKEND_DAYS_CONVERTER = "weekendDaysConverter";

    @Override
    public void configure(MapperFactory orikaMapperFactory) {
        ConverterFactory converterFactory = orikaMapperFactory.getConverterFactory();

        converterFactory.registerConverter(STRING_TO_UUID_CONVERTER, new StringToUuidConverter());
        converterFactory.registerConverter(WEEKEND_DAYS_CONVERTER, new WeekendDaysConverter());

        orikaMapperFactory.classMap(Calendar.class, CalendarResponseDto.class)
                .constructorB("id", "name", "active", "creationTime", "startWeekDay", "weekendDays", "workHoursFrom", "workHoursTo")
                .fieldMap("id", "id").converter(STRING_TO_UUID_CONVERTER).add()
                .fieldMap("name", "name").add()
                .fieldMap("active", "active").add()
                .fieldMap("creationTime", "creationTime").add()
                .fieldMap("startWeekDay", "startWeekDay").add()
                .fieldMap("weekendDays", "weekendDays").converter(WEEKEND_DAYS_CONVERTER).add()
                .fieldMap("workHoursFrom", "workHoursFrom").add()
                .fieldMap("workHoursTo", "workHoursTo").add()
                .fieldMap("offices{id}", "offices{}").add()
                .register();

        orikaMapperFactory.classMap(Calendar.class, CreatedCalendarResponse.class)
                .constructorB("id", "name", "active", "startWeekDay", "weekendDays", "workHoursFrom", "workHoursTo")
                .fieldMap("id", "id").converter(STRING_TO_UUID_CONVERTER).add()
                .fieldMap("name", "name").add()
                .fieldMap("active", "active").add()
                .fieldMap("startWeekDay", "startWeekDay").add()
                .fieldMap("weekendDays", "weekendDays").converter(WEEKEND_DAYS_CONVERTER).add()
                .fieldMap("workHoursFrom", "workHoursFrom").add()
                .fieldMap("workHoursTo", "workHoursTo").add()
                .register();

        orikaMapperFactory.classMap(Calendar.class, CalendarResponse.class)
                .constructorB("id", "name", "active", "startWeekDay", "weekendDays", "workHoursFrom", "workHoursTo")
                .fieldMap("id", "id").converter(STRING_TO_UUID_CONVERTER).add()
                .fieldMap("name", "name").add()
                .fieldMap("active", "active").add()
                .fieldMap("startWeekDay", "startWeekDay").add()
                .fieldMap("weekendDays", "weekendDays").converter(WEEKEND_DAYS_CONVERTER).add()
                .fieldMap("workHoursFrom", "workHoursFrom").add()
                .fieldMap("workHoursTo", "workHoursTo").add()
                .register();
    }
}
