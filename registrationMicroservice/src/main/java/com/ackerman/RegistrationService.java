package com.ackerman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private final PasswordValidator passwordValidator;

    @Autowired
    public RegistrationService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    public ResponseEntity<Integer> register(AppUserDTO appUserDTO) {
        // used to validate the password
        passwordValidator.validate(appUserDTO.password());

        // Creating AppUser instance from the DTO object
        AppUser createdUser = createUserFromDTO(appUserDTO);

        // Used to extract the user id assigned to newly created user, so it can be return in Response body
        Integer userId = appUserRepository.saveAndFlush(createdUser).getId();

        //TODO send message to notification microservice

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userId);
    }

    // validating the password provided by a user
    protected String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    protected AppUser createUserFromDTO(AppUserDTO appUserDTO) {
        String encodedPassword = encodePassword(appUserDTO.password());
        return new AppUser(
                appUserDTO.email(),
                appUserDTO.firstName(),
                appUserDTO.lastName(),
                encodedPassword,
                UserRole.USER
        );
    }
}
