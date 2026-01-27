package com.revpay.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validation Utility Tests")
public class ValidationUtilsTest {

    @Test
    @DisplayName("Email validation checks format")
    void testEmailValidation() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
        assertTrue(ValidationUtils.isValidEmail("name.surname@company.co.jp"));
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
        assertFalse(ValidationUtils.isValidEmail("@domain.com"));
        assertFalse(ValidationUtils.isValidEmail("user@"));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    @Test
    @DisplayName("Phone validation checks digits and length")
    void testPhoneValidation() {
        assertTrue(ValidationUtils.isValidPhone("1234567890")); // 10 digits
        assertTrue(ValidationUtils.isValidPhone("123456789012345")); // 15 digits
        assertFalse(ValidationUtils.isValidPhone("123")); // too short
        assertFalse(ValidationUtils.isValidPhone("1234567890123456")); // too long
        assertFalse(ValidationUtils.isValidPhone("abcdefghij")); // not digits
    }

    @Test
    @DisplayName("Password validation enforces complexity")
    void testPasswordValidation() {
        // Valid: 8+ chars, contains letters and numbers
        assertTrue(ValidationUtils.isValidPassword("password123"));
        assertTrue(ValidationUtils.isValidPassword("StrongP4ss"));

        // Invalid
        assertFalse(ValidationUtils.isValidPassword("pass")); // too short
        assertFalse(ValidationUtils.isValidPassword("passwordpassword")); // no digits
        assertFalse(ValidationUtils.isValidPassword("12345678")); // no letters
    }

    @Test
    @DisplayName("PIN validation checks for 4-6 digits")
    void testPinValidation() {
        assertTrue(ValidationUtils.isValidPin("1234"));
        assertTrue(ValidationUtils.isValidPin("123456"));
        assertFalse(ValidationUtils.isValidPin("12")); // too short
        assertFalse(ValidationUtils.isValidPin("12345678")); // too long
        assertFalse(ValidationUtils.isValidPin("abcd")); // not digits
    }

    @Test
    @DisplayName("Amount validation ensures positive values")
    void testAmountValidation() {
        assertTrue(ValidationUtils.isValidAmount(10.0));
        assertTrue(ValidationUtils.isValidAmount(0.01));
        assertFalse(ValidationUtils.isValidAmount(0.0));
        assertFalse(ValidationUtils.isValidAmount(-50.0));
    }

    @Test
    @DisplayName("Card number validation checks length")
    void testCardNumberValidation() {
        assertTrue(ValidationUtils.isValidCardNumber("1234567890123")); // 13 digits
        assertTrue(ValidationUtils.isValidCardNumber("1234567890123456789")); // 19 digits
        assertFalse(ValidationUtils.isValidCardNumber("123")); // too short
    }

    @Test
    @DisplayName("Card masking hides all but last 4 digits")
    void testCardMasking() {
        assertEquals("**** **** **** 3456", ValidationUtils.maskCardNumber("1234567812343456"));
        assertEquals("****", ValidationUtils.maskCardNumber("123")); // Handles short input gracefully
        assertEquals("**** **** **** 6789", ValidationUtils.maskCardNumber("6789"));
        assertEquals("**** **** **** null", ValidationUtils.maskCardNumber("null")); // String literal "null"
        assertEquals("****", ValidationUtils.maskCardNumber(null));
    }
}
