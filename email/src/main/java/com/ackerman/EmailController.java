package com.ackerman;

import com.ackerman.clients.email.ConfirmationRequest;
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
    public ResponseEntity<String> sendEmail(@RequestBody ConfirmationRequest confirmationRequest){
        return emailService.send(confirmationRequest);
    }
    @GetMapping(path = "test")
    public String test(){
        return "Hello";
    }
}
