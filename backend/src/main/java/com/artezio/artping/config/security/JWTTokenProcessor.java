package com.artezio.artping.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class JWTTokenProcessor {
    private String SECRET_KEY = "verylongsecretkeyshouldbe256kilobytesforverysecurereasons";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}
