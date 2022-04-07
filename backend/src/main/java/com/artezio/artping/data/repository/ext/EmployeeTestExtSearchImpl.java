package com.artezio.artping.data.repository.ext;

import com.artezio.artping.entity.EmployeeTest;
import java.time.ZonedDateTime;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public class EmployeeTestExtSearchImpl extends AbstractCustomDao<EmployeeTest> implements EmployeeTestExtSearch {

    protected EmployeeTestExtSearchImpl() {
        super(EmployeeTest.class);
    }

    @Override
    public Collection<EmployeeTest> findForPeriod(UUID id, ZonedDateTime startDate, ZonedDateTime endDate) {
        CriteriaBuilder cb = getCriteriaBuilder();
        CriteriaQuery<EmployeeTest> query = getCriteriaQuery(cb);
        Root<EmployeeTest> root = getRoot(query);

        Predicate predicate = cb.and(cb.between(root.get("startTime"), startDate, endDate));
        predicate = cb.and(predicate, cb.equal(root.get("employee").get("id"), id));
        query.where(predicate);

        return em.createQuery(query).getResultList();
    }

}
