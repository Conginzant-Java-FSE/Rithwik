package org.example.service;

import org.example.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserDao dao;

    @Autowired
    public UserService(UserDao dao) {
        this.dao = dao;
        System.out.println("[UserService] Created with Constructor Injection");
    }

    public UserDao getDao() {
        return dao;
    }

    public void setDao(UserDao dao) {
        this.dao = dao;
        System.out.println("[UserService] Dao set via Setter Injection");
    }
}
