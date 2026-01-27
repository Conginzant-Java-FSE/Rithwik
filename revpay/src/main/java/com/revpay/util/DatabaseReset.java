package com.revpay.util;

import com.revpay.config.DatabaseConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseReset {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   REVPAY DATABASE RESET & SETUP TOOL");
        System.out.println("==========================================");

        try {
            // 1. Initialize Config
            DatabaseConfig.initialize();

            // 2. Resolve paths
            Path projectDir = Paths.get(".").toAbsolutePath().normalize();
            Path schemaPath = projectDir.resolve("sql/schema.sql");
            Path dataPath = projectDir.resolve("sql/sample_data.sql");

            System.out.println("Project Directory: " + projectDir);

            // 3. Execute Schema
            if (Files.exists(schemaPath)) {
                System.out.println("\n[1/2] Executing Schema Script...");
                executeScript(schemaPath);
                System.out.println("SUCCESS: Schema applied.");
            } else {
                System.err.println("ERROR: Could not find " + schemaPath);
                return;
            }

            // 4. Execute Sample Data
            if (Files.exists(dataPath)) {
                System.out.println("\n[2/2] Executing Sample Data Script...");
                executeScript(dataPath);
                System.out.println("SUCCESS: Sample data loaded.");
            } else {
                System.err.println("WARNING: Could not find " + dataPath);
            }

            System.out.println("\n==========================================");
            System.out.println("   DATABASE SETUP COMPLETED SUCCESSFULLY");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("\nFATAL ERROR during database setup:");
            e.printStackTrace();
        }
    }

    private static void executeScript(Path filePath) throws Exception {
        String content = Files.readString(filePath);

        // Remove comments
        content = content.replaceAll("--.*", ""); // Single line -- comments

        // Split by semicolon
        List<String> statements = Arrays.stream(content.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement()) {

            for (String sql : statements) {
                // Skip USE command if using specific DB connection string generally,
                // but usually harmless to try.
                // We'll try-catch individual statements just in case of minor non-fatal issues
                // (like drops)
                try {
                    stmt.execute(sql);
                } catch (Exception e) {
                    if (sql.toUpperCase().startsWith("DROP")) {
                        // Ignore drop errors
                    } else {
                        System.err.println(
                                "Failed to execute: " + (sql.length() > 50 ? sql.substring(0, 50) + "..." : sql));
                        throw e;
                    }
                }
            }
        }
    }
}
