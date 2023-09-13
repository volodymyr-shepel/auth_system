package com.ackerman.registrationServices;

import com.ackerman.exception.PasswordValidationException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

// The service which is used to validate the password based on the requirements provided inside test method

// Passwords must be a minimum of 12 characters long and include a combination of uppercase and lowercase letters,
// digits, and at least one special character.
@Service
public class PasswordValidator {
    public void validate(String password) throws PasswordValidationException {
        // Minimum 12 characters
        if (password.length() < 12) {
            throw new PasswordValidationException("Password must be at least 12 characters long.");
        }

        // A combination of uppercase, lowercase, letters, and at least one special character
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{12,}$");
        if (!pattern.matcher(password).matches()) {
            throw new PasswordValidationException("Password must include uppercase and lowercase letters, a special character, and be at least 12 characters long.");
        }
    }
}
