package com.artezio.artping.data.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractSpecificationCreator<T> {

    public static final String LIKE_FORMAT = "%%%s%%";

    protected List<Predicate> allPredicates = new ArrayList<>();

    protected String prepareForLike(String value) {
        return String.format(LIKE_FORMAT, value);
    }

    protected <E> CriteriaBuilder.In<E> in(CriteriaBuilder criteriaBuilder, Expression<? extends E> var, Collection<E> values) {
        CriteriaBuilder.In<E> inClause = criteriaBuilder.in(var);
        values.stream().filter(Objects::nonNull).forEach(inClause::value);
        return inClause;
    }

    protected Predicate[] toVarArgs(List<Predicate> predicates) {
        return predicates.toArray(new Predicate[0]);
    }


    public abstract Specification<T> create();
}
