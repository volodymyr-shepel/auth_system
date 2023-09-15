package com.ackerman.auth;

import com.ackerman.appUser.AppUser;
import com.ackerman.appUser.AppUserRepository;
import com.ackerman.securityConfiguration.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authManager;
    private final JwtTokenUtil jwtUtil;

    private final AppUserRepository appUserRepository;

    @Autowired
    public AuthenticationService(AuthenticationManager authManager, JwtTokenUtil jwtUtil, AppUserRepository appUserRepository) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.appUserRepository = appUserRepository;
    }

    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );

            AppUser user = (AppUser) authentication.getPrincipal();

            String accessToken = jwtUtil.generateAccessToken(user);
            AuthenticationResponse response = new AuthenticationResponse(user.getUsername(), accessToken);

            return ResponseEntity.ok().body(response);


        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
