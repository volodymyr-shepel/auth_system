package com.ackerman;

public record AuthenticationRequest(
        String email,
        String password

) {
}
