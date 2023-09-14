package com.ackerman.registrationController;

import com.ackerman.appUser.AppUserDTO;
import com.ackerman.registrationServices.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping(path = "register")
    public ResponseEntity<Integer> register(@RequestBody AppUserDTO appUserDTO){
        return registrationService.register(appUserDTO);
    }

    @PutMapping(path = "confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        return registrationService.confirmEmail(token);
    }
    @GetMapping(path = "test")
    public String test(){
        return "success";
    }

}
