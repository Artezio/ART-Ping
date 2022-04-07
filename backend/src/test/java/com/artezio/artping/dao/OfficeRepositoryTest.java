package com.artezio.artping.dao;

import com.artezio.artping.data.repository.OfficeRepository;
import com.artezio.artping.entity.office.Office;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class OfficeRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private OfficeRepository officeRepository;

    @Test
    public void testCreate() {
        Office office = createOffice();
        office = officeRepository.save(office);

        assertNotNull(office);
        assertNotNull(office.getId());
    }
}
