# How to Run RevPay in IntelliJ IDEA

Follow these steps to successfully run the RevPay application in IntelliJ IDEA.

## 1. Open the Project
1.  Launch **IntelliJ IDEA**.
2.  Click **Open** or **File > Open**.
3.  Navigate to and select the folder:
    `c:\Users\khyathi\OneDrive\Documents\Coginizant Project\revpay`
4.  Click **OK**.

## 2. Configure project SDK (Important!)
1.  Go to **File > Project Structure...**
2.  In the **Project** settings (left sidebar), look at **SDK**.
3.  Ensure a **Java 17 SDK** (or later) is selected.
    *   If `<No SDK>` is shown, click it -> **Add SDK** -> **Download JDK** -> Select Version 17 -> Download.
4.  Click **Apply** and **OK**.

## 3. Load Maven Dependencies
IntelliJ needs to download the libraries (like MySQL driver, Log4j2) defined in `pom.xml`.
1.  Look for the **Maven** tool window (usually on the right sidebar).
2.  Click the **Reload All Maven Projects** button (icon with two circular arrows).
3.  Wait for the progress bar at the bottom to finish.

## 4. Setup Database (Crucial First Step!)
**You must run this once to create the tables and fix the "Unknown column" error.**

1.  In the Project view (left sidebar), navigate to:
    `src > main > java > com > revpay > util`
2.  Right-click on **DatabaseReset.java**.
3.  Select **Run 'DatabaseReset.main()'**.
4.  Wait for the console to say "DATABASE SETUP COMPLETED SUCCESSFULLY".

*Note: This script automatically applies the latest schema and loads test data.*

## 5. Run the Application
1.  In the Project view (left sidebar), navigate to:
    `src > main > java > com > revpay`
2.  Locate **RevPayApp.java**.
3.  **Right-click** on `RevPayApp.java`.
4.  Select **Run 'RevPayApp.main()'** (green play icon).

## 6. Using the Application
Once running, the **Run** window at the bottom will show:
```text
RevPay Application starting...
...
==========================================
        WELCOME TO REVPAY
      Secure Payments Made Easy
==========================================
1. Login
2. Register (Personal)
3. Register (Business)
4. Forgot Password
5. Exit
Enter choice:
```
*   Click inside the **Run** window to type your input.

## Troubleshooting
*   **"Release version 17 not supported"**: Go to File > Settings > Build, Execution, Deployment > Compiler > Java Compiler. Set "Project bytecode version" to 17.
*   **Database Connection Error**: Verify your MySQL server is running and the password in `src/main/resources/db.properties` matches your local MySQL password.
