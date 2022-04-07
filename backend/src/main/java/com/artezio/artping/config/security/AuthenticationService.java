package com.artezio.artping.config.security;

import com.artezio.artping.data.exceptions.NoSuchUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProcessor tokenProcessor;

    public Authentication authenticate(String username, String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    public User getCurrentUser(String token) throws NoSuchUserException {
        String s = tokenProcessor.extractUsername(token);
        User principal = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (principal.getUsername().equals(s)) {
            return principal;
        } else throw new NoSuchUserException();
    }
}
