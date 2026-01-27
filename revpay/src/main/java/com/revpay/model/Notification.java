package com.revpay.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Notification model for in-app notifications
public class Notification {

    private int notificationId;
    private int userId;
    private String title;
    private String message;
    private String notificationType; // TRANSACTION, REQUEST, ALERT, INVOICE, LOAN
    private boolean isRead;
    private LocalDateTime createdAt;

    public Notification() {
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(int userId, String title, String message, String notificationType) {
        this();
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Get formatted date
    public String getFormattedDate() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return createdAt.format(formatter);
        }
        return "";
    }

    // Get status icon
    public String getStatusIcon() {
        return isRead ? "[Read]" : "[New]";
    }

    // Get display string for console
    public String getDisplayString() {
        String readStatus = isRead ? "" : "[NEW] ";
        return readStatus + "[" + notificationType + "] " + title + " - " + getFormattedDate();
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", title='" + title + '\'' +
                ", type='" + notificationType + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
