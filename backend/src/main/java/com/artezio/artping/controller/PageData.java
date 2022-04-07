package com.artezio.artping.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Контейнер для страничного представления данных
 *
 * @param <E> тип данных
 */
@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageData<E> {

    public static final String HEADER_TOTAL_COUNT = "X-Total-Count";

    public final Collection<E> list;
    public final long totalCount;

}
