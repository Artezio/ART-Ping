package com.artezio.artping.controller;

import com.artezio.artping.dto.PwdRecoveryRequest;
import com.artezio.artping.service.PwdRecoveryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер для работы с восстановлением пароля пользователя
 */
@Api(tags = "Эндпоинты для работы с восстановлением паролей")
@RestController
@RequestMapping("/recovery")
@RequiredArgsConstructor
@Slf4j
public class PwdRecoveryController {

    private final PwdRecoveryService service;

    /**
     * Создание запроса на восстановление с отправкой на текущую почту сотрудника ключа для восстановления
     *
     * @param request идентификация пользователя по логину, либо почте
     */
    @ApiOperation(value = "Сервис для создания записи на восстановление пароля")
    @PostMapping(path = "init", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public void init(
            @ApiParam(required = true)
            @RequestBody @Validated PwdRecoveryRequest request) {
        if (isNotBlank(request.getLogin())) {
            service.createByLogin(request.getLogin());
        } else if (isNotBlank(request.getEmail())) {
            service.createByEmail(request.getEmail());
        } else {
            log.debug("Empty password recovery request");
        }
    }

    /**
     * Обновление пароля по полученному на почту пользователя ключу
     *
     * @param id       идентификатор запроса на восстановление
     * @param password новый пароль
     * @return статус обновления пароля
     */
    @PutMapping(path = "/{id}", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для обновления пароля")
    public ResponseEntity<Void> update(
            @PathVariable String id,
            @ApiParam(value = "Новый пароль", required = true)
            @RequestBody String password) {
        service.update(id, password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
