package com.ackerman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        // Act
        boolean result = passwordValidator.test(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testTooShortPassword() {
        // Arrange
        String tooShortPassword = "Short1!";

        // Act
        boolean result = passwordValidator.test(tooShortPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testMissingUppercase() {
        // Arrange
        String missingUppercase = "lowercase123!";

        // Act
        boolean result = passwordValidator.test(missingUppercase);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testMissingLowercase() {
        // Arrange
        String missingLowercase = "UPPERCASE123!";

        // Act
        boolean result = passwordValidator.test(missingLowercase);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testMissingSpecialCharacter() {
        // Arrange
        String missingSpecialCharacter = "MixedCase1234";

        // Act
        boolean result = passwordValidator.test(missingSpecialCharacter);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testValidMinimumLength() {
        // Arrange
        String validMinimumLength = "Aa!1Bb@2Cc#3Dd$4Ee%5";

        // Act
        boolean result = passwordValidator.test(validMinimumLength);

        // Assert
        assertTrue(result);
    }
}
