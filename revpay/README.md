# RevPay - Console-Based Financial Application

## Overview
RevPay is a comprehensive Java console application for secure digital payments and money management. It supports both personal and business accounts with a wide range of financial features.

## Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Quick Start

### 1. Database Setup
```bash
# Login to MySQL and run the schema script
mysql -u root -p < sql/schema.sql
```

### 2. Configure Database Connection
Edit `src/main/resources/db.properties` with your MySQL credentials:
```properties
db.url=jdbc:mysql://localhost:3306/revpay_db
db.username=root
db.password=your_password
```

### 3. Build and Run
```bash
# Build the project
mvn clean compile

# Run the application
mvn exec:java

# Or run tests
mvn test
```

### 4. Using IntelliJ IDEA
1. Open the project folder in IntelliJ
2. Wait for Maven to import dependencies
3. Navigate to `src/main/java/com/revpay/RevPayApp.java`
4. Right-click and select "Run 'RevPayApp.main()'"

## Features

### Personal Account
- User registration and login with 2FA simulation
- Send money to other users (by email, phone, or ID)
- Request money from other users
- Accept/Decline incoming money requests
- Add/Remove credit/debit cards (encrypted storage)
- View transaction history with filters
- Add/Withdraw money from wallet
- In-app notifications
- Password recovery with security questions

### Business Account
All personal features plus:
- Business registration with verification
- Create and manage invoices
- Accept payments from customers
- Apply for business loans
- Make loan payments
- Business analytics dashboard
- Revenue reports

### Security Features
- BCrypt password hashing
- AES-256 card encryption
- Transaction PIN verification
- Account lockout (3 failed attempts)
- Session timeout (30 minutes)
- Two-factor authentication (simulated)

## Project Structure
```
revpay/
├── pom.xml                 # Maven configuration
├── sql/
│   └── schema.sql          # Database creation script
├── src/
│   ├── main/
│   │   ├── java/com/revpay/
│   │   │   ├── RevPayApp.java        # Main entry point
│   │   │   ├── config/               # Database configuration
│   │   │   ├── model/                # Entity classes
│   │   │   ├── dao/                  # Data access layer
│   │   │   ├── service/              # Business logic
│   │   │   ├── security/             # Security utilities
│   │   │   ├── ui/                   # Console interface
│   │   │   └── util/                 # Helpers
│   │   └── resources/
│   │       ├── db.properties         # Database config
│   │       └── log4j2.xml            # Logging config
│   └── test/                         # JUnit tests
└── docs/                             # Documentation
```

## Technologies
- Java 17
- JDBC + MySQL Connector
- BCrypt (password hashing)
- AES-256 (encryption)
- Log4J 2 (logging)
- JUnit 5 (testing)
- Maven (build)

## License
This project is for educational purposes.
