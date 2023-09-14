package com.ackerman.clients.email;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "EMAIL",path = "api/email")
public interface EmailClient {

    @PostMapping(path = "sendEmail")
    ResponseEntity<String> sendEmail(@RequestBody ConfirmationRequest confirmationRequest);

    @GetMapping(path = "test")
    public String test();

}
