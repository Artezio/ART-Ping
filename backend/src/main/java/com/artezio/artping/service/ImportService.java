package com.artezio.artping.service;

import com.artezio.artping.data.exceptions.ArtPingException;
import com.artezio.artping.dto.integration.ImportResponse;
import com.artezio.artping.service.integration.IXlsxAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис для работы с импортом данных
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportService {

    private final IXlsxAdapter xlsIntegration;

    /**
     * Импорт данных из xlsx файла в БД
     *
     * @param mfile xlsx файл
     * @return сообщение о возникших ошибках при импорте, если их не возникло - null
     */
    public ImportResponse importData(MultipartFile mfile) {
        ImportResponse importResponse = new ImportResponse();

        try {
            importResponse.setMessage(xlsIntegration.process(mfile));
        } catch (ArtPingException e) {
            log.error(e.getMessage());
            importResponse.setMessage(e.getMessage());
        }

        return importResponse;
    }
}
