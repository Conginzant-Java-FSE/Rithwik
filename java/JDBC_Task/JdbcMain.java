// Main JDBC App

import java.util.Scanner;

// Main Application
public class JdbcMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserDAO dao = new UserDAO();
        User loggedInUser = null;

        System.out.println("=== User Management System ===");

        while (true) {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Update Email");
            System.out.println("4. Delete Account");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1: // Register
                    System.out.print("Enter Name: ");
                    String name = sc.next();
                    System.out.print("Enter Email: ");
                    String email = sc.next();
                    System.out.print("Enter Password: ");
                    String pass = sc.next();

                    User newUser = new User(name, email, pass);
                    if (dao.registerUser(newUser)) {
                        System.out.println(">> Registration Successful!");
                    } else {
                        System.out.println(">> Registration Failed!");
                    }
                    break;

                case 2: // Login
                    System.out.print("Username: ");
                    String uname = sc.next();
                    System.out.print("Password: ");
                    String upass = sc.next();

                    loggedInUser = dao.loginUser(uname, upass);
                    if (loggedInUser != null) {
                        System.out.println(">> Login Successful! Welcome " + loggedInUser.getUsername());
                    } else {
                        System.out.println(">> Invalid Credentials!");
                    }
                    break;

                case 3: // Update Email
                    if (loggedInUser == null) {
                        System.out.println(">> Please login first!");
                        break;
                    }
                    System.out.print("Enter new email: ");
                    String newMail = sc.next();
                    if (dao.updateEmail(loggedInUser.getId(), newMail)) {
                        System.out.println(">> Email Updated!");
                    } else {
                        System.out.println(">> Update Failed!");
                    }
                    break;

                case 4: // Delete
                    if (loggedInUser == null) {
                        System.out.println(">> Please login first!");
                        break;
                    }
                    if (dao.deleteUser(loggedInUser.getId())) {
                        System.out.println(">> Account Deleted. Bye!");
                        loggedInUser = null; // Logout
                    } else {
                        System.out.println(">> Delete Failed!");
                    }
                    break;

                case 5:
                    System.out.println("Exiting...");
                    sc.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}
