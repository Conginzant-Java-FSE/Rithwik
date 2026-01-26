package org.example.dao;

import org.example.config.DBConnection;
import org.example.model.User;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class UserDaoImpl implements UserDao {

    @Override
    public boolean register(User user) {
        try {
            String sql = "INSERT INTO users(username,email,password,status) VALUES(?,?,?,?)";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
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
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
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
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
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
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, email);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
