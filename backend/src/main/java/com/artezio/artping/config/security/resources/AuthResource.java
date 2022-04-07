package com.artezio.artping.config.security.resources;

import com.artezio.artping.config.security.AuthenticationService;
import com.artezio.artping.config.security.JwtTokenUtil;
import com.artezio.artping.config.security.model.AuthenticationRequest;
import com.artezio.artping.config.security.model.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthResource {
    private final UserDetailsService userDetailsService;
    private final AuthenticationService authenticationService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authorize(@RequestBody AuthenticationRequest request) {
        authenticationService.authenticate(request.getLogin(), request.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getLogin());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
