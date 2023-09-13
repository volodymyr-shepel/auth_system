package com.ackerman;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

// Template for the exception
public record ApiExcepiton(String message,
                           HttpStatus httpStatus,
                           ZonedDateTime timestamp) {

}
