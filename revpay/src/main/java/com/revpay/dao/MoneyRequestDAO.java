package com.revpay.dao;

import com.revpay.config.DatabaseConfig;
import com.revpay.model.MoneyRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for MoneyRequest operations
public class MoneyRequestDAO {

    private static final Logger logger = LogManager.getLogger(MoneyRequestDAO.class);

    // Create a new money request
    public MoneyRequest createRequest(MoneyRequest request) {
        String sql = "INSERT INTO money_requests (requester_id, target_id, amount, note, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, request.getRequesterId());
            stmt.setInt(2, request.getTargetId());
            stmt.setDouble(3, request.getAmount());
            stmt.setString(4, request.getNote());
            stmt.setString(5, request.getStatus());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    request.setRequestId(rs.getInt(1));
                    request.setCreatedAt(LocalDateTime.now());
                    logger.info("Money request created with ID: " + request.getRequestId());
                }
            }
            return request;
        } catch (SQLException e) {
            logger.error("Error creating money request: " + e.getMessage());
            throw new RuntimeException("Failed to create money request", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find request by ID
    public MoneyRequest findById(int requestId) {
        String sql = "SELECT mr.*, r.full_name as requester_name, t.full_name as target_name FROM money_requests mr LEFT JOIN users r ON mr.requester_id = r.user_id LEFT JOIN users t ON mr.target_id = t.user_id WHERE mr.request_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, requestId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToMoneyRequest(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding money request: " + e.getMessage());
            throw new RuntimeException("Failed to find money request", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find incoming requests for a user
    public List<MoneyRequest> findIncomingRequests(int userId) {
        String sql = "SELECT mr.*, r.full_name as requester_name, t.full_name as target_name FROM money_requests mr LEFT JOIN users r ON mr.requester_id = r.user_id LEFT JOIN users t ON mr.target_id = t.user_id WHERE mr.target_id = ? AND mr.status = 'PENDING' ORDER BY mr.created_at DESC";
        return getRequestsByQuery(sql, userId);
    }

    // Find outgoing requests for a user
    public List<MoneyRequest> findOutgoingRequests(int userId) {
        String sql = "SELECT mr.*, r.full_name as requester_name, t.full_name as target_name FROM money_requests mr LEFT JOIN users r ON mr.requester_id = r.user_id LEFT JOIN users t ON mr.target_id = t.user_id WHERE mr.requester_id = ? AND mr.status = 'PENDING' ORDER BY mr.created_at DESC";
        return getRequestsByQuery(sql, userId);
    }

    // Find all requests for a user
    public List<MoneyRequest> findAllByUserId(int userId) {
        String sql = "SELECT mr.*, r.full_name as requester_name, t.full_name as target_name FROM money_requests mr LEFT JOIN users r ON mr.requester_id = r.user_id LEFT JOIN users t ON mr.target_id = t.user_id WHERE mr.requester_id = ? OR mr.target_id = ? ORDER BY mr.created_at DESC";
        List<MoneyRequest> requests = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToMoneyRequest(rs));
            }
            return requests;
        } catch (SQLException e) {
            logger.error("Error finding all requests: " + e.getMessage());
            throw new RuntimeException("Failed to find requests", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Update request status
    public boolean updateStatus(int requestId, String status) {
        String sql = "UPDATE money_requests SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE request_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            int affectedRows = stmt.executeUpdate();
            logger.info("Money request " + requestId + " status updated to: " + status);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating request status: " + e.getMessage());
            throw new RuntimeException("Failed to update request", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Helper to get requests by query
    private List<MoneyRequest> getRequestsByQuery(String sql, int userId) {
        List<MoneyRequest> requests = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToMoneyRequest(rs));
            }
            return requests;
        } catch (SQLException e) {
            logger.error("Error getting requests: " + e.getMessage());
            throw new RuntimeException("Failed to get requests", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Map ResultSet to MoneyRequest object
    private MoneyRequest mapResultSetToMoneyRequest(ResultSet rs) throws SQLException {
        MoneyRequest request = new MoneyRequest();
        request.setRequestId(rs.getInt("request_id"));
        request.setRequesterId(rs.getInt("requester_id"));
        request.setTargetId(rs.getInt("target_id"));
        request.setAmount(rs.getDouble("amount"));
        request.setNote(rs.getString("note"));
        request.setStatus(rs.getString("status"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            request.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            request.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        try {
            request.setRequesterName(rs.getString("requester_name"));
            request.setTargetName(rs.getString("target_name"));
        } catch (SQLException e) {
            // Columns might not exist
        }
        return request;
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
