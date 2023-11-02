package com.ackerman.controller;

import com.ackerman.appUser.AppUserDTO;
import com.ackerman.services.AuthenticationService;
import com.ackerman.services.RegistrationService;
import com.ackerman.util.AuthenticationRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping(path = "/api/auth")
//@CrossOrigin
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final RegistrationService registrationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, RegistrationService registrationService) {
        this.authenticationService = authenticationService;
        this.registrationService = registrationService;
    }

    // used to send registration request
    @PostMapping(path = "/register")
    public ResponseEntity<Integer> register(@RequestBody AppUserDTO appUserDTO){
        return registrationService.register(appUserDTO);
    }

    // used to confirm an email address


    @PostMapping(path = "/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest request, HttpServletResponse response){
        return authenticationService.authenticate(authenticationRequest,request,response);
    }

    @GetMapping(path = "/loginWithGoogle")
    public RedirectView loginWithGoogle() {
        return new RedirectView("/oauth2/authorization/google");
    }


    @GetMapping("/logout")
    public RedirectView logout(HttpServletRequest request, HttpServletResponse response) {
        // Clear the JWT cookie
        Cookie cookie = new Cookie("jwtToken", "");
        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");
        response.addCookie(cookie);

        // Redirect to the home page
        return new RedirectView("http://localhost/api/ui/home");
    }

}