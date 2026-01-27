package com.revpay.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Money request model for requesting money from other users
public class MoneyRequest {

    private int requestId;
    private int requesterId;
    private int targetId;
    private double amount;
    private String note;
    private String status; // PENDING, ACCEPTED, DECLINED, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Additional fields for display
    private String requesterName;
    private String targetName;

    public MoneyRequest() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public MoneyRequest(int requesterId, int targetId, double amount, String note) {
        this();
        this.requesterId = requesterId;
        this.targetId = targetId;
        this.amount = amount;
        this.note = note;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    // Check if request is pending
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }

    // Get formatted date
    public String getFormattedDate() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return createdAt.format(formatter);
        }
        return "";
    }

    // Get formatted amount
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    @Override
    public String toString() {
        return "MoneyRequest{" +
                "requestId=" + requestId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", date='" + getFormattedDate() + '\'' +
                '}';
    }
}
