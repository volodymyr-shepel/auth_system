package com.ackerman.service;

import com.ackerman.util.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${app.email-from}")
    private String emailSender;

    @Value("${app.email-from-name}")
    private String emailSenderName;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public ResponseEntity<String> send(EmailRequest emailRequest) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");


            helper.setText(emailRequest.email(), true);
            helper.setTo(emailRequest.senderEmail());
            helper.setSubject(emailRequest.subject());
            helper.setFrom(emailSender, emailSenderName);
            javaMailSender.send(mimeMessage);

            return ResponseEntity.ok("The email has been sent successfully");
        } catch (MessagingException | UnsupportedEncodingException e) {
            return ResponseEntity.badRequest().body("Error occurred when sending an email");
        }


    }
}