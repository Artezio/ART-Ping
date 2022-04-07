package com.artezio.artping.service;

import com.artezio.artping.data.exceptions.NotificationNotFoundException;
import com.artezio.artping.data.repository.NotificationRepository;
import com.artezio.artping.dto.NoticeDto;
import com.artezio.artping.entity.Notification;
import com.artezio.artping.entity.NotificationType;
import com.artezio.artping.entity.user.Employee;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для рассылки уведомлений сотрудникам.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final EmployeeService employeeService;
    private final MapperFacade mapper;
    private final JavaMailSender emailSender;

    /**
     * Получение уведомлений для текущего сотрудника
     *
     * @return список уведомлений для сотрудника
     */
    public List<NoticeDto> getActual() {
        Employee currentEmployee = employeeService.getCurrentEmployee();
        return mapper.mapAsList(repository.findAllByEmployeeIdOrderByCreationTimeDesc(currentEmployee.getId()),
                NoticeDto.class);
    }

    /**
     * Пометить уведомление как прочитанное
     *
     * @param id идентификатор уведомления
     * @return обновленное уведомление
     */
    public NoticeDto markRead(String id) {
        Notification notification = repository.findById(UUID.fromString(id))
                .orElseThrow(NotificationNotFoundException::new);
        notification.setViewed(true);
        return mapper.map(repository.save(notification), NoticeDto.class);
    }

    /**
     * Создание уведомлений одного типа для сотрудников с дополнительной отправкой по почте
     *
     * @param text      содержимое уведомления
     * @param employees список сотрудников
     * @param type      тип уведомления
     */
    public void createNotificationsToEmployees(String text, List<Employee> employees, NotificationType type) {
        List<Notification> notifications = new ArrayList<>();
        List<SimpleMailMessage> mailMessages = new ArrayList<>();
        for (Employee employee : employees) {
            Notification notification = new Notification();
            notification.setText(text);
            notification.setEmployee(employee);
            notification.setType(type);
            notifications.add(notification);
            mailMessages.add(createEmailNotification(text, employee.getEmail()));
        }
        repository.saveAll(notifications);
        emailSender.send(mailMessages.toArray(new SimpleMailMessage[0]));
    }

    private SimpleMailMessage createEmailNotification(String text, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@gmail.com");
        message.setTo(email);
        message.setSubject("[ART-Ping] Напоминание");
        message.setText(text);
        return message;
    }
}
