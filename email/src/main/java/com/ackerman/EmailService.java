package com.ackerman;

import com.ackerman.clients.email.ConfirmationRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final Environment environment;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, Environment environment) {
        this.javaMailSender = javaMailSender;
        this.environment = environment;
    }

    @Async
    public ResponseEntity<String> send(ConfirmationRequest confirmationRequest) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");

            String from = environment.getProperty("app.email-from");
            String fromName = environment.getProperty("app.email-from-name");

            helper.setText(confirmationRequest.email(), true);
            helper.setTo(confirmationRequest.senderEmail());
            helper.setSubject(confirmationRequest.subject());
            helper.setFrom(from, fromName);
            javaMailSender.send(mimeMessage);

            return ResponseEntity.ok("The email has been sent successfully");
        } catch (MessagingException | UnsupportedEncodingException e) {
            return ResponseEntity.badRequest().body("Error occurred when sending an email");
        }


    }
}