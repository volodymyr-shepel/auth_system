package com.ackerman.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/customer")
public class CustomerController {

    @GetMapping(path = "test")
    public String test(){
        return "customer service";
    }

}