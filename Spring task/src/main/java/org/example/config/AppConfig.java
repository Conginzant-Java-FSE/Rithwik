package org.example.config;

import org.example.controller.MessageController;
import org.example.service.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = "org.example")
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    public MessageService messageService() {
        MessageService service = new MessageService("Hello All!!!");
        return service;
    }

    // @Bean
    // public MessageController messageController(){
    // return new MessageController(messageService()); //constructor inj
    // }

    @Bean
    public MessageController messageController() {
        MessageController controller = new MessageController();
        controller.setService(messageService());
        return controller;
    }

}
