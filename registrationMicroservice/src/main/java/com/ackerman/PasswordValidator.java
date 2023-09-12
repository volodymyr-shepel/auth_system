package com.ackerman;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

// The service which is used to validate the password based on the requirements provided inside test method

// Passwords must be a minimum of 12 characters long and include a combination of uppercase and lowercase letters,
// digits, and at least one special character.
@Service
public class PasswordValidator {
    public Boolean test(String password) {
        // Minimum 12 characters
        if (password.length() < 12) {
            return false;
        }

        // A combination of uppercase, lowercase, letters, and at least one special character
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{12,}$");
        return pattern.matcher(password).matches();

    }
}