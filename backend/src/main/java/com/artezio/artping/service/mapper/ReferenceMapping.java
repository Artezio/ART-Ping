package com.artezio.artping.service.mapper;

import com.artezio.artping.dto.ReferenceResponse;
import com.artezio.artping.dto.calendar.response.CalendarResponseDto;
import com.artezio.artping.dto.employee.response.EmployeeAndUserDto;
import com.artezio.artping.dto.office.OfficeDto;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

@Component
public class ReferenceMapping implements OrikaMapperFactoryConfigurer {

    @Override
    public void configure(MapperFactory mapperFactory) {
        mapperFactory.classMap(OfficeDto.class, ReferenceResponse.class)
                .field("timezone", "description")
                .byDefault()
                .register();

        mapperFactory.classMap(EmployeeAndUserDto.class, ReferenceResponse.class)
                .customize(
                        new CustomMapper<EmployeeAndUserDto, ReferenceResponse>() {
                            @Override
                            public void mapAtoB(EmployeeAndUserDto a, ReferenceResponse b, MappingContext context) {
                                b.setName(a.getLastName() + " " + a.getFirstName());
                            }
                        }
                )
                .field("login", "description")
                .byDefault()
                .register();

        mapperFactory.classMap(CalendarResponseDto.class, ReferenceResponse.class)
                .customize(
                        new CustomMapper<CalendarResponseDto, ReferenceResponse>() {
                            @Override
                            public void mapAtoB(CalendarResponseDto a, ReferenceResponse b, MappingContext context) {
                                b.setDescription(a.getWorkHoursFrom() + " - " + a.getWorkHoursTo());
                            }
                        }
                )
                .byDefault()
                .register();
    }
}
