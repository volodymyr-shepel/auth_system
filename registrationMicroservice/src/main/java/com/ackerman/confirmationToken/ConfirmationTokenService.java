package com.ackerman.confirmationToken;

import com.ackerman.appUser.AppUser;
import com.ackerman.appUser.AppUserRepository;
import com.ackerman.confirmationToken.ConfirmationToken;
import com.ackerman.confirmationToken.ConfirmationTokenRepository;
import com.ackerman.exception.InvalidConfirmationTokenException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final AppUserRepository appUserRepository;

    @Autowired
    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository, AppUserRepository appUserRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.appUserRepository = appUserRepository;
    }

    public String generateConfirmationToken(AppUser newUser) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), // confirmation token will be valid for 15 minutes
                newUser
        );
        confirmationTokenRepository.save(confirmationToken);
        return token;
    }

    @Transactional
    // used to verify if the confirmation token is valid, and if it is - enable user account
    public ResponseEntity<String> confirmToken(String token){
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new InvalidConfirmationTokenException("token not found"));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new InvalidConfirmationTokenException("token was already submitted");
        }
        if (LocalDateTime.now().isAfter(confirmationToken.getExpiresAt())) {
            throw new InvalidConfirmationTokenException("Token has expired");
        }
        AppUser appUser = appUserRepository.findByEmail(
                confirmationToken.getAppUser().getUsername()).orElseThrow(() ->
                new InvalidConfirmationTokenException("The user with provided email does not exist"));

        confirmationToken.setConfirmedAt(LocalDateTime.now());

        appUser.setIsEnabled(true);

        return ResponseEntity.ok("Confirmed");
    }
}