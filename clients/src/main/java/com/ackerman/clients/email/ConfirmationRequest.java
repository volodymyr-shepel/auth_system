package com.ackerman.clients.email;

public record ConfirmationRequest(
        String senderEmail,
        String subject,
        String email // means html form of email
){
}
