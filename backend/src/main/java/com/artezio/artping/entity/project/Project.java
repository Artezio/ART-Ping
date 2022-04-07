package com.artezio.artping.entity.project;

import com.artezio.artping.entity.AbstractEntity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность "Проект"
 */
@Entity
@Table(name = "project", schema = "art_ping")
@Getter
@Setter
public class Project extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Название проекта
     */
    @Column(nullable = false)
    private String name;

    /**
     * Признак активности
     */
    @Column(name = "is_active")
    private Boolean active;

    /**
     * Список сотрудников проекта
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "project", orphanRemoval = true)
    private List<ProjectEmployee> projectEmployees;

    /**
     * Список менеджеров проекта
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "project", orphanRemoval = true)
    private List<ProjectPM> projectManagers;

}