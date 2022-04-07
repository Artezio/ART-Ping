package com.artezio.artping.service;

import com.artezio.artping.dto.ContextUserDto;
import com.artezio.artping.dto.RoleEnum;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Сервис для получения данных о текущем пользователе системы
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    /**
     * Получение данных о текущем пользователе системы
     * @return имя пользователя и его роли
     * @throws SecurityException если пользователь не авторизован
     */
    public ContextUserDto getCurrentUser() throws SecurityException {
        ContextUserDto userDto = new ContextUserDto();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            userDto.setUsername(auth.getPrincipal().toString());
            List<RoleEnum> roles = auth.getAuthorities().stream()
                                       .map(authAuthorities -> RoleEnum.fromCode(authAuthorities.getAuthority()))
                                       .collect(Collectors.toList());
            userDto.setRoles(roles);
            return userDto;
        } catch (SecurityException secException) {
            log.error("User is not authorized!");
            throw new SecurityException("User cannot be found!");
        }
    }

}
