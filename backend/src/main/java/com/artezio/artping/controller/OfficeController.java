package com.artezio.artping.controller;

import com.artezio.artping.controller.request.SearchStringFilter;
import com.artezio.artping.dto.office.OfficeDto;
import com.artezio.artping.dto.office.request.CreateOfficeRequest;
import com.artezio.artping.dto.office.response.OfficeResponse;
import com.artezio.artping.dto.office.request.UpdateOfficeRequest;
import com.artezio.artping.service.OfficeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер для работы с офисами
 */
@RestController
@RequestMapping("/offices")
@Api(tags = "Эндпоинты для работы с офисами")
@RequiredArgsConstructor
public class OfficeController {

    private final OfficeService officeService;

    /**
     * Создание нового офиса
     *
     * @param office объект с информацией об офисе
     * @return объект с информацией офиса с id созданной сущности
     */
    @PostMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для создания офиса", response = OfficeResponse.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<OfficeResponse> createOffice(
            @RequestBody @ApiParam @Validated CreateOfficeRequest office) {
        return ResponseEntity.ok().body(officeService.create(office));
    }

    /**
     * Получение списка офисов постранично
     *
     * @param filter содержит информацию о пагинации, фильтрации и сортировке
     * @return постраничный список офисов
     */
    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для получения офисов постранично", response = PageData.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<PageData<OfficeDto>> get(SearchStringFilter filter) {
        PageData<OfficeDto> page = officeService.getByFilter(filter);
        return ResponseEntity.ok()
                .header(PageData.HEADER_TOTAL_COUNT, String.valueOf(page.totalCount))
                .body(page);
    }

    /**
     * Редактирование информации об офисе
     *
     * @param office информация, содержащая id необходимого офиса и поля
     * @return информация об измененном офисе
     */
    @PutMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для редактирования офиса", response = OfficeDto.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<OfficeResponse> updateOffice(@ApiParam @Validated
                                                       @RequestBody UpdateOfficeRequest office) {
        OfficeResponse update = officeService.update(office, office.getId());
        return ResponseEntity.ok().body(update);
    }

    /**
     * Удаление выбранного офиса, если в нём не содержатся сотрудники
     *
     * @param id идентификатор офиса
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Сервис для удаления офиса", authorizations = {@Authorization(value = "apiKey")})
    public void deleteOffice(@ApiParam @PathVariable String id) {
        officeService.deleteById(id);
    }

    /**
     * Получение информации офиса по id
     *
     * @param officeId идентификатор офиса
     * @return информация об офисе
     */
    @GetMapping(value = "/{officeId}", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для получения офиса по id", response = OfficeDto.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<OfficeDto> getOfficeById(@ApiParam @PathVariable String officeId) {
        return ResponseEntity.ok().body(officeService.findById(officeId));
    }
}
