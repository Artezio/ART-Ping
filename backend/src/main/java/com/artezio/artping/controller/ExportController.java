package com.artezio.artping.controller;

import com.artezio.artping.service.ExportService;
import com.artezio.artping.service.integration.ExportEmployeeTestRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * Контроллер для работы с экспортом данных
 */
@RestController
@RequestMapping(value = "/export")
@Api(tags = "Эндпоинты для работы с экспортом данных")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    /**
     * Экспорт информации о выбранных сотрудниках с проверками за период и статистикой их успешности
     *
     * @param request список сотрудников и период
     * @return информация о сотрудниках с данными о проверках
     */
    @PostMapping
    @ApiOperation(value = "Экспорт данных о проверках сотрудников", response = StreamingResponseBody.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<StreamingResponseBody> fileExport(@RequestBody @Validated ExportEmployeeTestRequest request) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=employeeTests.xlsx")
                .body(outputStream -> exportService.exportData(request, outputStream));
    }
}
