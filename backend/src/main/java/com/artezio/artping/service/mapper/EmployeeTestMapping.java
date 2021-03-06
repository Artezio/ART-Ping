package com.artezio.artping.service.mapper;

import com.artezio.artping.dto.EmployeeTestDto;
import com.artezio.artping.entity.EmployeeTest;
import ma.glasnost.orika.MapperFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

@Component
public class EmployeeTestMapping implements OrikaMapperFactoryConfigurer {
    @Override
    public void configure(MapperFactory orikaMapperFactory) {
        orikaMapperFactory.classMap(EmployeeTest.class, EmployeeTestDto.class)
                .byDefault()
                .register();
    }
}
