package org.example;

import org.example.config.AppConfig;
import org.example.dao.UserDao;
import org.example.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

// Application entry point for the user management console
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Scanner sc = new Scanner(System.in);
        UserDao dao = context.getBean(UserDao.class);
        User loggedUser = null;

        while (true) {
            System.out.println(
                    "\n1.Register 2.Login 3.View 4.Update 5.Exit");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    User u = new User();
                    System.out.print("Username: ");
                    u.setUsername(sc.next());
                    System.out.print("Email: ");
                    u.setEmail(sc.next());
                    System.out.print("Password: ");
                    u.setPassword(sc.next());
                    System.out.println(dao.register(u) ? "Registered" : "Failed");
                    break;
                case 2:
                    System.out.print("Username: ");
                    String un = sc.next();
                    System.out.print("Password: ");
                    String pw = sc.next();
                    loggedUser = dao.login(un, pw);
                    System.out.println(loggedUser != null ? "Login Success" : "Invalid Login");
                    break;
                case 3:
                    if (loggedUser == null) {
                        System.out.println("User Not Logged In. Please Login");
                        break;
                    }
                    System.out.println(dao.viewProfile(loggedUser.getId()).getEmail());
                    break;
                case 4:
                    System.out.print("New Email: ");
                    System.out.println(dao.updateProfile(loggedUser.getId(), sc.next()));
                    break;
                case 5:
                    System.exit(0);
            }
        }
    }
}
