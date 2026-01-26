package org.example;

import org.example.config.AppConfig;
import org.example.config.BeanConfig;
import org.example.controller.MessageController;
import org.example.controller.UserController;
import org.example.service.MessageService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

// Main class to demonstrate different Spring configuration methods
public class SpringConfigDemo {
   public static void main(String[] args) {
      ApplicationContext annotationContext = new AnnotationConfigApplicationContext(AppConfig.class);
      UserController userController = annotationContext.getBean(UserController.class);
      System.out.println("UserController: " + userController);

      ApplicationContext beanContext = new AnnotationConfigApplicationContext(BeanConfig.class);
      MessageController msgController = beanContext.getBean(MessageController.class);
      msgController.showMessage("Khyathi");

      ApplicationContext xmlContext = new ClassPathXmlApplicationContext("beans.xml");
      MessageController xmlController = (MessageController) xmlContext.getBean("messageControllerSetter");
      xmlController.showMessage("Student");
   }
}
