package SimpleJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SimpleUserDAO {

    // 1. Insert User
    public void insertUser(SimpleUser user) {
        String query = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = SimpleConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getStatus());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User inserted successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error inserting user: " + e.getMessage());
        }
    }

    // 2. Get User (Login)
    public SimpleUser getUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = SimpleConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                SimpleUser u = new SimpleUser();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setStatus(rs.getString("status"));
                return u;
            }
        } catch (Exception e) {
            System.out.println("Error getting user: " + e.getMessage());
        }
        return null;
    }

    // 3. Update Email
    public void updateEmail(int id, String newEmail) {
        String query = "UPDATE users SET email = ? WHERE id = ?";
        try (Connection conn = SimpleConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newEmail);
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User email updated successfully!");
            } else {
                System.out.println("User not found or update failed.");
            }
        } catch (Exception e) {
            System.out.println("Error updating email: " + e.getMessage());
        }
    }

    // 4. Delete User
    public void deleteUser(int id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = SimpleConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("User not found or delete failed.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }
}
