package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String createUsers = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    username   TEXT NOT NULL UNIQUE,
                    password   TEXT NOT NULL,
                    fullname   TEXT NOT NULL,
                    role       TEXT NOT NULL CHECK (role IN ('ADMIN', 'FRONT_DESK', 'TRAINER')),
                    status     TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                );
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsers);

            System.out.println("-> Database initialized.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            System.exit(1);
        }
    }
}