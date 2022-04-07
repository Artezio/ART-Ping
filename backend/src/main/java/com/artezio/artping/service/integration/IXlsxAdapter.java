package com.artezio.artping.service.integration;

import com.artezio.artping.data.exceptions.ArtPingException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Интерфейс для импорта XLSX
 */
public interface IXlsxAdapter extends IAdapter<MultipartFile> {

    /**
     * Импорт данных из XLSX файла
     *
     * @param mfile входной файл с данными
     *
     * @return ок - в случае успешного приема данных
     */
    String process(MultipartFile mfile) throws ArtPingException;

}
