package com.artezio.artping.entity;

import java.io.Serializable;
import java.util.UUID;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Класс, отвечающий за генерацию первичных ключей в сущностях
 */
public class PrimaryUuidGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o)
            throws HibernateException {
        return UUID.randomUUID();
    }

}
