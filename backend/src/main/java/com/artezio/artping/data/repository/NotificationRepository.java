package com.artezio.artping.data.repository;

import com.artezio.artping.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Получение уведомлений по идентификатору сотрудника
     *
     * @param employeeId идентификатор сотрудника
     * @return список уведомлений для сотрудника
     */
    List<Notification> findAllByEmployeeIdOrderByCreationTimeDesc(UUID employeeId);

}
