package com.revpay.service;

import com.revpay.dao.PaymentMethodDAO;
import com.revpay.model.PaymentMethod;
import com.revpay.security.EncryptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

// Payment method service for managing credit/debit cards
public class PaymentMethodService {

    private static final Logger logger = LogManager.getLogger(PaymentMethodService.class);
    private PaymentMethodDAO paymentMethodDAO;

    public PaymentMethodService() {
        this.paymentMethodDAO = new PaymentMethodDAO();
    }

    // Add a new payment method (credit/debit card)
    public PaymentMethod addPaymentMethod(int userId, String cardType, String cardNumber, String cardHolderName,
            String expiryMonth, String expiryYear, boolean setAsDefault) {
        try {
            // Store only last 4 digits for security
            String lastFour = cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : cardNumber;
            // Create payment method
            PaymentMethod method = new PaymentMethod();
            method.setUserId(userId);
            method.setCardType(cardType.toUpperCase());
            method.setCardLastFour(lastFour);
            method.setEncryptedCardNumber(EncryptionUtils.encryptCardNumber(cardNumber));
            method.setCardHolderName(cardHolderName);
            method.setExpiryMonth(expiryMonth);
            method.setExpiryYear(expiryYear);
            method.setDefault(setAsDefault);
            PaymentMethod createdMethod = paymentMethodDAO.createPaymentMethod(method);
            logger.info("Payment method added for user: " + userId);
            System.out.println("[NOTIFICATION] Payment method added successfully.");
            return createdMethod;
        } catch (Exception e) {
            logger.error("Error adding payment method: " + e.getMessage());
            System.err.println("Error adding payment method: " + e.getMessage());
            throw e;
        }
    }

    // Get all payment methods for a user
    public List<PaymentMethod> getPaymentMethods(int userId) {
        return paymentMethodDAO.findByUserId(userId);
    }

    // Get default payment method for a user
    public PaymentMethod getDefaultPaymentMethod(int userId) {
        return paymentMethodDAO.getDefaultPaymentMethod(userId);
    }

    // Set a payment method as default
    public boolean setAsDefault(int paymentId, int userId) {
        return paymentMethodDAO.setAsDefault(paymentId, userId);
    }

    // Remove a payment method
    public boolean removePaymentMethod(int paymentId, int userId) {
        try {
            // Verify ownership
            PaymentMethod method = paymentMethodDAO.findById(paymentId);
            if (method == null || method.getUserId() != userId) {
                logger.warn("Invalid payment method or ownership mismatch");
                return false;
            }
            boolean removed = paymentMethodDAO.deletePaymentMethod(paymentId);
            if (removed) {
                logger.info("Payment method removed: " + paymentId);
            }
            return removed;
        } catch (Exception e) {
            logger.error("Error removing payment method: " + e.getMessage());
            throw e;
        }
    }

    // Get a specific payment method
    public PaymentMethod getPaymentMethod(int paymentId) {
        return paymentMethodDAO.findById(paymentId);
    }

    // Check if user has any payment method
    public boolean hasPaymentMethod(int userId) {
        List<PaymentMethod> methods = paymentMethodDAO.findByUserId(userId);
        return methods != null && !methods.isEmpty();
    }
}
