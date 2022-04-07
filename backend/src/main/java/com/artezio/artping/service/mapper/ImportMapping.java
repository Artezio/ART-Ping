package com.artezio.artping.service.mapper;

import com.artezio.artping.dto.calendar.request.CalendarRequest;
import com.artezio.artping.dto.user.request.UserCreationRequest;
import com.artezio.artping.service.integration.dto.CalendarDto;
import com.artezio.artping.service.integration.dto.EmployeeDto;
import ma.glasnost.orika.MapperFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

@Component
public class ImportMapping implements OrikaMapperFactoryConfigurer {
    @Override
    public void configure(MapperFactory orikaMapperFactory) {
        orikaMapperFactory.classMap(CalendarDto.class, CalendarRequest.class)
                .field("weekendMask", "weekendDays")
                .byDefault()
                .register();

        orikaMapperFactory.classMap(EmployeeDto.class, UserCreationRequest.class)
                .exclude("office")
                .byDefault()
                .register();
    }
}
