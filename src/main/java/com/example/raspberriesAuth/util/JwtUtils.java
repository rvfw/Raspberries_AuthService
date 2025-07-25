package com.example.raspberriesAuth.util;

import com.example.raspberriesAuth.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;
    public String generateToken(User user) {
        Date now=new Date();
        return Jwts.builder()
                .setSubject(user.getId()+"")
                .claim("roles", String.join(",",user.getRoles()))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ Duration.ofHours(24).toMillis()))
                .signWith(SignatureAlgorithm.HS256, Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
