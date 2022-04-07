package com.artezio.artping.data.repository;

import com.artezio.artping.entity.project.Project;
import com.artezio.artping.entity.project.ProjectEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProjectEmployeeRepository extends JpaRepository<ProjectEmployee, String> {
    void deleteByProject(Project project);
}
