package com.revpay.model;

import java.time.LocalDateTime;

// Payment method model representing credit/debit cards
public class PaymentMethod {

    private int paymentId;
    private int userId;
    private String cardType; // CREDIT or DEBIT
    private String cardLastFour;
    private String encryptedCardNumber;
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private boolean isDefault;
    private LocalDateTime createdAt;

    public PaymentMethod() {
        this.isDefault = false;
    }

    public PaymentMethod(int userId, String cardType, String cardLastFour, String cardHolderName, String expiryMonth,
            String expiryYear) {
        this();
        this.userId = userId;
        this.cardType = cardType;
        this.cardLastFour = cardLastFour;
        this.cardHolderName = cardHolderName;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }

    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }

    public String getEncryptedCardNumber() {
        return encryptedCardNumber;
    }

    public void setEncryptedCardNumber(String encryptedCardNumber) {
        this.encryptedCardNumber = encryptedCardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Get masked card number for display
    public String getMaskedCardNumber() {
        return "**** **** **** " + (cardLastFour != null ? cardLastFour : "****");
    }

    // Get expiry as combined string
    public String getExpiry() {
        return expiryMonth + "/" + expiryYear;
    }

    // Display string for payment method
    public String getDisplayString() {
        String defaultLabel = isDefault ? " (Default)" : "";
        return cardType + " - " + getMaskedCardNumber() + " - Expires: " + getExpiry() + defaultLabel;
    }

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "paymentId=" + paymentId +
                ", cardType='" + cardType + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", expiry='" + getExpiry() + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
