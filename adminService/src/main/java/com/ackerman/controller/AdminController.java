package com.ackerman.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/admin")
public class AdminController {

    public String test(){
        return "admin service";
    }

}
