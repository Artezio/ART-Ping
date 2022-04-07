package com.artezio.artping.service.integration;

import com.artezio.artping.data.exceptions.ArtPingException;

public interface IAdapter<S> {

    /**
     * Импорт данных
     *
     * @param toImport входной файл с данными
     * @return ок - в случае успешного приема данных
     */
    String process(S toImport) throws ArtPingException;
}
