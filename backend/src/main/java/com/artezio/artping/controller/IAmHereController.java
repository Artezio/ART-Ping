package com.artezio.artping.controller;

import com.artezio.artping.dto.PointDto;
import com.artezio.artping.service.EmployeeTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с подтвержением нахождения сотрудника на рабочем месте
 */
@RestController
@RequestMapping("/iamhere")
@Api(tags = "Сервисы для подтверждения присутствия")
@RequiredArgsConstructor
public class IAmHereController {

    private final EmployeeTestService employeeTestService;

    /**
     * Обновление статуса проверки пользователя
     *
     * @param point     геоданные клиента
     * @param userAgent информация о клиенте
     * @return статус обновления проверки
     */
    @ApiOperation(value = "Обновление статуса проверки на ПРОЙДЕНА", authorizations = {@Authorization(value = "apiKey")})
    @Validated
    @PutMapping("/check")
    public ResponseEntity responseEmployeeTests(@ApiParam(value = "Геоданные клиента", required = true)
                                                @RequestBody PointDto point,
                                                @ApiParam(value = "Заголовок с данными о системе пользователя", hidden = true)
                                                @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        employeeTestService.responseToEmployeeTests(point, userAgent);
        return ResponseEntity.ok().build();
    }

    /**
     * Подтверждение получения проверки клиентом
     *
     * @param subscribeId идентификатор подписки firebase
     * @return статус передачи клиентом подтверждения получения проверки
     */
    @GetMapping(path = "/notification/{subscribeId}")
    @ApiOperation(value = "Устанавливает статус уведомления для проверки на ПОЛУЧЕНО",
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity receivedNotification(
            @ApiParam(name = "subscribeId", value = "Идентификатор подписки", required = true)
            @PathVariable(name = "subscribeId") String subscribeId) {
        employeeTestService.changeTestStatusNotificationToReceived(subscribeId);
        return ResponseEntity.ok().build();
    }

}
