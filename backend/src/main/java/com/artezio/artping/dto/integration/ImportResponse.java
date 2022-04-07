package com.artezio.artping.dto.integration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * ДТО для информации об ошибках при импорте
 */
@Data
@Getter
@Setter
public class ImportResponse {

    /**
     * Информация об ошибках
     */
    private String message;

}
