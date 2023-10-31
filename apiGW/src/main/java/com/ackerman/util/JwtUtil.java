package com.ackerman.util;

import com.ackerman.exception.JwtValidationException;
import com.google.common.net.HttpHeaders;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;


    public void validateToken(String token) {
        try {
            // Parse and validate the token
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            // If no exceptions are thrown, the token is valid
        } catch (Exception e) {
            throw new JwtValidationException("JWT token validation failed: " + e.getMessage());
        }
    }


    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    protected Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public List<UserRole> extractRolesFromToken(String authHeader) {
        Claims claims = extractAllClaims(authHeader);
        return (List<UserRole>) claims.get("roles");
    }

    public void checkUserRole(String authHeader, UserRole requiredRole) {
        List<UserRole> roles = extractRolesFromToken(authHeader);

        if (!roles.contains(requiredRole.toString())) {
            throw new IllegalStateException("In order to access this endpoint '" + requiredRole + "' role is required");
        }
    }

    public static String extractAuthToken(ServerWebExchange exchange) {
        HttpCookie tokenCookie = exchange.getRequest()
                .getCookies()
                .getFirst("jwtToken");

        if (tokenCookie == null) {
            throw new JwtValidationException("Token cookie not found");
        }

        return tokenCookie.getValue();
    }





}
