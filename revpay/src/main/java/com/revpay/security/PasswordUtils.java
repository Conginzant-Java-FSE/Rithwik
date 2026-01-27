package com.revpay.security;

import org.mindrot.jbcrypt.BCrypt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Password utility for hashing and verifying passwords and PINs using BCrypt
public class PasswordUtils {

    private static final Logger logger = LogManager.getLogger(PasswordUtils.class);

    // Hash a password using BCrypt
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            logger.warn("Attempt to hash null or empty password");
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        // Use BCrypt with default salt rounds (10)
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        logger.debug("Password hashed successfully");
        return hashed;
    }

    // Verify a plain password against a BCrypt hash
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            logger.warn("Null password or hash provided for verification");
            return false;
        }
        try {
            boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
            logger.debug("Password verification result: " + matches);
            return matches;
        } catch (Exception e) {
            logger.error("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    // Hash a transaction PIN using BCrypt
    public static String hashPin(String plainPin) {
        if (plainPin == null || plainPin.isEmpty()) {
            logger.warn("Attempt to hash null or empty PIN");
            throw new IllegalArgumentException("PIN cannot be null or empty");
        }
        // Use BCrypt for PIN as well
        String hashed = BCrypt.hashpw(plainPin, BCrypt.gensalt());
        logger.debug("PIN hashed successfully");
        return hashed;
    }

    // Verify a plain PIN against a BCrypt hash
    public static boolean verifyPin(String plainPin, String hashedPin) {
        if (plainPin == null || hashedPin == null) {
            logger.warn("Null PIN or hash provided for verification");
            return false;
        }
        try {
            boolean matches = BCrypt.checkpw(plainPin, hashedPin);
            logger.debug("PIN verification result: " + matches);
            return matches;
        } catch (Exception e) {
            logger.error("Error verifying PIN: " + e.getMessage());
            return false;
        }
    }
}
