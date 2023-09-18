package com.ackerman.rabbitmq;

import com.ackerman.service.EmailService;
import com.ackerman.util.EmailRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class EmailConsumer {
    private final EmailService emailService;

    @Autowired
    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${rabbitmq.queues.email}")
    public void consumer(EmailRequest emailRequest){
        emailService.send(emailRequest);

    }
}
