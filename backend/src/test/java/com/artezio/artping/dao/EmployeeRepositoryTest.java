package com.artezio.artping.dao;

import com.artezio.artping.data.repository.EmployeeRepository;
import com.artezio.artping.data.repository.RoleRepository;
import com.artezio.artping.data.repository.UserRepository;
import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.Role;
import com.artezio.artping.entity.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class EmployeeRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        Role admin = new Role();
        admin.setCode("admin");
        admin.setCode("admin");

        when(roleRepository.findByCode("admin")).thenReturn(admin);
    }

    @Test
    void testCreate() {
        Employee employee = createEmployee();
        employee.getRoles().add(roleRepository.findByCode(RoleEnum.ADMIN.getCode()));
        employee = employeeRepository.save(employee);
        UserEntity user = createUser(employee);
        user = userRepository.save(user);

        assertNotNull(employee);
        assertNotNull(employee.getId());
        assertNotNull(employee.getRoles());
        assertEquals(1, employee.getRoles().size());
        assertNotNull(user);
        assertNotNull(user.getId());
    }

    @Test
    void createEmployeeWithHROffices() {
        Office office = createOffice();
        Employee employee = createEmployee();
        employee.getOffices().add(office);
        employee = employeeRepository.save(employee);

        assertNotNull(employee);
        assertEquals(1, employee.getOffices().size());
    }
}
