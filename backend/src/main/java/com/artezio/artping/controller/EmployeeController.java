package com.artezio.artping.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.artezio.artping.config.security.model.AuthenticationResponse;
import com.artezio.artping.controller.request.EmployeesPageFilter;
import com.artezio.artping.dto.employee.response.EmployeeAndUserDto;
import com.artezio.artping.dto.employee.request.UpdateEmployeeRequest;
import com.artezio.artping.dto.employee.response.EmployeeDetailedResponse;
import com.artezio.artping.dto.employee.response.EmployeeDto;
import com.artezio.artping.dto.user.request.UserCreationRequest;
import com.artezio.artping.dto.user.response.UserDeletedResponse;
import com.artezio.artping.service.EmployeeService;
import com.artezio.artping.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.UUID;

import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с сотрудниками
 */
@RestController
@RequestMapping("/employee")
@Api(tags = "Эндпоинты для работы с сотрудниками")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;

    /**
     * Создание нового сотрудника и пользователя для его авторизации
     *
     * @param userCreationRequest поля с информацией о сотруднике
     * @return информация сотрудника с id
     */
    @PostMapping
    @ApiOperation(value = "Сервис для создания пользователей", response = AuthenticationResponse.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<EmployeeAndUserDto> createUser(@RequestBody UserCreationRequest userCreationRequest) {
        EmployeeAndUserDto employeeAndUserDto = userService.registerUser(userCreationRequest);
        return ResponseEntity.ok(employeeAndUserDto);
    }

    /**
     * Отметка сотрудника как неактивного
     *
     * @param id идентификатор сотрудника
     * @return информация о сотруднике
     */
    @DeleteMapping(path = "/{id}")
    @ApiOperation(value = "Пометить пользователя удаленным", response = AuthenticationResponse.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<UserDeletedResponse> markDeleted(@PathVariable String id) {
        return ResponseEntity.ok().body(userService.markDelete(id));
    }

    /**
     * Получение списка сотрудников постранично с учётом роли текущего сотрудника
     *
     * @param filter содержит информацию о пагинации, фильтрации и сортировке
     * @return постраничный список сотрудников
     */
    @GetMapping(produces = {APPLICATION_JSON_VALUE}, path = "/list")
    @ApiOperation(value = "Сервис для получения списка сотрудников по фильтру с учётом ролей текущего сотрудника",
            response = PageData.class, authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<PageData<EmployeeAndUserDto>> getAllByFilterForCurrentEmployee(EmployeesPageFilter filter) {
        PageData<EmployeeAndUserDto> page = employeeService.getEmployeesPage(filter);
        return ResponseEntity.ok()
                .header(PageData.HEADER_TOTAL_COUNT, String.valueOf(page.totalCount))
                .body(page);
    }

    /**
     * Получение списка сотрудников постранично без учёта ролей
     *
     * @param filter содержит информацию о пагинации, фильтрации и сортировке
     * @return постраничный список сотрудников
     */
    @GetMapping(produces = {APPLICATION_JSON_VALUE}, path = "/all")
    @ApiOperation(value = "Сервис для получения списка сотрудников по фильтру без учёта ролей текущего сотрудника",
            response = PageData.class, authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<PageData<EmployeeAndUserDto>> getAllByFilter(EmployeesPageFilter filter) {
        PageData<EmployeeAndUserDto> page = employeeService.getEmployeesPageFromAllOffices(filter);
        return ResponseEntity.ok()
                .header(PageData.HEADER_TOTAL_COUNT, String.valueOf(page.totalCount))
                .body(page);
    }

    /**
     * Получение подробной информации о сотруднике по id
     *
     * @param id идентификатор сотрудника
     * @return подробная информация о сотруднике
     */
    @GetMapping(path = "/{id}", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для поиска сотрудника по идентификатору", response = EmployeeDetailedResponse.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<EmployeeDetailedResponse> findEmployeeById(
            @ApiParam(name = "id", value = "Идентификатор сотрудника", required = true)
            @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok().body(employeeService.findById(id));
    }

    /**
     * Изменении информации о сотруднике
     *
     * @param id       идентификатор сотрудника
     * @param employee новая информация о сотруднике
     * @return обновлённая информация о сотруднике
     */
    @PutMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для изменения сотрудника", response = EmployeeDto.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<EmployeeAndUserDto> updateEmployee(@PathVariable String id,
                                                             @ApiParam @RequestBody UpdateEmployeeRequest employee) {
        EmployeeAndUserDto savedEmployee = employeeService.update(id, employee);
        return ResponseEntity.ok().body(savedEmployee);
    }
}
