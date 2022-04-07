package com.artezio.artping.data.repository;


import com.artezio.artping.entity.EmployeeSubscription;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Репозиторий для работы с подписками сотрудника
 */
public interface EmployeeSubscriptionRepository extends JpaRepository<EmployeeSubscription, UUID> {

    List<EmployeeSubscription> findAllByValidAndEmployeeIdIn(boolean valid, Iterable<UUID> ids);

    List<EmployeeSubscription> findBySubscribeId(String subscribeId);

    List<EmployeeSubscription> findByIdAndSubscribeIdAndValid(UUID id, String subscribeId, boolean valid);

    List<EmployeeSubscription> findAllBySubscribeIdAndValid(String subscribeId, boolean valid);
    
    @Modifying
    @Query("UPDATE EmployeeSubscription SET valid = false WHERE subscribeId = :subscribeId")
    int stopSubscription(@Param("subscribeId") String subscribeId);

    @Modifying
    @Query("DELETE from EmployeeSubscription where employeeId = :employeeId")
    void deleteByEmployeeId(UUID employeeId);
}
