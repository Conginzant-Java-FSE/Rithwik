package com.revpay.util;

import java.util.regex.Pattern;

// Utility class for input validation
public class ValidationUtils {

    // Email pattern - simple but effective
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    // Phone pattern - allows digits only (10-15 digits)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    private ValidationUtils() {
    }

    // Validate email format
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    // Validate phone number format (expects 10-15 digits only)
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Remove any non-digit characters before validation
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        return PHONE_PATTERN.matcher(digitsOnly).matches();
    }

    // Validate password strength (at least 8 chars with letter and number)
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < Constants.PASSWORD_MIN_LENGTH) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c))
                hasLetter = true;
            if (Character.isDigit(c))
                hasDigit = true;
        }
        return hasLetter && hasDigit;
    }

    // Validate transaction PIN (must be 4-6 digits only)
    public static boolean isValidPin(String pin) {
        if (pin == null) {
            return false;
        }
        int length = pin.length();
        if (length < Constants.PIN_MIN_LENGTH || length > Constants.PIN_MAX_LENGTH) {
            return false;
        }
        // Check if all characters are digits
        for (char c : pin.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    // Validate amount for transactions (must be positive)
    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }

    // Validate that a string is not null or empty
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Format a phone number to remove all non-digit characters
    public static String formatPhone(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[^0-9]", "");
    }

    // Validate card number format (basic check - should be 13-19 digits)
    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return false;
        }
        String digitsOnly = cardNumber.replaceAll("[^0-9]", "");
        int length = digitsOnly.length();
        return length >= 13 && length <= 19;
    }

    // Mask card number for display, showing only last 4 digits
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }
}
