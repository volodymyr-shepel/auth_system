package com.ackerman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegistrationServiceTest {

    private RegistrationService registrationService;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidator passwordValidator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        registrationService = new RegistrationService(appUserRepository, passwordEncoder, passwordValidator);
    }
    @Test
    public void testEncodePassword() {
        // Arrange
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";

        // Mock the behavior of the PasswordEncoder
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // Act
        String result = registrationService.encodePassword(rawPassword);

        // Assert
        assertEquals(encodedPassword, result);
    }

    @Test
    public void testCreateUserFromDTO() {
        // Arrange
        AppUserDTO appUserDTO = new AppUserDTO("test@example.com", "John", "Doe", "password123");
        String encodedPassword = "encodedPassword";

        // Mock the behavior of the PasswordEncoder
        when(passwordEncoder.encode(appUserDTO.password())).thenReturn(encodedPassword);

        // Act
        AppUser result = registrationService.createUserFromDTO(appUserDTO);

        // Assert
        assertEquals(appUserDTO.username(), result.getUsername());
        assertEquals(appUserDTO.firstName(), result.getFirstName());
        assertEquals(appUserDTO.lastName(), result.getLastName());
        assertEquals(encodedPassword, result.getPassword());
        assertEquals(UserRole.USER, result.getRole());
    }

    @Test
    public void testRegisterUserWithValidPassword() {
        // Arrange
        AppUserDTO appUserDTO = new AppUserDTO("test@example.com", "John", "Doe", "StrongP@ssw0rd");
        when(passwordValidator.test(appUserDTO.password())).thenReturn(true);

        AppUser createdUser = new AppUser(1, appUserDTO.username(), appUserDTO.firstName(), appUserDTO.lastName(), "encodedPassword", UserRole.USER);
        when(appUserRepository.saveAndFlush(any(AppUser.class))).thenReturn(createdUser);

        // Act
        ResponseEntity<Integer> response = registrationService.register(appUserDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, response.getBody());
    }

    @Test
    public void testRegisterUserWithInvalidPassword() {
        // Arrange
        AppUserDTO appUserDTO = new AppUserDTO("test@example.com", "John", "Doe", "weak");
        when(passwordValidator.test(appUserDTO.password())).thenReturn(false);

        // Act and Assert
        assertThrows(PasswordValidationException.class, () -> registrationService.register(appUserDTO));
        verify(appUserRepository, never()).saveAndFlush(any(AppUser.class));
    }
}