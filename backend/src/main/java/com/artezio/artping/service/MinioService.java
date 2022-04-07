package com.artezio.artping.service;

import com.artezio.artping.config.minio.MinioConfigProperties;
import com.artezio.artping.service.utils.MathUtils;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Сервис для геймификации (получения картинок)
 */
@Service
@AllArgsConstructor
public class MinioService {
    public final static String GAMING_PATH = "/gaming/";

    private MinioClient minioClient;
    private OfficeService officeService;

    private MinioConfigProperties minioProp;

    /**
     * Получение случайного файла для сотрудника по его офису
     *
     * @param officeId идентификатор офиса
     * @return случайный файл
     */
    public Item getRandomObject(String officeId) {
        String path = GAMING_PATH + officeService.findEntityById(officeId).getName() + "/";
        List<Item> set = getNotDirs(path);
        if (set.isEmpty()) {
            set = getNotDirs(GAMING_PATH + "default.png");
        }

        int ind = MathUtils.getRandomNumber(0, set.size() - 1);
        return set.get(ind);
    }

    /**
     * Получение файла по его имени
     *
     * @param objectName имя файла
     * @return содержимое файла
     */
    public InputStream getObjectInputStream(String objectName) {
        try {
            return minioClient.getObject(minioProp.getBucketName(), objectName);
        } catch (XmlParserException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | IOException | InvalidKeyException | ErrorResponseException | InternalException | InvalidResponseException e) {
            throw new RuntimeException("Error while fetching files in Minio", e);
        }
    }

    /**
     * Получение файлов и каталогов по переданному адресу
     *
     * @param path выбранный каталог
     * @return элементы в каталоге
     */
    public List<Item> getAny(String path) {
        return getObjectSet(path, false, item -> true);
    }

    /**
     * Получение только файлов в переданном адресе
     *
     * @param path выбранный каталог
     * @return файлы в каталоге
     */
    public List<Item> getNotDirs(String path) {
        return getObjectSet(path, false, item -> !item.isDir());
    }

    /**
     * Получение только каталогов в переданном адресе
     *
     * @param path выбранный каталог
     * @return каталоги в выбранном адресе
     */
    public List<Item> getDirs(String path) {
        return getObjectSet(path, false, Item::isDir);
    }

    private List<Item> getObjectSet(String path, boolean recursive, Predicate<? super Item> filter) {
        return getItems(minioClient.listObjects(minioProp.getBucketName(), path, recursive), filter);
    }

    private List<Item> getItems(Iterable<Result<Item>> myObjects, Predicate<? super Item> filter) {
        return StreamSupport
                .stream(myObjects.spliterator(), true)
                .map(itemResult -> {
                    try {
                        return itemResult.get();
                    } catch (InvalidBucketNameException |
                            NoSuchAlgorithmException |
                            InsufficientDataException |
                            IOException |
                            InvalidKeyException |
                            XmlParserException |
                            InvalidResponseException |
                            ErrorResponseException |
                            InternalException e) {
                        throw new RuntimeException("Error while parsing list of objects", e);
                    }
                })
                .filter(filter)
                .collect(Collectors.toList());
    }
}
