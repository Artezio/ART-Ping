package com.artezio.artping.service.mapper;

import com.artezio.artping.dto.employee.response.EmployeeAndUserDto;
import com.artezio.artping.dto.employee.response.EmployeeDetailedResponse;
import com.artezio.artping.dto.employee.response.EmployeeDto;
import com.artezio.artping.dto.employee.response.EmployeeResponse;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.service.mapper.converter.StringToUuidConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapping implements OrikaMapperFactoryConfigurer {

    public static final String STRING_TO_UUID_CONVERTER = "stringToUuidConverter";

    @Override
    public void configure(MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();

        converterFactory.registerConverter(STRING_TO_UUID_CONVERTER, new StringToUuidConverter());

        mapperFactory.classMap(Employee.class, EmployeeAndUserDto.class)
                .fieldMap("projectEmployees{project.id}", "projects{}").add()
                .fieldMap("projectPMs{project.id}", "managedProjects{}").add()
                .fieldMap("roles{code}", "roles{}").add()
                .fieldMap("managedOffices{id}", "managedOffices{}").add()
                .fieldMap("offices{id}", "offices{}").add()
                .fieldMap("baseOffice.id", "baseOffice").converter(STRING_TO_UUID_CONVERTER).add()
                .fieldMap("user.active", "active").add()
                .byDefault()
                .register();

        mapperFactory.classMap(Employee.class, EmployeeDto.class)
                .field("baseOffice.id", "baseOffice")
                .field("calendar.id", "calendarId")
                .field("roles{code}", "roles{}")
                .byDefault()
                .register();

        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .field("baseOffice.id", "baseOffice")
                .field("calendar.id", "calendarId")
                .field("projectEmployees{project.id}", "projects{}")
                .field("roles{code}", "roles{}")
                .byDefault()
                .register();

        mapperFactory.classMap(Employee.class, EmployeeDetailedResponse.class)
                .field("projectEmployees{project.id}", "projects{}")
                .field("projectPMs{project.id}", "managedProjects{}")
                .field("managedOffices{id}", "managedOffices{}")
                .field("offices{id}", "offices{}")
                .field("calendar.customCalendar", "isCustomCalendar")
                .field("baseOffice.id", "baseOffice")
                .field("calendar.id", "calendarId")
                .field("roles{code}", "roles{}")
                .byDefault()
                .register();
    }
}
