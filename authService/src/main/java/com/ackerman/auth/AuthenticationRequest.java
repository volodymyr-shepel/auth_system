package com.ackerman.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class AuthenticationRequest {

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }


    public String email;

    public String password;

}
