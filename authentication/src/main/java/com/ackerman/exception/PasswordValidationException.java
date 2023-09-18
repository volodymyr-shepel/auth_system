package com.ackerman.exception;

// PasswordValidationException is invoked when validation of the password fails
public class PasswordValidationException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Password does not meet security requirements";

    public PasswordValidationException() {
        super(DEFAULT_MESSAGE);
    }

    public PasswordValidationException(String customMessage) {
        super(customMessage);
    }
}
