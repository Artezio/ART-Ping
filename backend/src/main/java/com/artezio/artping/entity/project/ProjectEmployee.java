package com.artezio.artping.entity.project;

import com.artezio.artping.entity.user.Employee;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.NoArgsConstructor;

/**
 * Сущность для связи сотрудников и проектов
 */
@Entity
@DiscriminatorValue("false")
@NoArgsConstructor
public class ProjectEmployee extends AbstractProjectEmployee {

    private static final long serialVersionUID = 1L;

    public ProjectEmployee(Employee employee, Project project) {
        this.setProject(project);
        this.setEmployee(employee);
    }
}
