package com.revpay.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// AES-256 encryption utility for encrypting sensitive data like card numbers
public class EncryptionUtils {

    private static final Logger logger = LogManager.getLogger(EncryptionUtils.class);
    // 16-byte key for AES-128 (Universally supported)
    private static final String SECRET_KEY = "RevPaySecureKey!RevPaySecureKey!"; // 32 chars for AES-256

    // Encrypt a card number using AES-128
    public static String encryptCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            logger.warn("Attempt to encrypt null or empty card number");
            return null;
        }
        try {
            // Create AES cipher
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            // Encrypt and encode to Base64
            byte[] encrypted = cipher.doFinal(cardNumber.getBytes());
            String result = Base64.getEncoder().encodeToString(encrypted);
            logger.debug("Card number encrypted successfully");
            return result;
        } catch (Exception e) {
            logger.error("Error encrypting card number: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            return null;
        }
    }

    // Decrypt a card number using AES-256
    public static String decryptCardNumber(String encryptedCard) {
        if (encryptedCard == null || encryptedCard.isEmpty()) {
            logger.warn("Attempt to decrypt null or empty card number");
            return null;
        }
        try {
            // Create AES cipher
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            // Decrypt from Base64
            byte[] decoded = Base64.getDecoder().decode(encryptedCard);
            byte[] decrypted = cipher.doFinal(decoded);
            String result = new String(decrypted);
            logger.debug("Card number decrypted successfully");
            return result;
        } catch (Exception e) {
            logger.error("Error decrypting card number: " + e.getMessage());
            return null;
        }
    }
}
