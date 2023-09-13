package com.ackerman;

// used during registration request
public record AppUserDTO(String email,
                         String firstName,
                         String lastName,
                         String password) {
}
