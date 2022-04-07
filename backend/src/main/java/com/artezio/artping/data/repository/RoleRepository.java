package com.artezio.artping.data.repository;

import com.artezio.artping.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Репозиторий для получения данных о ролях
 */
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Поиск роли по коду
     *
     * @param code код роли
     *
     * @return найденная в БД роль
     */
    Role findByCode(String code);

    /**
     * Поиск роли по названию
     *
     * @param name название роли
     *
     * @return найденная в БД роль
     */
    Role findByName(String name);

}
