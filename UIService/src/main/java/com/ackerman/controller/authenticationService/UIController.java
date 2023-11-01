package com.ackerman.controller.authenticationService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UIController {
    @GetMapping("/api/ui/css/{cssFileName}")
    public String getCss(@PathVariable String cssFileName) {
        return "forward:/css/" + cssFileName; // Assuming CSS files are in /src/main/resources/static/css/
    }
    @GetMapping("/api/ui/js/{jsFileName}")
    public String getJs(@PathVariable String jsFileName) {
        return "forward:/js/" + jsFileName; // Assuming JS files are in /src/main/resources/static/js/
    }

}