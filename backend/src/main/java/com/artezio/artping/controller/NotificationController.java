package com.artezio.artping.controller;

import com.artezio.artping.dto.NoticeDto;
import com.artezio.artping.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер для работы с уведомлениями
 */
@RestController
@RequestMapping("/notification")
@Api(tags = "Эндпоинты для работы с уведомлениями")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Получение текущих уведомлений для текущего сотрудника
     *
     * @return список уведомлений
     */
    @GetMapping(value = "/actual", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Получение текущих актуальных уведомлений", response = NoticeDto.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<List<NoticeDto>> getActual() {
        return ResponseEntity.ok().body(notificationService.getActual());
    }

    /**
     * Пометить уведомление как прочитанное
     *
     * @param id идентификатор уведомления
     * @return обновленное уведомление
     */
    @PatchMapping(value = "/{id}/markRead", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Отметить уведомление как прочитанное", response = NoticeDto.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<NoticeDto> markRead(@PathVariable String id) {
        return ResponseEntity.ok().body(notificationService.markRead(id));
    }
}
