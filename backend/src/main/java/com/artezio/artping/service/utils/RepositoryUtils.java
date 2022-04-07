package com.artezio.artping.service.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public class RepositoryUtils {

    public static <T> List<T> saveAllIfNotEmpty(JpaRepository<T, UUID> repository, Collection<T> entities) {
        if (!entities.isEmpty()) {
            return repository.saveAll(entities);
        }
        return Collections.emptyList();
    }

    public static <T> void deleteAllIfNotEmpty(JpaRepository<T, UUID> repository, Collection<T> entities) {
        if (!entities.isEmpty()) {
            repository.deleteAll(entities);
        }
    }
}
