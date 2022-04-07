package com.artezio.artping.service;

import com.artezio.artping.config.security.AuthenticationService;
import com.artezio.artping.data.exceptions.*;
import com.artezio.artping.data.repository.UserRepository;
import com.artezio.artping.dto.employee.response.EmployeeAndUserDto;
import com.artezio.artping.dto.user.request.UserCreationRequest;
import com.artezio.artping.dto.user.response.UserDeletedResponse;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.UserEntity;
import com.mchange.rmi.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с сущностями пользователя, которые предназначены для авторизации и тесно связаны с сотрудником
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final MapperFacade mapper;
    private final UserRepository repository;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder encoder;
    @Autowired(required = false)
    private EmployeeService employeeService;

    /**
     * Получение информации о сотруднике по его логину
     *
     * @param login логин пользователя
     * @return информация о сотруднике
     */
    @Override
    public EmployeeAndUserDto getUserByLogin(String login) {
        return mapper.map(getOptionalUserByLogin(login).orElseThrow(NoEmployeesFoundException::new), EmployeeAndUserDto.class);
    }

    /**
     * Получение optional сущности пользователя по логину
     *
     * @param login логин пользователя
     * @return optional сущность пользователя
     */
    @Override
    @Transactional
    public Optional<UserEntity> getOptionalUserByLogin(String login) {
        return repository.findByLoginAndActiveTrue(login);
    }

    /**
     * Получение информации о текущем сотруднике по ключу авторизации
     *
     * @param token ключ авторизации
     * @return информация о текущем сотруднике
     * @throws NoSuchUserException пользователь не найден
     */
    @Override
    @Transactional
    public EmployeeAndUserDto getCurrentUser(String token) throws NoSuchUserException {
        User currentUser = authenticationService.getCurrentUser(token);
        UserEntity userEntity = repository.findByLoginAndActiveTrue(currentUser.getUsername())
                .orElseThrow(NoSuchUserException::new);
        return mapper.map(userEntity, EmployeeAndUserDto.class);
    }

    /**
     * Получение сущности текущего пользователя
     *
     * @return сущность текущего пользователя
     * @throws NotAuthorizedException пользователь не авторизован
     */
    @Override
    @Transactional
    public UserEntity getCurrentUserEntity() throws NotAuthorizedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal == "anonymousUser") {
            throw new NotAuthorizedException("You are anonymousUser now");
        }
        User user = (User) principal;
        String userLogin = user.getUsername();
        return repository.findByLoginAndActiveTrue(userLogin).orElseThrow(() -> new UserNotFoundByLoginException(userLogin));
    }

    /**
     * Создание сущностей пользователя и сотрудника
     *
     * @param user информация о сотруднике
     * @return информация о созданном сотруднике
     */
    @Transactional
    public EmployeeAndUserDto registerUser(UserCreationRequest user) {
        if (repository.findByLogin(user.getLogin()).isPresent()) {
            throw new UserWithThisLoginAlreadyExist();
        }
        user.setLogin(user.getLogin().trim());
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        UserEntity map = mapper.map(user, UserEntity.class);
        map.setActive(true);

        UserEntity save = repository.save(map);

        Employee employee = employeeService.createEmployee(user, save);
        map.setEmployee(employee);

        EmployeeAndUserDto employeeAndUserDto = mapper.map(employee, EmployeeAndUserDto.class);
        employeeAndUserDto.setLogin(save.getLogin());
        return employeeAndUserDto;
    }

    /**
     * Получение всех пользователей
     *
     * @return список пользователей
     */
    @Override
    public List<EmployeeAndUserDto> getAll() {
        return mapper.mapAsList(repository.findAll(), EmployeeAndUserDto.class);
    }

    /**
     * Отметка пользователя как неактивного и отмена запланированных проверок
     *
     * @param id идентификатор пользователя
     * @return информация о неактивном пользователе
     */
    @Override
    @Transactional
    public UserDeletedResponse markDelete(String id) {
        Employee employee = employeeService.findEmployeeById(UUID.fromString(id));
        UserEntity userEntity = employee.getUser();
        userEntity.setActive(false);
        employeeService.cancelEmployeeTestsAfterTime(employee.getId(), ZonedDateTime.now());
        return mapper.map(repository.save(userEntity), UserDeletedResponse.class);
    }

    /**
     * Получение текущего сотрудника
     *
     * @return текущий сотрудник
     */
    @Override
    @Transactional
    public EmployeeAndUserDto getCurrentUserWithTestsFlag() {
        Employee employee = employeeService.getCurrentEmployee();
        if (employee == null) {
            throw new SecurityException("Сотрудник не найден");
        }
        return mapper.map(employee, EmployeeAndUserDto.class);
    }
}
