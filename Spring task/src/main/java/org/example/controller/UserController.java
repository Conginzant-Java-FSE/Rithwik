package org.example.controller;

import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
        System.out.println("[UserController] Created with Constructor Injection");
    }

    public UserService getService() {
        return service;
    }
}
