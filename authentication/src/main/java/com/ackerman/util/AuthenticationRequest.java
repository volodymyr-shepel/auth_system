package com.ackerman.util;

public record AuthenticationRequest(
        String email,
        String password

) {
}
