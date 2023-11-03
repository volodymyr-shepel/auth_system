package com.ackerman.util;

import com.ackerman.exception.JwtValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public List<String> extractRolesFromToken(String authHeader) {
        Claims claims = extractAllClaims(authHeader);
        return (List<String>) claims.get("roles");
    }
    public List<GrantedAuthority> extractAuthoritiesFromToken(String authHeader) {
        List<String> userRoles = extractRolesFromToken(authHeader);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : userRoles) {
            // Assuming your UserRole class has a method to get the role name
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    public void checkUserRole(String authHeader, UserRole requiredRole) {
        List<String> roles = extractRolesFromToken(authHeader);

        if (!roles.contains(requiredRole.toString())) {
            throw new IllegalStateException("In order to access this endpoint '" + requiredRole + "' role is required");
        }
    }

    public static String extractAuthToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new JwtValidationException("No cookies found in the request");
        }

        for (Cookie cookie : cookies) {
            if ("jwtToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new JwtValidationException("Token cookie not found");
    }
}

