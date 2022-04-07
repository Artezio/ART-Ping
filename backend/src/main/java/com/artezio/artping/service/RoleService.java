package com.artezio.artping.service;

import com.artezio.artping.data.repository.RoleRepository;
import com.artezio.artping.entity.user.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для работы с ролями сотрудников
 */
@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Нахождение сущности роли в БД по её коду
     * @param code код роли
     * @return сущность необходимой роли
     */
    @Transactional
    public Role findByCode(String code) {
        return roleRepository.findByCode(code);
    }
}
