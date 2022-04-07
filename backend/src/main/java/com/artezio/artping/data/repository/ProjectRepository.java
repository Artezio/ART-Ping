package com.artezio.artping.data.repository;

import com.artezio.artping.entity.project.Project;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с проектами
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findAllByActive(Boolean active);

    /**
     * Поиск по вхождению в название проекта
     *
     * @param searchString строка поиска
     * @param pageRqst     информация о странице
     * @return коллекция найденных проектов
     */
    Page<Project> findByActiveTrueAndNameContainingIgnoreCase(String searchString, Pageable pageRqst);

    /**
     * Поиск по вхождению в названия выбранных проектов
     *
     * @param searchString строка поиска
     * @param projects     идентификаторы проектов
     * @param pageable     информация о странице
     * @return коллекция найденных проектов
     */
    Page<Project> findByActiveTrueAndNameContainingIgnoreCaseAndIdIn(String searchString,
                                                                     Collection<UUID> projects,
                                                                     Pageable pageable);

    @Query("select p from Project p where p.active = 'true' and p.id in (select projectId from AbstractProjectEmployee where employeeId in :ids)")
    List<Project> findProjectsByActiveTrueAndEmployeeIdsIn(Iterable<UUID> ids);

    Optional<Project> findByNameIgnoreCase(String name);
}
