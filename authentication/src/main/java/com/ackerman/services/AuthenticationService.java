package com.ackerman.services;

import com.ackerman.appUser.AppUser;
import com.ackerman.util.AuthenticationRequest;
import com.ackerman.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<String> authenticate(AuthenticationRequest authenticationRequest, HttpServletRequest request, HttpServletResponse response) {
        // verify the provided credentials

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.email(), authenticationRequest.password())
            );
            //extracts the authenticated user's information from the Authentication object's principal
            AppUser user = (AppUser) authentication.getPrincipal();


            String jwtToken = jwtUtil.generateToken(user);
            Cookie cookie = new Cookie("jwtToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(request.isSecure()); // Optionally set the "secure" flag for HTTPS
            cookie.setPath("/"); // Set the cookie path as needed
            response.addCookie(cookie);



            return ResponseEntity.ok(jwtToken);
        }
        catch(org.springframework.security.core.AuthenticationException e){
            return ResponseEntity.badRequest().body("Check your credentials");
        }


    }


}
