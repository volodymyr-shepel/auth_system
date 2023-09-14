package com.ackerman.registrationService;

import com.ackerman.amqp.RabbitMQMessageProducer;
import com.ackerman.appUser.AppUser;
import com.ackerman.appUser.AppUserDTO;
import com.ackerman.appUser.AppUserRepository;
import com.ackerman.appUser.UserRole;
import com.ackerman.clients.email.EmailClient;
import com.ackerman.clients.email.ConfirmationRequest;
import com.ackerman.confirmationToken.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;




//TODO:REFACTOR

@Service
public class RegistrationService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    private final TemplateEngine templateEngine;

    private final ConfirmationTokenService confirmationTokenService;

    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    @Value("${app.api-gateway-address}")
    private String apiGatewayUrl;

    @Value("${app.confirmation-email-template}")
    private String confirmationEmailTemplateName;

    @Autowired
    public RegistrationService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator, TemplateEngine templateEngine, ConfirmationTokenService confirmationTokenService, RabbitMQMessageProducer rabbitMQMessageProducer) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.templateEngine = templateEngine;
        this.confirmationTokenService = confirmationTokenService;
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
    }

    public ResponseEntity<Integer> register(AppUserDTO appUserDTO) {

        // used to validate the password
        passwordValidator.validate(appUserDTO.password());

        // Creating AppUser instance from the DTO object
        AppUser createdUser = createUserFromDTO(appUserDTO);

        // Saves newly created user and used to extract user id which will be returned in ResponseEntity
        Integer userId = appUserRepository.saveAndFlush(createdUser).getId();

        String link = generateConfirmationLink(createdUser);


        //  Request which will be sent to email service
        ConfirmationRequest confirmationRequest = createConfirmationRequest(createdUser.getUsername(),link,createdUser.getFirstName());


        //TODO: send it to message queue
        //send request to email service with the use of feign client
        //emailClient.sendEmail(confirmationRequest);

        rabbitMQMessageProducer.publish(
                confirmationRequest,
                "internal.exchange",
                "internal.email.routing-key"
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userId);
    }

    @Transactional
    public ResponseEntity<String> confirmEmail(String token) {
        return confirmationTokenService.confirmToken(token);
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
                UserRole.USER
        );
    }


    // Generate confirmation link which will be included in email, by clicking on which user will make request to
    // registration microservice through api gateway. In result account will be activated

    private String generateConfirmationLink(AppUser createdUser) {
        String token = confirmationTokenService.generateConfirmationToken(createdUser);
        // TODO: change so it sends on the service not on direct ip address
        return apiGatewayUrl + "/api/confirm?token=" + token;
    }


    // Generate the html body for the confirmation email
    private String generateConfirmationEmail(String link, String name) {
        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("name", name);

        return templateEngine.process(confirmationEmailTemplateName, context);
    }

    //Used to create confirmation request which will be sent to the email service
    private ConfirmationRequest createConfirmationRequest(String username, String link, String firstName) {
        return new ConfirmationRequest(
                username,
                "Confirm your email",
                generateConfirmationEmail(link, firstName)
        );
    }

}
