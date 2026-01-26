package org.example.controller;

import org.example.service.MessageService;
import org.springframework.stereotype.Component;

// This controller handles message display and demonstrates constructor and setter injection
@Component
public class MessageController {
    private MessageService service;

    public MessageController() {
        System.out.println("[MessageController] Created with default constructor");
    }

    public MessageController(MessageService service) {
        this.service = service;
        System.out.println("[MessageController] Created with Constructor Injection");
    }

    public void setService(MessageService service) {
        this.service = service;
        System.out.println("[MessageController] Service set via Setter Injection");
    }

    public void showMessage(String name) {
        if (service != null) {
            System.out.println(service.getGreeting(name));
        } else {
            System.out.println("Service not injected!");
        }
    }
}
