package com.artezio.artping.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * ДТО для поиска записей по строке
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Setter @Getter
public class SearchStringFilter extends PageFilterRequest {
    private static final long serialVersionUID = -5560516463313183434L;

    /**
     * Строка поиска
     */
    @ApiModelProperty(notes = "Строка поиска", name = "searchString")
    private String searchString = "";
}
