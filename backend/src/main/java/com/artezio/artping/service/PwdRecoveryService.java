package com.artezio.artping.service;

import com.artezio.artping.data.exceptions.PasswordRecoveryIsNotActiveException;
import com.artezio.artping.data.repository.EmployeeRepository;
import com.artezio.artping.data.repository.PwdRecoveryRepository;
import com.artezio.artping.entity.PwdRecovery;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

/**
 * Сервис для работы с записями о восстановлении пароля
 */
@Service("pwdRecoveryService")
@RequiredArgsConstructor
@Transactional
@Lazy
@Slf4j
public class PwdRecoveryService {
    private final PasswordEncoder bcryptPasswordEncoder;
    private final PwdRecoveryRepository repository;
    private final EmployeeRepository employeeRepository;
    private final JavaMailSender emailSender;

    @Value("${art-ping.pwdRecoveryURL}")
    private String pwdRecoveryURL;

    /**
     * Создание запроса на изменение пароля
     * <p>
     * Создание запроса на изменение пароля по email, генерация ссылки и отправка на почту сотрудника
     *
     * @param email электронная почта сотрудника
     */
    public void createByEmail(String email) {
        Collection<Employee> employees = employeeRepository.findAllByUserActiveTrueAndEmailIgnoreCase(email);
        if (isEmpty(employees)) {
            return;
        }

        process(employees.iterator().next());
    }

    /**
     * Создание запроса на изменение пароля
     * <p>
     * Создание запроса на изменение пароля по логину, генерация ссылки и отправка на почту сотрудника
     *
     * @param login логин сотрудника
     */
    public void createByLogin(String login) {
        Employee employee = employeeRepository.findDistinctByUserActiveTrueAndLogin(login);
        if (employee == null) {
            return;
        }

        process(employee);
    }

    private void process(Employee employee) {
        PwdRecovery recovery = repository.findDistinctByActiveTrueAndEmployeeId(employee.getId());
        if (recovery == null) {
            recovery = createPwdRecovery(employee);
            log.info("recovery record {} created", recovery.getId());
        } else {
            log.info("found recovery record {}", recovery.getId());
        }

        sendPwdRecoveryMessage(employee.getEmail(), pwdRecoveryURL + recovery.getId().toString());
    }

    private PwdRecovery createPwdRecovery(Employee emp) {
        PwdRecovery recovery = new PwdRecovery();
        recovery.setCreated(LocalDate.now());
        recovery.setActive(true);
        recovery.setEmployee(emp);
        return repository.save(recovery);
    }

    /**
     * Обновление пароля по запросу
     * Поиск в базе id запроса на обновление, проверка active флага,
     * декодирование пароля из Base64 в строку,
     * кодирование пароля в bcrypt, обновление пароля у сотрудника, перезапись флага активности запроса
     *
     * @param recoveryId ключ для восстановления пароля
     * @param base64EncodedPassword новый пароль
     */
    @Transactional
    public void update(String recoveryId, String base64EncodedPassword) {
        PwdRecovery recovery = repository.findById(UUID.fromString(recoveryId))
                .orElseThrow(PasswordRecoveryIsNotActiveException::new);

        if (recovery.getActive()) {
            String hashedPassword = encodeWithBCrypt(base64EncodedPassword);
            updateEmployeePassword(recovery.getEmployee(), hashedPassword);
            recovery.setActive(false);
        } else {
            throw new PasswordRecoveryIsNotActiveException();
        }
    }

    private void updateEmployeePassword(Employee employee, String hashedPassword) {
        UserEntity user = employee.getUser();
        user.setPassword(hashedPassword);
    }

    private String encodeWithBCrypt(String base64EncodedPassword) {
        byte[] decoded = Base64.getMimeDecoder().decode(base64EncodedPassword);
        String password = new String(decoded);
        return bcryptPasswordEncoder.encode(password);
    }

    private void sendPwdRecoveryMessage(String email, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@gmail.com");
        message.setTo(email);
        message.setSubject("Восстановление пароля");
        message.setText(text);
        emailSender.send(message);
    }
}
