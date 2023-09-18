package com.ackerman.controller;

import com.ackerman.appUser.AppUserDTO;
import com.ackerman.services.AuthenticationService;
import com.ackerman.services.RegistrationService;
import com.ackerman.util.AuthenticationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthenticationContoller {

    private final AuthenticationService authenticationService;

    private final RegistrationService registrationService;

    @Autowired
    public AuthenticationContoller(AuthenticationService authenticationService, RegistrationService registrationService) {
        this.authenticationService = authenticationService;
        this.registrationService = registrationService;
    }

    // used to send registration request
    @PostMapping(path = "/register")
    public ResponseEntity<Integer> register(@RequestBody AppUserDTO appUserDTO){
        return registrationService.register(appUserDTO);
    }

    // used to confirm an email address
    @GetMapping(path = "/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        return registrationService.confirmEmail(token);
    }

    @PostMapping(path = "/authenticate")
    public String authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        return authenticationService.authenticate(authenticationRequest);
    }

}
