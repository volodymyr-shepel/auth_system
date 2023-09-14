package com.ackerman.registrationServices;

public record NotificationRequest(
        String senderEmail,
        String topic,
        String email // means html form of email
){
}
