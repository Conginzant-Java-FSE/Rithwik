// User DAO Class

import java.sql.*;

// Data Access Object for User operations
public class UserDAO {

    public boolean register(User user) {
        try {
            String sql = "INSERT INTO users(username,email,password,status) VALUES(?,?,?,?)";
            PreparedStatement ps = JdbcConnection.getConnection().prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, "ACTIVE");
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public User login(String username, String password) {
        try {
            String sql = "SELECT * FROM users WHERE username=? AND password=? AND status='ACTIVE'";
            PreparedStatement ps = JdbcConnection.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password")); // Added because UserDAO usually sets password
                u.setStatus(rs.getString("status"));
                return u;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public User viewProfile(int id) {
        try {
            String sql = "SELECT * FROM users WHERE id=?";
            PreparedStatement ps = JdbcConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setStatus(rs.getString("status"));
                return u;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public boolean updateProfile(int id, String email) {
        try {
            String sql = "UPDATE users SET email=? WHERE id=?";
            PreparedStatement ps = JdbcConnection.getConnection().prepareStatement(sql);
            ps.setString(1, email);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
