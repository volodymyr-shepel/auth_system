package com.ackerman.controller;

import com.ackerman.service.EmailService;
import com.ackerman.util.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/email")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping(path = "sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest){
        return emailService.send(emailRequest);
    }
}
