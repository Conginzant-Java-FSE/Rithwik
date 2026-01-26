package org.example.config;

import org.example.controller.MessageController;
import org.example.service.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Java-based configuration for message beans
@Configuration
public class BeanConfig {

    @Bean
    public MessageService messageService() {
        MessageService service = new MessageService("Hello from @Bean!");
        System.out.println("[BeanConfig] Created MessageService bean");
        return service;
    }

    @Bean
    public MessageController messageController() {
        MessageController controller = new MessageController();
        controller.setService(messageService());
        System.out.println("[BeanConfig] Created MessageController with Setter Injection");
        return controller;
    }
}
