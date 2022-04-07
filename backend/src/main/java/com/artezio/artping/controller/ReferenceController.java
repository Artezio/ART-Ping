package com.artezio.artping.controller;

import com.artezio.artping.dto.ReferenceResponse;
import com.artezio.artping.service.ReferenceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер для работы со справочниками
 */
@RestController
@RequestMapping("/references")
@Api(tags = "Эндпоинты для работы со справочными данными")
@RequiredArgsConstructor
public class ReferenceController {

    private final ReferenceService referenceService;

    /**
     * Получение информации об выбранном справочнике
     *
     * @param refkey ключ, определяющий необходимый справочник
     * @return полный список выбранных справочных данных
     */
    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для получения справочных данных",
            response = ReferenceResponse.class, authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<List<ReferenceResponse>> get(@ApiParam @RequestParam String refkey) {
        return ResponseEntity.ok().body(referenceService.getReference(refkey));
    }
}
