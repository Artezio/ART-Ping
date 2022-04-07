package com.artezio.artping.dao;

import com.artezio.artping.data.repository.PwdRecoveryRepository;
import com.artezio.artping.entity.PwdRecovery;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PwdRecoveryRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PwdRecoveryRepository repository;

    @Test
    void testCreate() {
        PwdRecovery recovery = new PwdRecovery();
        recovery.setEmployee(createEmployee());
        recovery.setActive(Boolean.TRUE);
        recovery.setCreated(LocalDate.now());
        recovery = repository.save(recovery);

        assertNotNull(recovery);
        assertNotNull(recovery.getId());
        assertNotNull(recovery.getCreated());
        assertEquals(Boolean.TRUE, recovery.getActive());
    }

    @Test
    void testUpdate() {
        PwdRecovery recovery = new PwdRecovery();
        recovery.setEmployee(createEmployee());
        recovery.setActive(Boolean.TRUE);
        recovery.setCreated(LocalDate.now());
        recovery = repository.save(recovery);

        assertNotNull(recovery);
        assertNotNull(recovery.getId());
        assertEquals(Boolean.TRUE, recovery.getActive());

        recovery.setActive(Boolean.FALSE);
        recovery = repository.save(recovery);

        assertNotNull(recovery);
        assertNotNull(recovery.getId());
        assertEquals(Boolean.FALSE, recovery.getActive());
    }
}
