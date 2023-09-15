package com.ackerman;

import com.ackerman.appUser.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    public String authenticate(AuthenticationRequest authenticationRequest) {
        // verify the provided credentials

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(), authenticationRequest.password())
        );

        //extracts the authenticated user's information from the Authentication object's principal
        AppUser user = (AppUser) authentication.getPrincipal();

        return jwtUtil.generateToken(user);
    }
}
