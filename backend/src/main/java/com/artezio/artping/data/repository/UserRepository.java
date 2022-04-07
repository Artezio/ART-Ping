package com.artezio.artping.data.repository;

import com.artezio.artping.entity.user.UserEntity;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для получения данных о пользователях
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Поиск пользовтаеля по логину
     *
     * @param login логин для поиска
     *
     * @return найденный пользователь
     */
    Optional<UserEntity> findByLogin(String login);

    /**
     * Посик активного пользователя по логину
     *
     * @param login логин для поиска
     *
     * @return найденный пользователь
     */
    Optional<UserEntity> findByLoginAndActiveTrue(String login);

}
