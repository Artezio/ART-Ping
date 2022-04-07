package com.artezio.artping.data.exceptions;

import java.text.MessageFormat;

/**
 * Ошибка при чтении файла импорта
 */
public class ReadImportFileException extends ArtPingException {
    private static final long serialVersionUID = 1732759941866115495L;

    public ReadImportFileException(String errorMsg) {
        super(MessageFormat.format(ExceptionEnum.READ_IMPORT_FILE.getErrorMessage(), errorMsg));
    }
}
