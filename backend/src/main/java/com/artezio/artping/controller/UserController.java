package com.artezio.artping.controller;

import com.artezio.artping.dto.employee.response.EmployeeAndUserDto;
import com.artezio.artping.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с сущностью пользователя, которая отвечает за авторизацию сотрудника.
 */
@RestController
@RequestMapping(("/user"))
@Api(tags = "Эндпоинты для работы с пользователями")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Получение информации о текущем авторизованном сотруднике через его пользователя
     *
     * @return информация о текущем сотруднике
     */
    @ApiOperation(value = "Получить информацию о текущем пользователе",
            authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(path = "/whoami", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeAndUserDto> whoami() {
        return ResponseEntity.ok().body(userService.getCurrentUserWithTestsFlag());
    }

}
