package com.artezio.artping.data.repository;

import com.artezio.artping.entity.PwdRecovery;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репоситорий для работы с записями о восстановлении пароля
 */
@Repository
public interface PwdRecoveryRepository extends JpaRepository<PwdRecovery, UUID> {

    /**
     * Поиск всех записей о восстановлении пароля по сотруднику
     *
     * @param employeeId идентификаторо сотрудника
     *
     * @return список найденных заявок на восстановление
     */
    Collection<PwdRecovery> findAllByEmployeeId(UUID employeeId);

    /**
     * Поиск активной заявки на восстановление пароля по сотруднику
     *Аа
     * @param employeeId идентификаторо сотрудника
     *
     * @return найденная активная заявка
     */
    PwdRecovery findDistinctByActiveTrueAndEmployeeId(UUID employeeId);

}
