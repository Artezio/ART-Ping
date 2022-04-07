package com.artezio.artping.data.repository.ext;

import com.artezio.artping.entity.AbstractEntity;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractCustomDao<E extends AbstractEntity> {

    private static final String LOADGRAPH_HINT = "javax.persistence.loadgraph";
    private static final String GRAPH_DELIMETER = ".";

    @PersistenceContext
    protected EntityManager em;

    private final Class<E> entityClass;

    protected AbstractCustomDao(Class<E> entityClass) {
        if (entityClass == null) {
            throw new IllegalArgumentException("entityClass is null in DAO.");
        }
        this.entityClass = entityClass;
    }

    protected EntityManager getEm() {
        return em;
    }

    protected CriteriaBuilder getCriteriaBuilder() {
        return getEm().getCriteriaBuilder();
    }

    protected CriteriaQuery<E> getCriteriaQuery(CriteriaBuilder builder) {
        return builder.createQuery(entityClass);
    }

    protected Root<E> getRoot(CriteriaQuery<E> query) {
        return query.from(entityClass);
    }

}
