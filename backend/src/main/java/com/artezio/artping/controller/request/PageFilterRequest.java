package com.artezio.artping.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;

/**
 * ДТО для постраничного фильтра
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageFilterRequest implements Serializable {
    private static final long serialVersionUID = 2885657459087956330L;

    /**
     * Смещение страницы
     */
    @PositiveOrZero(message = "Допустимый номер страницы")
    @ApiModelProperty(notes = "Допустимый номер страницы", name = "pageNumber", required = true)
    private Integer pageNumber = 0;

    /**
     * Ограничение размера страницы
     */
    @ApiModelProperty(notes = "Допустимое количество записей в выборке", name = "pageSize")
    private Integer pageSize;

    /**
     * Список полей для сортировки
     */
    @ApiModelProperty(notes = "Список полей для сортировки", name = "sorting")
    private SortDto[] sorting;

}
