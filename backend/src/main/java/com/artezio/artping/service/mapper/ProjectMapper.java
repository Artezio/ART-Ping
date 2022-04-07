package com.artezio.artping.service.mapper;

import com.artezio.artping.dto.ProjectDto;
import com.artezio.artping.entity.project.Project;
import com.artezio.artping.entity.project.ProjectEmployee;
import com.artezio.artping.entity.project.ProjectPM;
import ma.glasnost.orika.MapperFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper implements OrikaMapperFactoryConfigurer {

    @Override
    public void configure(MapperFactory mapperFactory) {
        mapperFactory.classMap(ProjectEmployee.class, ProjectDto.class)
                .fieldAToB("project.id", "id")
                .fieldAToB("project.name", "name")
                .register();

        mapperFactory.classMap(ProjectPM.class, ProjectDto.class)
                .fieldAToB("project.id", "id")
                .fieldAToB("project.name", "name")
                .register();

        mapperFactory.classMap(Project.class, ProjectDto.class)
                .field("projectManagers{employee.id}", "pmIds{}")
                .field("projectEmployees{employee.id}", "employeeIds{}")
                .byDefault()
                .register();
    }
}
