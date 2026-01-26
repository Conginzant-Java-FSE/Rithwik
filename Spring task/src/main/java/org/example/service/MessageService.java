package org.example.service;

import org.springframework.stereotype.Service;

// Service component for checking messages handling business logic
@Service
public class MessageService {
    private String message;

    public MessageService() {
        this.message = "Default Message";
    }

    public MessageService(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGreeting(String name) {
        return message + " Welcome, " + name + "!";
    }
}
