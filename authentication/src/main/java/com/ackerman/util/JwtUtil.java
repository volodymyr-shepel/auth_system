package com.ackerman.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    public String generateToken(UserDetails  userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Extract user roles from UserDetails authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        if (authorities != null && !authorities.isEmpty()) {
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            claims.put("roles", roles);
        }

        return createToken(claims, userDetails.getUsername());


    }
    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("ECommerce")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 24 hours expiration
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
