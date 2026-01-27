package com.revpay.service;

import com.revpay.dao.NotificationDAO;
import com.revpay.model.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Simple notification service that prints messages to console and saves to DB
public class NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    private NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    // Send welcome notification to new user
    public void sendWelcomeNotification(int userId) {
        logger.info("Welcome notification sent to user: " + userId);
        Notification n = new Notification(userId, "Welcome", "Welcome to RevPay! Your account has been created.",
                "INFO");
        notificationDAO.createNotification(n);
        System.out.println("[NOTIFICATION] " + n.getMessage());
    }

    // Notify money sent
    public void notifyMoneySent(int senderId, int receiverId, double amount, String receiverName) {
        logger.info("Money sent notification: $" + amount + " to " + receiverName);
        String msg = String.format("You sent $%.2f to %s", amount, receiverName);
        Notification n = new Notification(senderId, "Money Sent", msg, "TRANSACTION");
        notificationDAO.createNotification(n);
        System.out.println("[NOTIFICATION] " + msg);
    }

    // Notify money received
    public void notifyMoneyReceived(int userId, double amount, String senderName) {
        logger.info("Money received notification: $" + amount + " from " + senderName);
        String msg = String.format("You received $%.2f from %s", amount, senderName);
        Notification n = new Notification(userId, "Money Received", msg, "TRANSACTION");
        notificationDAO.createNotification(n);
        System.out.println("[NOTIFICATION] " + msg);
    }

    // Notify money requested
    public void notifyMoneyRequested(int requesterId, int targetId, double amount, String requesterName) {
        logger.info("Money request notification: $" + amount + " from " + requesterName);
        String msg = String.format("%s is requesting $%.2f from you", requesterName, amount);
        Notification n = new Notification(targetId, "Money Request", msg, "REQUEST");
        notificationDAO.createNotification(n);
        System.out.println("[NOTIFICATION] " + msg);
    }

    // Notify request accepted
    public void notifyRequestAccepted(int requesterId, double amount, String targetName) {
        logger.info("Request accepted notification: $" + amount + " by " + targetName);
        String msg = String.format("%s accepted your request for $%.2f", targetName, amount);
        Notification n = new Notification(requesterId, "Request Accepted", msg, "REQUEST");
        notificationDAO.createNotification(n);
        System.out.println("[NOTIFICATION] " + msg);
    }

    // Notify request declined
    public void notifyRequestDeclined(int requesterId, double amount, String targetName) {
        logger.info("Request declined notification: $" + amount + " by " + targetName);
        String msg = String.format("%s declined your request for $%.2f", targetName, amount);
        Notification n = new Notification(requesterId, "Request Declined", msg, "REQUEST");
        notificationDAO.createNotification(n);
        System.out.println("[NOTIFICATION] " + msg);
    }

    // Notify low balance
    public void notifyLowBalance(int userId, double balance) {
        logger.warn("Low balance notification for user: " + userId);
        String msg = String.format("Low balance alert: $%.2f", balance);
        Notification n = new Notification(userId, "Low Balance", msg, "WARNING");
        notificationDAO.createNotification(n);
        System.out.println("[NOTIFICATION] " + msg);
    }

    // Notify invoice created
    public void notifyInvoiceCreated(int customerId, String invoiceNumber, double amount, String businessName) {
        logger.info("Invoice created notification: " + invoiceNumber);
        String msg = String.format("New invoice %s for $%.2f from %s", invoiceNumber, amount, businessName);
        Notification n = new Notification(customerId, "New Invoice", msg, "INVOICE");
        notificationDAO.createNotification(n);
        System.out.println("[NOTIFICATION] " + msg);
    }

    // Notify invoice paid
    public void notifyInvoicePaid(int businessId, String invoiceNumber, double amount, String customerName) {
        logger.info("Invoice paid notification: " + invoiceNumber);
        String msg = String.format("Invoice %s paid: $%.2f from %s", invoiceNumber, amount, customerName);
        Notification n = new Notification(businessId, "Invoice Paid", msg, "INVOICE");
        notificationDAO.createNotification(n);
        System.out.println("[NOTIFICATION] " + msg);
    }

    // Notify loan status change
    public void notifyLoanStatusChange(int userId, String status, double amount) {
        logger.info("Loan status change notification: " + status + " for user: " + userId);
        String msg = String.format("Loan status: %s - Amount: $%.2f", status, amount);
        Notification n = new Notification(userId, "Loan Update", msg, "LOAN");
        notificationDAO.createNotification(n);
        System.out.println("[NOTIFICATION] " + msg);
    }

    // Generic notification
    public void createNotification(int userId, String title, String message, String type) {
        logger.info("Generic notification created for user: " + userId);
        Notification n = new Notification(userId, title, message, type);
        notificationDAO.createNotification(n);
        System.out.println(String.format("[%s] %s: %s", type, title, message));
    }

    // Get unread notifications
    public java.util.List<Notification> getUnreadNotifications(int userId) {
        return notificationDAO.findUnreadByUserId(userId);
    }

    // Get all notifications
    public java.util.List<Notification> getAllNotifications(int userId) {
        return notificationDAO.findByUserId(userId);
    }

    // Mark as read
    public boolean markAsRead(int notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }

    // Mark all as read
    public boolean markAllAsRead(int userId) {
        return notificationDAO.markAllAsRead(userId);
    }
}
