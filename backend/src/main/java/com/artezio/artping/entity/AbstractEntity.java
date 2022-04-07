package com.artezio.artping.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Абстрактный родитель всех сущностей в проекте
 */
@MappedSuperclass
@NoArgsConstructor
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ID = "id";

    /**
     * Уникальный идентификатор
     */
    @Id
    @GenericGenerator(name = "guid-generator", strategy = "com.artezio.artping.entity.PrimaryUuidGenerator")
    @GeneratedValue(generator = "guid-generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractEntity that = (AbstractEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}