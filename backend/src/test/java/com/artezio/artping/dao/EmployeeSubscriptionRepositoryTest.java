package com.artezio.artping.dao;

import com.artezio.artping.data.repository.EmployeeSubscriptionRepository;
import com.artezio.artping.entity.EmployeeSubscription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class EmployeeSubscriptionRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private EmployeeSubscriptionRepository employeeSubscriptionRepository;

    @Test
    public void testCreate() {
        EmployeeSubscription employeeSubscription = new EmployeeSubscription();
        employeeSubscription.setEmployee(createEmployee());
        employeeSubscription = employeeSubscriptionRepository.save(employeeSubscription);

        assertNotNull(employeeSubscription);
        assertNotNull(employeeSubscription.getId());
    }

}
