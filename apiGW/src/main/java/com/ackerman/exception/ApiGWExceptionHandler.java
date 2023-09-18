//package com.ackerman.exception;
//
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//
//@ControllerAdvice
//public class ApiGWExceptionHandler {
//    @ExceptionHandler(value = {
//            JwtValidationException.class
//    })
//    public ResponseEntity<Object> handleCustomExceptions(RuntimeException e) {
//        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
//        ApiException apiException = new ApiException(
//
//                e.getMessage(),
//                HttpStatus.BAD_REQUEST,
//                ZonedDateTime.now(ZoneId.of("Europe/Warsaw"))
//        );
//
//        return new ResponseEntity<>(apiException, badRequest);
//    }
//
//}