package com.ackerman.controller.authenticationService;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/static")
@CrossOrigin
public class UIServiceController {
    @GetMapping(path = "/login")
    public String getLoginPage(Model model){
        return "login";
    }

    @GetMapping(path = "/home")
    public String getHomePage(){
        return "home";
    }

}
