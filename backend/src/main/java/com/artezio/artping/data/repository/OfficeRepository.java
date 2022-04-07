package com.artezio.artping.data.repository;

import com.artezio.artping.entity.office.Office;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с офисами
 */
public interface OfficeRepository extends JpaRepository<Office, UUID> {

    /**
     * Поиск по вхождению в название офиса
     *
     * @param searchString строка поиска
     * @param pageRqst информация о странице
     * @return коллекция найденных офисов
     */
    Page<Office> findByNameContainingIgnoreCase(String searchString, Pageable pageRqst);

    /**
     * Поиск по вхождению в название офиса
     *
     * @param searchString строка поиска
     * @param ids коллекция идентификаторов офисов
     * @param pageRqst информация о странице
     * @return коллекция найденных офисов
     */
    Page<Office> findByNameContainingIgnoreCaseAndIdIn(String searchString, Collection<UUID> ids, Pageable pageRqst);

    Optional<Office> findByNameIgnoreCase(String name);

    @Query("select o.name from Office o")
    List<String> findAllOfficeNames();

}
