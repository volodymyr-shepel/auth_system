package com.ackerman.exception;

public class InvalidConfirmationTokenException extends RuntimeException{
    public InvalidConfirmationTokenException(String customMessage) {
        super(customMessage);
    }
}
