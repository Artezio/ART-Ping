package com.artezio.artping.dao;

import com.artezio.artping.data.repository.EmployeeRepository;
import com.artezio.artping.data.repository.EmployeeTestRepository;
import com.artezio.artping.entity.EmployeeTest;
import com.artezio.artping.entity.NotificationStatusEnum;
import com.artezio.artping.entity.TestStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class EmployeeTestRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private EmployeeTestRepository employeeTestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testCreate() {
        EmployeeTest employeeTest = new EmployeeTest();
        employeeTest.setEmployee(employeeRepository.save(createEmployee()));
        employeeTest.setStatus(TestStatusEnum.PLANNED);
        employeeTest.setNotification(NotificationStatusEnum.NO_SUBSCRIPTION);

        employeeTest = employeeTestRepository.save(employeeTest);
        assertNotNull(employeeTest);
        assertNotNull(employeeTest.getId());
    }

}
