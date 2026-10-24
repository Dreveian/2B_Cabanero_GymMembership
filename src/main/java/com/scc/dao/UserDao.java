package com.scc.dao;

import com.scc.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public UserDao() {
        try (Connection conn = DatabaseConnection.getConnection()) {
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
                try (Statement ins = conn.createStatement()) {
                    ins.executeUpdate(insertSql);
                }
                System.out.println("-> Default Admin created");
            }
        } catch (SQLException e) {
            System.err.println("Error seeding default admin: " + e.getMessage());
        }
    }

    public boolean registerUser(String fullname, String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, fullname, role, status) VALUES(?,?,?,?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullname);
            pstmt.setString(4, role.toUpperCase());
            pstmt.executeUpdate();
            System.out.println("\n-> Successful Registration, Waiting for Approval!");
            return true;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                System.err.println("Failed: username already taken.");
            } else {
                System.err.println("Failed: " + e.getMessage());
            }
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT user_id, username, password, fullname, role, status, created_at "
                + "FROM users WHERE username = ? AND password = ?";

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
                    if ("REJECTED".equalsIgnoreCase(status)) {
                        System.out.println("-> Account was rejected. Contact the administrator.");
                        return null;
                    }
                    return mapUser(rs);
                } else {
                    System.out.println(" -> Invalid username or password");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login " + e.getMessage());
        }
        return null;
    }

    // ---------- Manage Users ----------

    // status = null -> all users; or "PENDING" / "APPROVED" / "REJECTED"
    public List<User> getUsers(String status) {
        String base = "SELECT user_id, username, password, fullname, role, status, created_at FROM users";
        String sql = (status == null) ? base + " ORDER BY user_id"
                : base + " WHERE status = ? ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (status != null) pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public User findById(int userId) {
        String sql = "SELECT user_id, username, password, fullname, role, status, created_at "
                + "FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStatus(int userId, String status) {
        return updateColumn("status", userId, status);
    }

    public boolean updateRole(int userId, String role) {
        return updateColumn("role", userId, role);
    }

    private boolean updateColumn(String column, int userId, String value) {
        String sql = "UPDATE users SET " + column + " = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, value);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("fullname"),
                rs.getString("role"),
                rs.getString("status"),
                rs.getString("created_at")
        );
    }
}