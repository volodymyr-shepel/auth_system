package com.ackerman.services;

import com.ackerman.amqp.RabbitMQMessageProducer;
import com.ackerman.appUser.AppUser;
import com.ackerman.appUser.AppUserDTO;
import com.ackerman.appUser.AppUserRepository;
import com.ackerman.confirmationToken.ConfirmationTokenService;
import com.ackerman.exception.InvalidConfirmationTokenException;
import com.ackerman.util.EmailRequest;
import com.ackerman.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;




@Service
public class RegistrationService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    private final TemplateEngine templateEngine;

    private final ConfirmationTokenService confirmationTokenService;

    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    @Value("${app.domain-name}")
    private String apiGatewayUrl;

    @Value("${app.confirmation-email-template}")
    private String confirmationEmailTemplateName;

    @Value("${app.rabbitmq.internal-exchange}")
    private String internalExchange;

    @Value("${app.rabbitmq.email-routing-key}")
    private String emailRoutingKey;

    @Autowired
    public RegistrationService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator, TemplateEngine templateEngine, ConfirmationTokenService confirmationTokenService, RabbitMQMessageProducer rabbitMQMessageProducer) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.templateEngine = templateEngine;
        this.confirmationTokenService = confirmationTokenService;
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
    }


    // used to register the user and publish EmailRequest to rabbitMQ
    @Transactional
    public ResponseEntity<Integer> register(AppUserDTO appUserDTO) {

        // used to validate the password
        passwordValidator.validate(appUserDTO.password());

        // Creating AppUser instance from the DTO object
        AppUser createdUser = createUserFromDTO(appUserDTO);

        // Saves newly created user and used to extract user id which will be returned in ResponseEntity
        Integer userId = appUserRepository.saveAndFlush(createdUser).getId();

        String link = generateConfirmationLink(createdUser);

        //  Request which will be sent to email service
        EmailRequest emailRequest = createEmailRequest(createdUser.getUsername(),link,createdUser.getFirstName());

        rabbitMQMessageProducer.publish(
                emailRequest,
                internalExchange,
                emailRoutingKey
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userId);
    }

    @Transactional
    public String confirmEmail(String token, Model model) {
        try{
            confirmationTokenService.confirmToken(token);
            return "activation-success";
        }
        catch (InvalidConfirmationTokenException e){
            model.addAttribute("error",e.getMessage());
            return "activation-failed";
        }
        catch (Exception e){
            return "Unexpected error occurred." + e.getMessage();
        }

    }



    // Used to encode the password provided by a user, so it can be stored in database safely
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }


    // Used to create the AppUser which will be stored in database based on the appUserDto provided by the user
    public AppUser createUserFromDTO(AppUserDTO appUserDTO) {
        String encodedPassword = encodePassword(appUserDTO.password());
        return new AppUser(
                appUserDTO.email(),
                appUserDTO.firstName(),
                appUserDTO.lastName(),
                encodedPassword,
                appUserDTO.userRole()
        );
    }


    // Generate confirmation link which will be included in email, by clicking on which user will make request to
    // registration microservice through api gateway. In result account will be activated

    private String generateConfirmationLink(AppUser createdUser) {
        String token = confirmationTokenService.generateConfirmationToken(createdUser);
        return apiGatewayUrl + "/api/auth/confirm?token=" + token;
    }


    // Generate the html body for the confirmation email
    private String generateConfirmationEmail(String link, String name) {
        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("name", name);
        return templateEngine.process(confirmationEmailTemplateName, context);
    }

    //Used to create confirmation request which will be sent to the email service
    private EmailRequest createEmailRequest(String username, String link, String firstName) {
        return new EmailRequest(
                username,
                "Confirm your email",
                generateConfirmationEmail(link, firstName)
        );
    }

}
