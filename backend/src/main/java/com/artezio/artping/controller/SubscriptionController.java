package com.artezio.artping.controller;

import com.artezio.artping.controller.request.SubscriptionAddRequestDto;
import com.artezio.artping.controller.request.SubscriptionDeleteRequestDto;
import com.artezio.artping.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с подписками сотрудников
 */
@RestController
@RequestMapping("/subscription")
@Api(tags = "Сервис для подписок")
@RequiredArgsConstructor
public class SubscriptionController {

    private final EmployeeService employeeService;

    /**
     * Добавление подписки сотруднику
     *
     * @param userAgent                 информация о клиенте
     * @param subscriptionAddRequestDto идентификатор подписки firebase
     * @return статус создания подписки
     */
    @PostMapping(value = "/add")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Добавить подписку текущему пользователю", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity addSubscription(@ApiParam(value = "Объект с идентификатором подписки", hidden = true)
                                          @RequestHeader(value = "User-Agent", required = false) String userAgent,
                                          @ApiParam(value = "Объект с идентификатором подписки", required = true)
                                          @RequestBody SubscriptionAddRequestDto subscriptionAddRequestDto) {
        employeeService.addSubscription(subscriptionAddRequestDto, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Перевод подписки в неактивный статус
     *
     * @param subscriptionDeleteRequestDto идентификатор подписки firebase
     * @return статус операции перевода подписки
     */
    @PostMapping(value = "/delete")
    @ApiOperation(value = "Пометить подписку как не валидную", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity deleteSubscription(@ApiParam(value = "Объект с идентификатором подписки", required = true)
                                             @RequestBody SubscriptionDeleteRequestDto subscriptionDeleteRequestDto) {
        employeeService.deleteSubscription(subscriptionDeleteRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
