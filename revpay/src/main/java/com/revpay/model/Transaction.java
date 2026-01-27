package com.revpay.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Transaction model representing money transfers between users
public class Transaction {

    private int transactionId;
    private int senderId;
    private int receiverId;
    private double amount;
    private String transactionType; // SEND, REQUEST, ADD_MONEY, WITHDRAW, INVOICE_PAYMENT
    private String status; // PENDING, COMPLETED, FAILED, CANCELLED
    private String note;
    private LocalDateTime createdAt;
    // Additional fields for display purposes
    private String senderName;
    private String receiverName;

    public Transaction() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Transaction(int senderId, int receiverId, double amount, String transactionType, String note) {
        this();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.note = note;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    // Get formatted date
    public String getFormattedDate() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return createdAt.format(formatter);
        }
        return "";
    }

    // Check if transaction is completed
    public boolean isCompleted() {
        return "COMPLETED".equals(this.status);
    }

    // Get formatted amount with currency
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", type='" + transactionType + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", date='" + getFormattedDate() + '\'' +
                '}';
    }
}
