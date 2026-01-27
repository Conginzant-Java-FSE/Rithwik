package com.revpay.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Encryption Utils Tests")
public class EncryptionUtilsTest {

    @Test
    @DisplayName("Encryption and decryption should work correctly")
    void testEncryptionDecryption() {
        String originalCardNumber = "4532015112830366";
        String encrypted = EncryptionUtils.encryptCardNumber(originalCardNumber);
        String decrypted = EncryptionUtils.decryptCardNumber(encrypted);

        assertNotNull(encrypted);
        assertNotEquals(originalCardNumber, encrypted);
        assertEquals(originalCardNumber, decrypted);
    }

    @Test
    @DisplayName("Card encryption should be deterministic (ECB mode)")
    void testCardEncryptionDeterministic() {
        String cardNumber = "4532015112830366";
        String encrypted1 = EncryptionUtils.encryptCardNumber(cardNumber);
        String encrypted2 = EncryptionUtils.encryptCardNumber(cardNumber);

        assertEquals(encrypted1, encrypted2);
        assertEquals(EncryptionUtils.decryptCardNumber(encrypted1), EncryptionUtils.decryptCardNumber(encrypted2));
    }
}
