package com.artezio.artping.controller;

import com.artezio.artping.dto.EmployeeLastTestInfo;
import com.artezio.artping.service.EmployeeTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для альтернативной работы с проверками сотрудников (без firebase)
 */
@RestController
@RequestMapping("/firebase")
@Api(tags = "Контроллер для проверки наличия теста на текущий момент")
@RequiredArgsConstructor
public class FirebaseStubController {

    private final EmployeeTestService employeeTestService;

    /**
     * Получение последней проверки сотрудника
     *
     * @return информация о последней проверке сотрудника
     */
    @ApiOperation(value = "Проверка на наличие теста", authorizations = {@Authorization(value = "apiKey")})
    @Validated
    @GetMapping("/stub")
    public EmployeeLastTestInfo getLastCurrentEmployeeCheck() {
        return employeeTestService.getLastCurrentEmployeeCheck();
    }
}
