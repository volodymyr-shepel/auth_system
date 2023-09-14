package com.ackerman.registrationService;

import com.ackerman.appUser.AppUser;
import com.ackerman.appUser.AppUserDTO;
import com.ackerman.appUser.AppUserRepository;
import com.ackerman.appUser.UserRole;
import com.ackerman.clients.email.EmailClient;
import com.ackerman.clients.email.ConfirmationRequest;
import com.ackerman.confirmationToken.ConfirmationToken;
import com.ackerman.confirmationToken.ConfirmationTokenRepository;
import com.ackerman.confirmationToken.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;


//TODO:REFACTOR

@Service
public class RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final AppUserRepository appUserRepository; // +
    private final PasswordEncoder passwordEncoder;// +
    private final PasswordValidator passwordValidator; // +

    private final TemplateEngine templateEngine; // ?

    private final ConfirmationTokenService confirmationTokenService; // +

    private final ConfirmationTokenRepository confirmationTokenRepository; // ?

    private final EmailClient emailClient; // +

    @Autowired
    public RegistrationService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator, TemplateEngine templateEngine, ConfirmationTokenService confirmationTokenService, ConfirmationTokenRepository confirmationTokenRepository, EmailClient emailClient) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.templateEngine = templateEngine;
        this.confirmationTokenService = confirmationTokenService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailClient = emailClient;
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


        //send request to email service with the use of feign client
        emailClient.sendEmail(confirmationRequest);

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


    // Generate confirmation link which will be included in email, by clicking on which user will activate account
    private String generateConfirmationLink(AppUser createdUser) {
        String token = confirmationTokenService.generateConfirmationToken(createdUser);
        // TODO: change so it sends on the service not on direct ip address
        String baseUrl = "http://localhost:8080"; // send request to api gateway
        return baseUrl + "/api/confirm?token=" + token;
    }


    // Generate the html body for the confirmation email
    private String generateConfirmationEmail(String link, String name) {
        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("name", name);

        return templateEngine.process("confirmation-email-template", context);
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
