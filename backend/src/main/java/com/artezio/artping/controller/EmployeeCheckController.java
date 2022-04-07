package com.artezio.artping.controller;

import com.artezio.artping.controller.request.EmployeesTestsPageFilter;
import com.artezio.artping.dto.CancelEmployeeAutoChecksRequest;
import com.artezio.artping.dto.CancelEmployeeListAutoChecksRequest;
import com.artezio.artping.dto.EmployeePlannedTestsRequest;
import com.artezio.artping.dto.EmployeeTestsResponseDto;
import com.artezio.artping.dto.employee.response.employeeDayInfo.EmployeeWithDayInfo;
import com.artezio.artping.service.EmployeeTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер для работы с проверками сотрудников
 */
@RestController
@RequestMapping("/employee-checks")
@Api(tags = "Эндпоинты для работы с запланированными тестами")
@RequiredArgsConstructor
public class EmployeeCheckController {

    private final EmployeeTestService employeeTestService;

    /**
     * Постраничный список сотрудников с данными об их проверках на определенный период
     *
     * @param request содержит информацию о периоде, пагинации, фильтрации и сортировке
     * @return постраничный список сотрудников с данными о проверках
     * @throws SecurityException возникает в случае, если сотрудник не имеет доступ к данному методу
     */
    @ApiOperation(value = "Получение сотрудников с запланированными тестами на определенный период", response = PageData.class,
            authorizations = {@Authorization(value = "apiKey")})
    @Validated
    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<PageData<EmployeeWithDayInfo>> findAllEmployeesWithDayInfoForPeriod(
            @ApiParam(value = "Период и данные для фильтрации", required = true)
            @Valid EmployeesTestsPageFilter request) throws SecurityException {
        PageData<EmployeeWithDayInfo> page = employeeTestService.findAllEmployeesByCriteria(request);
        return ResponseEntity.ok()
                .header(PageData.HEADER_TOTAL_COUNT, String.valueOf(page.totalCount))
                .body(page);
    }

    /**
     * Планировка проверок переданным сотрудникам на определенный период
     *
     * @param request идентификаторы сотрудников, кол-во назначаемых проверок и период
     * @return сообщение о возникших ошибках во время планировки, если их не возникло - null
     * @throws SecurityException возникает в случае, если сотрудник не имеет доступ к данному методу
     */
    @ApiOperation(value = "Добавление в БД запланированных проверок для сотрудников на определенный период",
            response = EmployeeTestsResponseDto.class, authorizations = {@Authorization(value = "apiKey")})
    @Validated
    @ResponseStatus(CREATED)
    @PostMapping(path = "/planForEmployees", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<EmployeeTestsResponseDto> planForEmployees(
            @ApiParam(value = "Идентификаторы сотрудников, начальная и конечная дата", required = true)
            @Valid @RequestBody EmployeePlannedTestsRequest request) throws SecurityException {
        return ResponseEntity.ok().body(employeeTestService.planForEmployees(request));
    }

    /**
     * Проверка переданных сотрудников прямо сейчас
     *
     * @param employeeIds идентификаторы сотрудников
     * @return сообщение о возникших ошибках во время создания, если их не возникло - null
     * @throws SecurityException возникает в случае, если сотрудник не имеет доступ к данному методу
     */
    @ApiOperation(value = "Добавление в БД для выбранных сотрудников проверки на данный момент",
            response = EmployeeTestsResponseDto.class, authorizations = {@Authorization(value = "apiKey")})
    @Validated
    @ResponseStatus(CREATED)
    @PostMapping(path = "/checkNow", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<EmployeeTestsResponseDto> checkNow(
            @ApiParam(value = "Идентификаторы проверяемых сотрудников", required = true)
            @Valid @RequestBody List<UUID> employeeIds) throws SecurityException {
        return ResponseEntity.ok().body(employeeTestService.setCheckNowTestToEmployees(employeeIds));
    }

    /**
     * Отмена проверок сотруднику на определенный день
     *
     * @param request идентификатор сотрудника и дата, на которую производится отмена
     * @return сообщение о возникших ошибках во время создания, если их не возникло - null
     * @throws SecurityException возникает в случае, если сотрудник не имеет доступ к данному методу
     */
    @ApiOperation(value = "Отмена запланированных проверок сотрудника на переданную дату",
            response = EmployeeTestsResponseDto.class, authorizations = {@Authorization(value = "apiKey")})
    @Validated
    @ResponseStatus(CREATED)
    @PostMapping(path = "/cancelChecksForEmployee", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<EmployeeTestsResponseDto> cancelChecksForEmployee(
            @ApiParam(value = "Идентификатор сотрудника и выбранная дата", required = true)
            @Valid @RequestBody CancelEmployeeAutoChecksRequest request) throws SecurityException {
        return ResponseEntity.ok().body(
                employeeTestService.cancelEmployeesAutoChecks(Collections.singletonList(request.getId()),
                        request.getDate(), request.getDate()));
    }

    /**
     * Отмена проверок переданному списку сотрудников на определенный период
     *
     * @param request список сотрудников и период
     * @return сообщение о возникших ошибках во время создания, если их не возникло - null
     * @throws SecurityException возникает в случае, если сотрудник не имеет доступ к данному методу
     */
    @ApiOperation(value = "Отмена запланированных проверок сотрудникам на переданный период",
            response = EmployeeTestsResponseDto.class, authorizations = {@Authorization(value = "apiKey")})
    @Validated
    @ResponseStatus(CREATED)
    @PostMapping(path = "/cancelEmployeesChecks", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<EmployeeTestsResponseDto> cancelEmployeesChecks(
            @ApiParam(value = "Идентификаторы сотрудников и выбранный период", required = true)
            @Valid @RequestBody CancelEmployeeListAutoChecksRequest request) throws SecurityException {
        return ResponseEntity.ok().body(employeeTestService.cancelEmployeesAutoChecks(request.getIds(),
                request.getStartDate(), request.getEndDate()));
    }
}
