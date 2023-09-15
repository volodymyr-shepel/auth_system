package com.ackerman.auth;


public record AuthenticationResponse(String email, String accessToken) {


    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "email='" + email + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }


}
