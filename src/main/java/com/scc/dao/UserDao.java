package com.scc.dao;

import com.scc.model.User;

import javax.xml.crypto.Data;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class UserDao {

    public UserDao() {

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            defaultAdminGenerator(conn);
        } catch (SQLException e) {
            System.err.println("Error seeding default admin: " + e.getMessage());
        }
    }

    public void defaultAdminGenerator(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE role ='ADMIN'";
        String insertSql = "INSERT INTO users(username, password, fullname, role, status) VALUES( 'admin', 'admin1', 'admin', 'ADMIN', 'APPROVED')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(insertSql);
                System.out.println("-> Default Admin created");
            }
        } catch (SQLException e) {
            System.err.println("Error seeding defualt admin: " + e.getMessage());
        }

    }

    public boolean registerUser(String fullname, String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, fullname,role, status) VALUES(?,?,?,?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullname);
            pstmt.setString(4, role.toUpperCase());
            pstmt.executeUpdate();
            System.out.println("\n-> Succesful Registration, Waiting for Approval!");
            return true;
        } catch (SQLException e) {
            System.err.println("Failed: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT user_id, username, password, fullname, role, status, created_at " + "FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    if ("PENDING".equalsIgnoreCase(status)) {
                        System.out.println("-> Account is pending, Waiting for Approval");
                        return null;
                    }
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("fullname"),
                            rs.getString("role"),
                            status,
                            rs.getString("created_at")
                    );
                } else {
                    System.out.println(" -> Invalid username or password");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login " + e.getMessage());
        }
        return null;
    }

}
