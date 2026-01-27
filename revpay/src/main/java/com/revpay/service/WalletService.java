package com.revpay.service;

import com.revpay.dao.UserDAO;
import com.revpay.model.User;
import com.revpay.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Wallet service for managing user wallet balance
public class WalletService {

    private static final Logger logger = LogManager.getLogger(WalletService.class);
    private UserDAO userDAO;
    private NotificationService notificationService;

    public WalletService() {
        this.userDAO = new UserDAO();
        this.notificationService = new NotificationService();
    }

    // Get wallet balance for a user
    public double getBalance(int userId) {
        User user = userDAO.findById(userId);
        return user != null ? user.getWalletBalance() : 0.0;
    }

    // Add money to wallet from a payment method (simulated)
    public boolean addMoney(int userId, double amount) {
        if (amount <= 0) {
            logger.warn("Invalid amount to add: " + amount);
            return false;
        }
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                return false;
            }
            double newBalance = user.getWalletBalance() + amount;
            user.setWalletBalance(newBalance);
            boolean updated = userDAO.updateWalletBalance(userId, newBalance);
            if (updated) {
                logger.info(String.format("Added $%.2f to wallet for user %d. New balance: $%.2f", amount, userId,
                        newBalance));
            }
            return updated;
        } catch (Exception e) {
            logger.error("Error adding money to wallet: " + e.getMessage());
            throw e;
        }
    }

    // Withdraw money from wallet to bank account (simulated)
    public boolean withdrawMoney(int userId, double amount) {
        if (amount <= 0) {
            logger.warn("Invalid amount to withdraw: " + amount);
            return false;
        }
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                return false;
            }
            // Check sufficient balance
            if (user.getWalletBalance() < amount) {
                logger.warn("Insufficient balance for withdrawal. Balance: " + user.getWalletBalance() + ", Requested: "
                        + amount);
                return false;
            }
            double newBalance = user.getWalletBalance() - amount;
            user.setWalletBalance(newBalance);
            boolean updated = userDAO.updateWalletBalance(userId, newBalance);
            if (updated) {
                logger.info(String.format("Withdrew $%.2f from wallet for user %d. New balance: $%.2f", amount, userId,
                        newBalance));
                // Check for low balance
                checkLowBalance(userId, newBalance);
            }
            return updated;
        } catch (Exception e) {
            logger.error("Error withdrawing money from wallet: " + e.getMessage());
            throw e;
        }
    }

    // Transfer money between two users' wallets
    public boolean transfer(int senderId, int receiverId, double amount) {
        if (amount <= 0) {
            logger.warn("Invalid transfer amount: " + amount);
            return false;
        }
        try {
            User sender = userDAO.findById(senderId);
            User receiver = userDAO.findById(receiverId);
            if (sender == null || receiver == null) {
                return false;
            }
            // Check sufficient balance
            if (sender.getWalletBalance() < amount) {
                logger.warn("Insufficient balance for transfer. Balance: " + sender.getWalletBalance() + ", Requested: "
                        + amount);
                return false;
            }
            // Deduct from sender
            double senderNewBalance = sender.getWalletBalance() - amount;
            userDAO.updateWalletBalance(senderId, senderNewBalance);
            // Add to receiver
            double receiverNewBalance = receiver.getWalletBalance() + amount;
            userDAO.updateWalletBalance(receiverId, receiverNewBalance);
            logger.info(String.format("Transferred $%.2f from user %d to user %d", amount, senderId, receiverId));
            // Check for low balance
            checkLowBalance(senderId, senderNewBalance);
            return true;
        } catch (Exception e) {
            logger.error("Error transferring money: " + e.getMessage());
            throw e;
        }
    }

    // Check if user has sufficient balance for a transaction
    public boolean hasSufficientBalance(int userId, double amount) {
        User user = userDAO.findById(userId);
        return user != null && user.getWalletBalance() >= amount;
    }

    // Check for low balance and send notification if needed
    private void checkLowBalance(int userId, double balance) {
        if (balance < Constants.LOW_BALANCE_THRESHOLD && balance > 0) {
            notificationService.notifyLowBalance(userId, balance);
        }
    }

    // Get formatted balance string
    public String getFormattedBalance(int userId) {
        double balance = getBalance(userId);
        return String.format("$%,.2f", balance);
    }

    // Debit money from wallet (internal payment)
    public boolean debit(int userId, double amount, String description) {
        if (amount <= 0)
            return false;
        try {
            User user = userDAO.findById(userId);
            if (user == null || user.getWalletBalance() < amount) {
                return false;
            }
            double newBalance = user.getWalletBalance() - amount;
            boolean updated = userDAO.updateWalletBalance(userId, newBalance);
            if (updated) {
                logger.info("Debited $" + amount + " from user " + userId + ": " + description);
                checkLowBalance(userId, newBalance);
            }
            return updated;
        } catch (Exception e) {
            logger.error("Error debiting wallet: " + e.getMessage());
            return false;
        }
    }
}
