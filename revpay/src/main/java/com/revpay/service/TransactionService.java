package com.revpay.service;

import com.revpay.dao.TransactionDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.util.List;

// Transaction service for sending money and managing transactions
public class TransactionService {

    private static final Logger logger = LogManager.getLogger(TransactionService.class);
    private TransactionDAO transactionDAO;
    private UserDAO userDAO;
    private WalletService walletService;
    private NotificationService notificationService;

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
        this.userDAO = new UserDAO();
        this.walletService = new WalletService();
        this.notificationService = new NotificationService();
    }

    // Send money to another user (requires PIN)
    public Transaction sendMoney(int senderId, String receiverIdentifier, double amount, String note, String pin) {
        try {
            // Validate amount
            if (amount <= 0) {
                logger.warn("Invalid send amount: " + amount);
                return null;
            }
            // Find receiver
            User receiver = userDAO.findByIdentifier(receiverIdentifier);
            if (receiver == null) {
                logger.warn("Receiver not found: " + receiverIdentifier);
                return null;
            }
            // Cannot send to self
            if (receiver.getUserId() == senderId) {
                logger.warn("Cannot send money to self");
                return null;
            }
            // Check balance
            if (!walletService.hasSufficientBalance(senderId, amount)) {
                logger.warn("Insufficient balance for user " + senderId);
                return null;
            }
            // Get sender name for notification
            User sender = userDAO.findById(senderId);

            // Verify Transaction PIN
            if (sender.getTransactionPinHash() == null
                    || !com.revpay.security.PasswordUtils.verifyPin(pin, sender.getTransactionPinHash())) {
                logger.warn("Invalid Transaction PIN for user " + senderId);
                System.out.println("Transaction failed: Invalid PIN.");
                return null;
            }
            // Create transaction record
            Transaction transaction = new Transaction();
            transaction.setSenderId(senderId);
            transaction.setReceiverId(receiver.getUserId());
            transaction.setAmount(amount);
            transaction.setTransactionType(Constants.TRANSACTION_SEND);
            transaction.setStatus(Constants.STATUS_COMPLETED);
            transaction.setNote(note);
            // Transfer money
            boolean transferred = walletService.transfer(senderId, receiver.getUserId(), amount);
            if (!transferred) {
                transaction.setStatus(Constants.STATUS_FAILED);
                transactionDAO.createTransaction(transaction);
                return null;
            }
            // Save transaction
            Transaction createdTransaction = transactionDAO.createTransaction(transaction);
            // Send notifications
            notificationService.notifyMoneySent(senderId, receiver.getUserId(), amount, receiver.getFullName());
            logger.info(String.format("Money sent: $%.2f from %d to %d", amount, senderId, receiver.getUserId()));
            return createdTransaction;
        } catch (Exception e) {
            logger.error("Error sending money: " + e.getMessage());
            throw e;
        }
    }

    // Record money added to wallet
    public Transaction recordAddMoney(int userId, double amount, String note) {
        Transaction transaction = new Transaction();
        transaction.setSenderId(userId);
        transaction.setReceiverId(userId);
        transaction.setAmount(amount);
        transaction.setTransactionType(Constants.TRANSACTION_ADD_MONEY);
        transaction.setStatus(Constants.STATUS_COMPLETED);
        transaction.setNote(note != null ? note : "Added money to wallet");
        return transactionDAO.createTransaction(transaction);
    }

    // Record money withdrawn from wallet
    public Transaction recordWithdraw(int userId, double amount, String note) {
        Transaction transaction = new Transaction();
        transaction.setSenderId(userId);
        transaction.setReceiverId(userId);
        transaction.setAmount(amount);
        transaction.setTransactionType(Constants.TRANSACTION_WITHDRAW);
        transaction.setStatus(Constants.STATUS_COMPLETED);
        transaction.setNote(note != null ? note : "Withdrew from wallet");
        return transactionDAO.createTransaction(transaction);
    }

    // Get all transactions for a user
    public List<Transaction> getTransactions(int userId) {
        return transactionDAO.findByUserId(userId);
    }

    // Get transactions with filters
    public List<Transaction> getTransactionsFiltered(int userId, String type, String status, LocalDate startDate,
            LocalDate endDate, double minAmount, double maxAmount) {
        return transactionDAO.findWithFilters(userId, type, status, startDate, endDate, minAmount, maxAmount);
    }

    // Get a specific transaction by ID
    public Transaction getTransaction(int transactionId) {
        return transactionDAO.findById(transactionId);
    }

    // Get total amount sent by user
    public double getTotalSent(int userId) {
        return transactionDAO.getTotalSent(userId);
    }

    // Get total amount received by user
    public double getTotalReceived(int userId) {
        return transactionDAO.getTotalReceived(userId);
    }

    // Search for a user by identifier
    public User findUser(String identifier) {
        return userDAO.findByIdentifier(identifier);
    }

    // Export transaction history to CSV format
    public String exportToCSV(int userId) {
        List<Transaction> transactions = getTransactions(userId);
        StringBuilder csv = new StringBuilder();
        csv.append("Transaction ID,Type,Amount,Status,Note,Date\n");
        for (Transaction t : transactions) {
            csv.append(t.getTransactionId()).append(",");
            csv.append(t.getTransactionType()).append(",");
            csv.append(String.format("%.2f", t.getAmount())).append(",");
            csv.append(t.getStatus()).append(",");
            csv.append(t.getNote() != null ? "\"" + t.getNote() + "\"" : "").append(",");
            csv.append(t.getFormattedDate()).append("\n");
        }
        return csv.toString();
    }
}
