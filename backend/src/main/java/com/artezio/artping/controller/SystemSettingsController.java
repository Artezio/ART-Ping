package com.artezio.artping.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.artezio.artping.dto.PublicSystemSettingsDto;
import com.artezio.artping.dto.SystemSettingsDto;
import com.artezio.artping.service.SystemSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;

import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с системными настройками
 */
@RestController
@RequestMapping("/system-settings")
@Api(value = "SystemSettingsController", tags = "Системные настроеки ART-PING")
@RequiredArgsConstructor
public class SystemSettingsController {

    private final SystemSettingsService settingsService;

    /**
     * Получение списка текущих настроек системы
     *
     * @return список системных настроек
     */
    @GetMapping(value = "/list", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Получение списка системных настроек ART-PING",
            response = SystemSettingsDto.class, authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<SystemSettingsDto> getSystemSettings() {
        return ResponseEntity.ok().body(settingsService.getSystemSettings());
    }

    /**
     * Информация о текущем статусе капчи
     *
     * @return статус капчи на странице с проверками
     */
    @GetMapping(value = "/captcha", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Получение списка системных настроек для отображения Captcha ART-PING",
            response = Boolean.class, authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<Boolean> getCaptchaSettings() {
        return ResponseEntity.ok().body(settingsService.getCaptchaSettings());
    }

    /**
     * Изменение системных настроек
     *
     * @param systemSettingsDto новые данные системных настроек
     * @return статус обновления настроек
     */
    @PutMapping(value = "/update", consumes = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Обновление системных настроек ART-PING", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity updateSystemSettings(@Valid @RequestBody SystemSettingsDto systemSettingsDto) {
        settingsService.updateSystemSettings(systemSettingsDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение настройки, связанной с рекомендованным количеством проверок в день
     *
     * @return значение рекомендованных проверок в день
     */
    @GetMapping(value = "/recommended-daily-checks", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Получение системной настройки ART-PING Рекомендованное количество проверок в день",
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<PublicSystemSettingsDto> getRecommendedDailyChecks() {
        return ResponseEntity.ok().body(settingsService.getRecommendedDailyChecks());
    }

}
