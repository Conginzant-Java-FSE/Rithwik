package SimpleJDBC;

import java.util.Scanner;

public class SimpleMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SimpleUserDAO dao = new SimpleUserDAO();

        System.out.println("--- Simple JDBC App ---");

        // 1. Register
        System.out.print("Enter Username to Register: ");
        String name = sc.next();
        System.out.print("Enter Email: ");
        String email = sc.next();
        System.out.print("Enter Password: ");
        String pass = sc.next();

        SimpleUser newUser = new SimpleUser(name, email, pass);
        dao.insertUser(newUser);

        // 2. Login
        System.out.println("\n--- Login ---");
        System.out.print("Enter Username: ");
        String loginName = sc.next();
        System.out.print("Enter Password: ");
        String loginPass = sc.next();

        SimpleUser loggedInUser = dao.getUser(loginName, loginPass);

        if (loggedInUser != null) {
            System.out.println("Login Successful! " + loggedInUser);

            // 3. Update
            System.out.println("\n--- Update Email ---");
            System.out.print("Enter new email: ");
            String newMail = sc.next();
            dao.updateEmail(loggedInUser.getId(), newMail);

            // 4. Delete
            System.out.println("\n--- Delete Account ---");
            System.out.print("Type 'YES' to delete account: ");
            String confirm = sc.next();
            if (confirm.equalsIgnoreCase("YES")) {
                dao.deleteUser(loggedInUser.getId());
            } else {
                System.out.println("Delete cancelled.");
            }

        } else {
            System.out.println("Login Failed!");
        }

        sc.close();
    }
}
