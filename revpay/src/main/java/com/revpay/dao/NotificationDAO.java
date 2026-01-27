package com.revpay.dao;

import com.revpay.config.DatabaseConfig;
import com.revpay.model.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for Notification operations
public class NotificationDAO {

    private static final Logger logger = LogManager.getLogger(NotificationDAO.class);

    // Create a new notification
    public Notification createNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, title, message, notification_type, is_read) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getTitle());
            stmt.setString(3, notification.getMessage());
            stmt.setString(4, notification.getNotificationType());
            stmt.setBoolean(5, notification.isRead());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    notification.setNotificationId(rs.getInt(1));
                    notification.setCreatedAt(LocalDateTime.now());
                    logger.info("Notification created with ID: " + notification.getNotificationId());
                }
            }
            return notification;
        } catch (SQLException e) {
            logger.error("Error creating notification: " + e.getMessage());
            throw new RuntimeException("Failed to create notification", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find notifications by user ID
    public List<Notification> findByUserId(int userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 50";
        List<Notification> notifications = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            return notifications;
        } catch (SQLException e) {
            logger.error("Error finding notifications: " + e.getMessage());
            throw new RuntimeException("Failed to find notifications", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find unread notifications
    public List<Notification> findUnreadByUserId(int userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            return notifications;
        } catch (SQLException e) {
            logger.error("Error finding unread notifications: " + e.getMessage());
            throw new RuntimeException("Failed to find notifications", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Mark notification as read
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, notificationId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error marking notification as read: " + e.getMessage());
            throw new RuntimeException("Failed to update notification", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Mark all notifications as read for a user
    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();
            logger.info("Marked " + affectedRows + " notifications as read for user: " + userId);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error marking all notifications as read: " + e.getMessage());
            throw new RuntimeException("Failed to update notifications", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Get unread notification count
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = FALSE";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error getting unread count: " + e.getMessage());
            return 0;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Map ResultSet to Notification object
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setNotificationType(rs.getString("notification_type"));
        notification.setRead(rs.getBoolean("is_read"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            notification.setCreatedAt(createdAt.toLocalDateTime());
        }
        return notification;
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
