package com.revpay.model;

import java.time.LocalDateTime;

// Business account model containing additional business-specific information
public class BusinessAccount {

    private int businessId;
    private int userId;
    private String businessName;
    private String businessType;
    private String taxId;
    private String address;
    private boolean isVerified;
    private LocalDateTime createdAt;
    private User user;

    public BusinessAccount() {
        this.isVerified = false;
    }

    public BusinessAccount(int userId, String businessName, String businessType, String taxId, String address) {
        this();
        this.userId = userId;
        this.businessName = businessName;
        this.businessType = businessType;
        this.taxId = taxId;
        this.address = address;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "BusinessAccount{" +
                "businessId=" + businessId +
                ", businessName='" + businessName + '\'' +
                ", businessType='" + businessType + '\'' +
                ", isVerified=" + isVerified +
                '}';
    }
}
