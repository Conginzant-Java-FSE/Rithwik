package org.example.dao;

import org.example.model.User;

// Interface defining data access operations for User
public interface UserDao {
    boolean register(User user);

    User login(String username, String password);

    User viewProfile(int id);

    boolean updateProfile(int id, String email);
}