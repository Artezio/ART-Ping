package com.artezio.artping.controller;

import com.artezio.artping.dto.EmployeeTestDto;
import com.artezio.artping.dto.EmployeeTestForPeriodRequest;
import com.artezio.artping.service.EmployeeTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер для работы с информацией о проверках сотрудников
 */
@RestController
@RequestMapping("/tests")
@Api(tags = "Эндпоинты для работы с проверками сотрудников")
@RequiredArgsConstructor
public class EmployeeTestController {

    private final EmployeeTestService employeeTestService;

    /**
     * Получение сотрудника с информацией о проверках
     *
     * @param request идентификатор сотрудника и период
     * @return информация о сотруднике и его проверках
     * @throws SecurityException возникает в случае, если пользователь не имеет доступ к этому методу
     */
    @ApiOperation(value = "Получение запланированных тестов сотрудника на определенный период",
            response = EmployeeTestDto[].class, authorizations = {@Authorization(value = "apiKey")})
    @Validated
    @ResponseStatus(CREATED)
    @PostMapping(path = "/employeeTestForPeriod", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<List<EmployeeTestDto>> getEmployeeTestByIdForPeriod(
            @ApiParam(value = "Идентификатор сотрудника и период", required = true)
            @Validated @RequestBody EmployeeTestForPeriodRequest request) throws SecurityException {
        return ResponseEntity.ok().body(employeeTestService.getEmployeeTestForPeriod(request));
    }
}
