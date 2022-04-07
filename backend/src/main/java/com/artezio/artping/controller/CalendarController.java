package com.artezio.artping.controller;

import com.artezio.artping.controller.request.SearchStringFilter;
import com.artezio.artping.dto.calendar.CalendarDto;
import com.artezio.artping.dto.calendar.request.CalendarRequest;
import com.artezio.artping.dto.calendar.request.UpdateCalendarRequest;
import com.artezio.artping.dto.calendar.response.CalendarResponse;
import com.artezio.artping.dto.calendar.response.CalendarResponseDto;
import com.artezio.artping.dto.calendar.response.CreatedCalendarResponse;
import com.artezio.artping.service.CalendarService;
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
 * Контроллер для работы с календарями
 */
@RestController
@RequestMapping("/calendars")
@Api(tags = "Эндпоинты для работы с календарями")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    /**
     * Создание нового календаря
     *
     * @param newCalendar объект с информацией о календаре
     * @return объект созданного календаря, содержащий id
     */
    @PostMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для создания календаря", response = CalendarResponse.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<CreatedCalendarResponse> createCalendar(
            @RequestBody @Validated @ApiParam CalendarRequest newCalendar) {
        CreatedCalendarResponse save = calendarService.save(newCalendar);
        return ResponseEntity.ok().body(save);
    }

    /**
     * Редактирование информации о календаре
     *
     * @param id          идентификатор календаря
     * @param newCalendar объект с информацией о календаре
     * @return объект изменённого календаря
     */
    @PutMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для изменения календаря", response = CalendarResponse.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<CalendarDto> updateCalendar(@PathVariable String id,
                                                      @ApiParam @RequestBody UpdateCalendarRequest newCalendar) {
        CalendarDto savedCalendar = calendarService.update(newCalendar, id);
        return ResponseEntity.ok().body(savedCalendar);
    }

    /**
     * Получение календарей в постраничном виде
     *
     * @param filter содержит информацию о пагинации, фильтрации и сортировке
     * @return список календарей в постраничном виде
     */
    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для получения календарей постранично", response = PageData.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<PageData<CalendarResponseDto>> get(SearchStringFilter filter) {
        PageData<CalendarResponseDto> page = calendarService.getByFilter(filter);
        return ResponseEntity.ok()
                .header(PageData.HEADER_TOTAL_COUNT, String.valueOf(page.totalCount))
                .body(page);
    }

    /**
     * Подробная информация о календаре по id
     *
     * @param id идентификатор календаря
     * @return подробная информация о календаре
     */
    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для получения инфо о календаре по id", response = CalendarResponse.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<CalendarResponse> getCalendarById(@PathVariable String id) {
        CalendarResponse calendar = calendarService.getResponseById(id);
        return ResponseEntity.ok().body(calendar);
    }

    /**
     * Отметка календаря как неактивный, при условии, что он не содержится в сотрудниках или офисах
     *
     * @param id идентификатор календаря
     * @return информация о неактивном календаре
     */
    @DeleteMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Пометить календарь удалённым", response = CalendarResponse.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<CalendarResponseDto> markDeleted(@PathVariable String id) {
        CalendarResponseDto savedCalendar = calendarService.markDelete(id);
        return ResponseEntity.ok().body(savedCalendar);
    }

    /**
     * Получение информации о календаре с событиями за переданный год
     *
     * @param id   идентификатор календаря
     * @param year за какой год получаем события
     * @return информация о календаре с событиями за год
     */
    @GetMapping(value = "/{id}/{year}", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для получения инфо о календаре по id за определенный год",
            response = CalendarResponse.class, authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<CalendarResponse> getCalendarByIdAndYearEvents(@PathVariable String id,
                                                                         @PathVariable Integer year) {
        return ResponseEntity.ok().body(calendarService.getCalendarByIdAndYear(id, year));
    }

}
