package com.ackerman.confirmationToken;

import com.ackerman.appUser.AppUser;
import com.ackerman.appUser.AppUserRepository;
import com.ackerman.exception.InvalidConfirmationTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ConfirmationTokenServiceTest {


    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        confirmationTokenService = new ConfirmationTokenService(
                confirmationTokenRepository,
                appUserRepository
        );
    }
    @Test
    public void testConfirmTokenSuccess() {
        // Arrange
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        AppUser appUser = new AppUser();
        appUser.setEmail("test@example.com");

        confirmationToken.setAppUser(appUser); // Associate the token with the user

        when(confirmationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(confirmationToken));
        when(appUserRepository.findByEmail(anyString())).thenReturn(Optional.of(appUser));

        // Act
        ResponseEntity<String> response = confirmationTokenService.confirmToken("validToken");

        // Assert
        verify(confirmationTokenRepository, times(1)).findByToken("validToken");
        verify(appUserRepository, times(1)).findByEmail(anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Confirmed", response.getBody());
        assertEquals(true, appUser.getIsEnabled());
        assertNotNull(confirmationToken.getConfirmedAt());
    }

    @Test
    public void testConfirmTokenTokenNotFound() {
        // Arrange
        when(confirmationTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(InvalidConfirmationTokenException.class, () -> {
            confirmationTokenService.confirmToken("invalidToken");
        });
    }
    @Test
    public void testConfirmTokenAlreadySubmitted() {
        // Arrange
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setConfirmedAt(LocalDateTime.now());

        when(confirmationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(confirmationToken));

        // Act and Assert
        assertThrows(InvalidConfirmationTokenException.class, () -> {
            confirmationTokenService.confirmToken("alreadyConfirmedToken");
        });
    }

    @Test
    public void testConfirmTokenTokenExpired() {
        // Arrange
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(confirmationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(confirmationToken));

        // Act and Assert
        assertThrows(InvalidConfirmationTokenException.class, () -> {
            confirmationTokenService.confirmToken("expiredToken");
        });
    }

}
