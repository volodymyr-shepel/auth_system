package com.ackerman.controller;

import com.ackerman.services.RegistrationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UIController {


    private final RegistrationService registrationService;

    @Autowired
    public UIController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping(path = "/api/auth/confirm")
    public String confirmEmail(@RequestParam("token") String token,Model model) {
        return registrationService.confirmEmail(token,model);
    }

    @GetMapping("/api/auth/css/{cssFileName}")
    public String getCss(@PathVariable String cssFileName) {
        return "forward:/css/" + cssFileName; // Assuming CSS files are in /src/main/resources/static/css/
    }
    @GetMapping("/api/auth/js/{jsFileName}")
    public String getJs(@PathVariable String jsFileName) {
        return "forward:/js/" + jsFileName; // Assuming JS files are in /src/main/resources/static/js/
    }
    @GetMapping(path = "/api/auth/success")
    public String success(Model model) {
        return "activation-success";
    }
    @GetMapping(path = "/api/auth/failure")
    public String failure(Model model) {
        return "activation-failed";
    }

}

