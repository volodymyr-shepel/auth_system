package com.ackerman;

import com.ackerman.exception.PasswordValidationException;
import com.ackerman.registrationService.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTest {

    private PasswordValidator passwordValidator;

    @BeforeEach
    public void setUp() {
        passwordValidator = new PasswordValidator();
    }

    @Test
    public void testValidPassword() {
        // Arrange
        String validPassword = "StrongP@ssw0rd";

        // Act & Assert
        assertDoesNotThrow(() -> passwordValidator.validate(validPassword));
    }

    @Test
    public void testTooShortPassword() {
        // Arrange
        String tooShortPassword = "Short1!";

        // Act & Assert
        assertThrows(PasswordValidationException.class, () -> passwordValidator.validate(tooShortPassword));
    }

    @Test
    public void testMissingUppercase() {
        // Arrange
        String missingUppercase = "lowercase123!";

        // Act & Assert
        assertThrows(PasswordValidationException.class, () -> passwordValidator.validate(missingUppercase));
    }

    @Test
    public void testMissingLowercase() {
        // Arrange
        String missingLowercase = "UPPERCASE123!";

        // Act & Assert
        assertThrows(PasswordValidationException.class, () -> passwordValidator.validate(missingLowercase));
    }

    @Test
    public void testMissingSpecialCharacter() {
        // Arrange
        String missingSpecialCharacter = "MixedCase1234";

        // Act & Assert
        assertThrows(PasswordValidationException.class, () -> passwordValidator.validate(missingSpecialCharacter));
    }

    @Test
    public void testValidMinimumLength() {
        // Arrange
        String validMinimumLength = "Aa!1Bb@2Cc#3Dd$4Ee%5";

        // Act & Assert
        assertDoesNotThrow(() -> passwordValidator.validate(validMinimumLength));
    }
}