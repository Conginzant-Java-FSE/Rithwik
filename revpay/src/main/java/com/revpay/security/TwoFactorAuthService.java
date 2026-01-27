package com.revpay.security;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Service for generating and verifying 2FA codes (simulated)
public class TwoFactorAuthService {

    private static final Logger logger = LogManager.getLogger(TwoFactorAuthService.class);
    private static TwoFactorAuthService instance;
    // Store codes in memory with expiration time
    private Map<Integer, String> userCodes = new HashMap<>();
    private Map<Integer, LocalDateTime> codeExpiry = new HashMap<>();

    private TwoFactorAuthService() {
    }

    // Get singleton instance
    public static synchronized TwoFactorAuthService getInstance() {
        if (instance == null) {
            instance = new TwoFactorAuthService();
        }
        return instance;
    }

    // Generate a 6-digit 2FA code for a user
    public String generateCode(int userId) {
        // Generate random 6-digit code
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        String codeStr = String.valueOf(code);
        // Store code with 5 minute expiration
        userCodes.put(userId, codeStr);
        codeExpiry.put(userId, LocalDateTime.now().plusMinutes(5));
        logger.info("2FA code generated for user: " + userId);
        // Simulate sending code (in real app, send via email/SMS)
        System.out.println("[2FA] Your verification code is: " + codeStr);
        return codeStr;
    }

    // Verify a 2FA code for a user
    public boolean verifyCode(int userId, String code) {
        if (!userCodes.containsKey(userId)) {
            logger.warn("No 2FA code found for user: " + userId);
            return false;
        }
        // Check if code expired
        LocalDateTime expiry = codeExpiry.get(userId);
        if (LocalDateTime.now().isAfter(expiry)) {
            logger.warn("2FA code expired for user: " + userId);
            userCodes.remove(userId);
            codeExpiry.remove(userId);
            return false;
        }
        // Verify code
        String storedCode = userCodes.get(userId);
        boolean matches = storedCode.equals(code);
        if (matches) {
            logger.info("2FA code verified successfully for user: " + userId);
            // Remove code after successful verification
            userCodes.remove(userId);
            codeExpiry.remove(userId);
        } else {
            logger.warn("Invalid 2FA code for user: " + userId);
        }
        return matches;
    }

    // Clear code for a user
    public void clearCode(int userId) {
        userCodes.remove(userId);
        codeExpiry.remove(userId);
        logger.debug("2FA code cleared for user: " + userId);
    }
}
