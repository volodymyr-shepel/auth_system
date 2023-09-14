package com.ackerman.registrationServices;

import com.ackerman.ConfirmationToken;
import com.ackerman.ConfirmationTokenRepository;
import com.ackerman.ConfirmationTokenService;
import com.ackerman.appUser.AppUser;
import com.ackerman.appUser.AppUserDTO;
import com.ackerman.appUser.AppUserRepository;
import com.ackerman.appUser.UserRole;
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

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    private final TemplateEngine templateEngine;

    private final ConfirmationTokenService confirmationTokenService;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public RegistrationService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator, TemplateEngine templateEngine, ConfirmationTokenService confirmationTokenService, ConfirmationTokenRepository confirmationTokenRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.templateEngine = templateEngine;
        this.confirmationTokenService = confirmationTokenService;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public ResponseEntity<Integer> register(AppUserDTO appUserDTO) {
        // used to validate the password
        passwordValidator.validate(appUserDTO.password());

        // Creating AppUser instance from the DTO object
        AppUser createdUser = createUserFromDTO(appUserDTO);

        // Used to extract the user id assigned to newly created user, so it can be return in Response body
        Integer userId = appUserRepository.saveAndFlush(createdUser).getId();

        String token = confirmationTokenService.generateConfirmationToken(createdUser);
        String baseUrl = "http://localhost:8081";
        String link = baseUrl + "/api/registration/confirm?token=" + token;

        // Notification request is ready to be sent to notification service
        NotificationRequest notificationRequest = new NotificationRequest(
                createdUser.getUsername(),
                "Confirm your email",
                generateConfirmationEmail(link,createdUser.getFirstName())
        );

        logger.info(generateConfirmationEmail(link,createdUser.getFirstName()));
        //TODO: send request to notification service


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userId);
    }


    @Transactional
    public ResponseEntity<String> confirmEmail(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("token was already submitted");
        }
        if (LocalDateTime.now().isAfter(confirmationToken.getExpiresAt())) {
            throw new IllegalStateException("Token has expired");
        }
        confirmationToken.setConfirmedAt(LocalDateTime.now());

        AppUser appUser = appUserRepository.findByEmail(
                confirmationToken.getAppUser().getUsername()).orElseThrow(() ->
                new IllegalStateException("The user with provided email does not exist"));

        appUser.setIsEnabled(true);


        return ResponseEntity.ok("Confirmed");
    }



    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
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


    public String generateConfirmationEmail(String link, String name) {
        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("name", name);

        return templateEngine.process("confirmation-email-template", context);
    }


}
