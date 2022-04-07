package com.artezio.artping.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.artezio.artping.dto.integration.ImportResponse;
import com.artezio.artping.service.ImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Контроллер для работы с импортом данных в БД
 */
@RestController
@RequestMapping(value = "/import", consumes = "multipart/form-data")
@Api(tags = "Эндпоинты для работы с импортом данных")
@RequiredArgsConstructor
public class ImportController {

    public final ImportService importService;

    /**
     * Импорт данных в БД через файл
     *
     * @param file файл с данными импорта
     * @return информация об ошибках, возникших при импорте
     */
    @PostMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Импорт данных из файла", response = ImportResponse.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<ImportResponse> fileImport(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok().body(importService.importData(file));
    }
}
