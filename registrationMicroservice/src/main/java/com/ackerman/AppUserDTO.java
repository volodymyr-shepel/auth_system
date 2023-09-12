package com.ackerman;

// used during registration request
public record AppUserDTO(String username,
                         String firstName,
                         String lastName,
                         String password) {
}
