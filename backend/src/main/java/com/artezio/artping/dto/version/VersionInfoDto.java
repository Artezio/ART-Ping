package com.artezio.artping.dto.version;

import com.artezio.artping.dto.json.DateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Информация о сборке
 */
@Getter
@Setter
@RequiredArgsConstructor
public class VersionInfoDto implements Serializable {
    private static final long serialVersionUID = -8339922904539281339L;

    /**
     * Ветка сборки
     */
    private final String branch;

    /**
     * Хэш коммита, полная версия
     */
    private final String commitHashFull;

    /**
     * Хэш коммита, укороченная версия
     */
    private final String commitHashShort;

    /**
     * Строковое представление даты и времени последнего коммита
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private final LocalDateTime commitDateTime;

    /**
     * Строковое представление даты и времени сборки
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private final LocalDateTime buildDateTime;
}
