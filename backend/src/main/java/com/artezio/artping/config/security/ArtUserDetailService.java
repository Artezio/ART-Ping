package com.artezio.artping.config.security;

import com.artezio.artping.data.exceptions.NoSuchUserException;
import com.artezio.artping.data.repository.RoleRepository;
import com.artezio.artping.data.repository.UserRepository;
import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.UserEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtUserDetailService implements UserDetailsService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = repository.findByLoginAndActiveTrue(username).orElseThrow(NoSuchUserException::new);
        return new User(user.getLogin(), user.getPassword(), getUserRoles(user.getEmployee()));
    }

    private List<GrantedAuthority> getUserRoles(Employee employee) {
        return employee!=null ?
                employee.getRoles().stream().map(r->new SimpleGrantedAuthority(r.getCode())).collect(Collectors.toList()) :
                Collections.singletonList(new SimpleGrantedAuthority(roleRepository.findByCode(RoleEnum.ADMIN.getCode()).getCode()));
    }
}
