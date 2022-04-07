package com.artezio.artping.service.mapper;

import com.artezio.artping.controller.request.EmployeesPageFilter;
import com.artezio.artping.controller.request.EmployeesTestsPageFilter;
import com.artezio.artping.data.specification.EmployeeSpecificationCreator;
import com.artezio.artping.service.mapper.converter.StringToRoleConverter;
import com.artezio.artping.service.mapper.converter.StringToUuidConverter;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FilterMapping implements OrikaMapperFactoryConfigurer {

    private static final String STRING_TO_UUID_CONVERTER = "stringToUuidConverter";
    private static final String STRING_TO_ROLE_CONVERTER = "stringToRoleConverter";

    private final StringToRoleConverter stringToRoleConverter;

    @Override
    public void configure(MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(STRING_TO_UUID_CONVERTER, new StringToUuidConverter());
        converterFactory.registerConverter(STRING_TO_ROLE_CONVERTER, stringToRoleConverter);

        mapperFactory.classMap(EmployeesPageFilter.class, EmployeeSpecificationCreator.class)
                .field("officeId", "officeIds[0]")
                .field("projectId", "projectIds[0]")
                .field("searchString", "name")
                .fieldMap("roleName", "roles").converter(STRING_TO_ROLE_CONVERTER).add()
                .byDefault()
                .register();

        mapperFactory.classMap(EmployeesTestsPageFilter.class, EmployeeSpecificationCreator.class)
                .field("officeId", "officeIds[0]")
                .field("projectId", "projectIds[0]")
                .field("searchString", "name")
                .fieldMap("roleName", "roles").converter(STRING_TO_ROLE_CONVERTER).add()
                .field("startPeriod", "startPeriod")
                .byDefault()
                .register();

    }
}
