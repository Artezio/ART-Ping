package com.artezio.artping.controller;

import com.artezio.artping.dto.version.VersionInfoDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер для получения информации о сборке
 */
@RestController
@RequestMapping("/version")
@Api(tags = "Эндпоинт для отображения информации о сборке")
@RequiredArgsConstructor
public class VersionController {

    @Value("${git.branch}")
    private String branch;

    @Value("${git.commit.id}")
    private String commitFull;

    @Value("${git.commit.id.abbrev}")
    private String commitShort;

    @Value("${git.commit.time}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private LocalDateTime commitDateTime;

    @Value("${git.build.time}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private LocalDateTime buildDateTime;

    /**
     * Получение информации о текущей сборке
     *
     * @return информация о сборке
     */
    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Получение информации о ветке сборки", response = VersionInfoDto.class)
    public ResponseEntity<VersionInfoDto> getVersion() {
        return ResponseEntity.ok().body(
                new VersionInfoDto(branch, commitFull, commitShort, commitDateTime, buildDateTime)
        );
    }
}
