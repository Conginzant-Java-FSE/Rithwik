package com.revpay.model;

import java.time.LocalDateTime;

/**
 * User model representing both personal and business account users.
 */
public class User {

    private int userId;
    private String email;
    private String phone;
    private String fullName;
    private String passwordHash;
    private String securityQuestion;
    private String securityAnswer;
    private String transactionPinHash;
    private int failedLoginAttempts;
    private LocalDateTime lockoutUntil;
    private double walletBalance;
    private String accountType;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    // Default constructor
    public User() {
        this.walletBalance = 0.0;
        this.accountType = "PERSONAL";
        this.failedLoginAttempts = 0;
    }

    // Constructor with basic fields
    public User(String email, String phone, String fullName, String passwordHash) {
        this();
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getTransactionPinHash() {
        return transactionPinHash;
    }

    public void setTransactionPinHash(String transactionPinHash) {
        this.transactionPinHash = transactionPinHash;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getLockoutUntil() {
        return lockoutUntil;
    }

    public void setLockoutUntil(LocalDateTime lockoutUntil) {
        this.lockoutUntil = lockoutUntil;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper method to check if user is a business account
    public boolean isBusinessAccount() {
        return "BUSINESS".equals(this.accountType);
    }

    public boolean isLockoutActive() {
        return lockoutUntil != null && lockoutUntil.isAfter(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", accountType='" + accountType + '\'' +
                ", walletBalance=" + walletBalance +
                '}';
    }
}
