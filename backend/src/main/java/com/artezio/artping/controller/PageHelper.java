package com.artezio.artping.controller;

import com.artezio.artping.controller.request.PageFilterRequest;
import com.artezio.artping.controller.request.SortDto;
import com.artezio.artping.service.SystemSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Компонент для преобразования фильтров с пагинацией в спинговый пейджинг
 */
@Component
@AllArgsConstructor
public class PageHelper {

    private final SystemSettingsService systemSettingsService;

    /**
     * Преобразование фильтра с пагинацией в спринговый пэйджинг
     *
     * @param filter     исходный фильтр
     * @param fieldNames опциональный список полей сотрировки
     * @return спринговый интерфейс пагинации
     */
    public Pageable processFilter(PageFilterRequest filter, SortDto... fieldNames) {
        final Sort sort = createSorting(filter, fieldNames);
        final Integer pageSize = filter.getPageSize() != null ? filter.getPageSize() : systemSettingsService.getDefaultPageSize();
        //
        PageRequest pageable;
        if (sort != null) {
            pageable = PageRequest.of(filter.getPageNumber(), pageSize, sort);
        } else {
            pageable = PageRequest.of(filter.getPageNumber(), pageSize);
        }
        //
        return pageable;
    }

    private static Sort createSorting(PageFilterRequest filter, SortDto... fieldNames) {
        if (filter == null || fieldNames == null || fieldNames.length == 0) {
            return null;
        }
        //
        Iterator<SortDto> it = Arrays.stream(fieldNames).iterator();
        Sort result = it.next().convert();
        while (it.hasNext()) {
            result = result.and(it.next().convert());
        }
        //
        return result;
    }

}
