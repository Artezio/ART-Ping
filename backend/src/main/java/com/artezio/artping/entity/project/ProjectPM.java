package com.artezio.artping.entity.project;

import com.artezio.artping.entity.user.Employee;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.NoArgsConstructor;

/**
 * Сущность для связи ПМ-ов и проектов
 */
@Entity
@DiscriminatorValue("true")
@NoArgsConstructor
public class ProjectPM extends AbstractProjectEmployee {

    private static final long serialVersionUID = 1L;

    public ProjectPM(Employee employee, Project project) {
        this.setProject(project);
        this.setEmployee(employee);
    }
}
