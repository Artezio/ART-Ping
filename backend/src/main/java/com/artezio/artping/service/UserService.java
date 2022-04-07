package com.artezio.artping.service;

import com.artezio.artping.dto.employee.response.EmployeeAndUserDto;
import com.artezio.artping.dto.user.request.UserCreationRequest;
import com.artezio.artping.dto.user.response.UserDeletedResponse;
import com.artezio.artping.entity.user.UserEntity;
import com.mchange.rmi.NotAuthorizedException;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    EmployeeAndUserDto registerUser(UserCreationRequest user);

    /**
     * Получение пользователя по его логину
     *
     * @param login логин пользователя
     * @return информация о пользователе
     */
    EmployeeAndUserDto getUserByLogin(String login);

    /**
     * Получение сущности пользователя в виде optional
     *
     * @param login логин пользователя
     * @return сущность пользователя в виде optional
     */
    Optional<UserEntity> getOptionalUserByLogin(String login);

    /**
     * Получение текущего пользователя по токену авторизации
     *
     * @param token ключ авторизации
     * @return информация о пользователе
     * @throws UserPrincipalNotFoundException если сотрудник не найден
     */
    EmployeeAndUserDto getCurrentUser(String token) throws UserPrincipalNotFoundException;

    /**
     * Получение сущности текущего авторизованного пользователя
     *
     * @return текущий пользователь
     * @throws NotAuthorizedException если пользователь не авторизован
     */
    UserEntity getCurrentUserEntity() throws NotAuthorizedException;

    /**
     * Получение всех пользователей в виде сотрудников
     *
     * @return список пользователей
     */
    List<EmployeeAndUserDto> getAll();

    /**
     * Мягкое удаление пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return информация об удаленном пользователе
     */
    UserDeletedResponse markDelete(String id);

    /**
     * Получение информации о сотруднике по текущему авторизованному пользователю
     *
     * @return информация об авторизованном сотруднике
     */
    EmployeeAndUserDto getCurrentUserWithTestsFlag();
}
