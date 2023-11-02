package com.ackerman.util;

public record EmailRequest(
        String senderEmail,
        String subject,
        String email // means html form of email
){
}
