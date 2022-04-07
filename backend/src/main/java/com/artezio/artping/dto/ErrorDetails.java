package com.artezio.artping.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация об ошибках
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {

    /**
     * Время возникновения ошибки
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Сообщение ошибки
     */
    private String message;

    /**
     * Информация о запросе
     */
    private String details;
}
