package com.revpay.dao;

import com.revpay.config.DatabaseConfig;
import com.revpay.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for Transaction operations
public class TransactionDAO {

    private static final Logger logger = LogManager.getLogger(TransactionDAO.class);

    // Create a new transaction
    public Transaction createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (sender_id, receiver_id, amount, transaction_type, status, note) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, transaction.getSenderId());
            stmt.setInt(2, transaction.getReceiverId());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setString(4, transaction.getTransactionType());
            stmt.setString(5, transaction.getStatus());
            stmt.setString(6, transaction.getNote());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    transaction.setTransactionId(rs.getInt(1));
                    transaction.setCreatedAt(LocalDateTime.now());
                    logger.info("Transaction created with ID: " + transaction.getTransactionId());
                }
            }
            return transaction;
        } catch (SQLException e) {
            logger.error("Error creating transaction: " + e.getMessage());
            throw new RuntimeException("Failed to create transaction", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find a transaction by ID
    public Transaction findById(int transactionId) {
        String sql = "SELECT t.*, s.full_name as sender_name, r.full_name as receiver_name FROM transactions t LEFT JOIN users s ON t.sender_id = s.user_id LEFT JOIN users r ON t.receiver_id = r.user_id WHERE t.transaction_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, transactionId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding transaction: " + e.getMessage());
            throw new RuntimeException("Failed to find transaction", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Get all transactions for a user (sent and received)
    public List<Transaction> findByUserId(int userId) {
        String sql = "SELECT t.*, s.full_name as sender_name, r.full_name as receiver_name FROM transactions t LEFT JOIN users s ON t.sender_id = s.user_id LEFT JOIN users r ON t.receiver_id = r.user_id WHERE t.sender_id = ? OR t.receiver_id = ? ORDER BY t.created_at DESC";
        return getTransactionsByQuery(sql, userId, userId);
    }

    // Get transactions with filters
    public List<Transaction> findWithFilters(int userId, String transactionType, String status, LocalDate startDate,
            LocalDate endDate, double minAmount, double maxAmount) {
        StringBuilder sql = new StringBuilder();
        sql.append(
                "SELECT t.*, s.full_name as sender_name, r.full_name as receiver_name FROM transactions t LEFT JOIN users s ON t.sender_id = s.user_id LEFT JOIN users r ON t.receiver_id = r.user_id WHERE (t.sender_id = ? OR t.receiver_id = ?) ");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(userId);
        if (transactionType != null && !transactionType.isEmpty()) {
            sql.append("AND t.transaction_type = ? ");
            params.add(transactionType);
        }
        if (status != null && !status.isEmpty()) {
            sql.append("AND t.status = ? ");
            params.add(status);
        }
        if (startDate != null) {
            sql.append("AND DATE(t.created_at) >= ? ");
            params.add(Date.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append("AND DATE(t.created_at) <= ? ");
            params.add(Date.valueOf(endDate));
        }
        if (minAmount > 0) {
            sql.append("AND t.amount >= ? ");
            params.add(minAmount);
        }
        if (maxAmount > 0) {
            sql.append("AND t.amount <= ? ");
            params.add(maxAmount);
        }
        sql.append("ORDER BY t.created_at DESC");
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            return transactions;
        } catch (SQLException e) {
            logger.error("Error finding filtered transactions: " + e.getMessage());
            throw new RuntimeException("Failed to find transactions", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Update transaction status
    public boolean updateStatus(int transactionId, String status) {
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, transactionId);
            int affectedRows = stmt.executeUpdate();
            logger.info("Transaction " + transactionId + " status updated to: " + status);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating transaction status: " + e.getMessage());
            throw new RuntimeException("Failed to update transaction", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Get total amount sent by a user
    public double getTotalSent(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions WHERE sender_id = ? AND status = 'COMPLETED' AND transaction_type = 'SEND'";
        return getTotal(sql, userId);
    }

    // Get total amount received by a user
    public double getTotalReceived(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions WHERE receiver_id = ? AND status = 'COMPLETED' AND transaction_type = 'SEND'";
        return getTotal(sql, userId);
    }

    // Helper method to execute aggregate queries
    private double getTotal(String sql, int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error calculating total: " + e.getMessage());
            return 0;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Helper method to get transactions by query
    private List<Transaction> getTransactionsByQuery(String sql, int param1, int param2) {
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, param1);
            stmt.setInt(2, param2);
            rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            return transactions;
        } catch (SQLException e) {
            logger.error("Error getting transactions: " + e.getMessage());
            throw new RuntimeException("Failed to get transactions", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Map ResultSet to Transaction object
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setSenderId(rs.getInt("sender_id"));
        transaction.setReceiverId(rs.getInt("receiver_id"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setStatus(rs.getString("status"));
        transaction.setNote(rs.getString("note"));
        // Try to get joined user names
        try {
            transaction.setSenderName(rs.getString("sender_name"));
            transaction.setReceiverName(rs.getString("receiver_name"));
        } catch (SQLException e) {
            // Column might not exist if not joined
        }
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            transaction.setCreatedAt(createdAt.toLocalDateTime());
        }
        return transaction;
    }

    // Close database resources
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null) {
                // conn.close(); // Shared connection
            }
        } catch (SQLException e) {
            logger.error("Error closing resources: " + e.getMessage());
        }
    }
}
