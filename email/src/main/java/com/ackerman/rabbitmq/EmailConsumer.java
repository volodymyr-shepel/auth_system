package com.ackerman.rabbitmq;

import com.ackerman.EmailService;
import com.ackerman.clients.email.ConfirmationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailConsumer {
    private final EmailService emailService;

    @Autowired
    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${rabbitmq.queues.email}")
    public void consumer(ConfirmationRequest confirmationRequest){
        log.info("Consuming message");
        emailService.send(confirmationRequest);
        log.info("Consumed message");

    }
}
