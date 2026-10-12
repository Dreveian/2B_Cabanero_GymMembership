package com.scc.dao;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class UserDao {

    public boolean registerUser(String fullname, String username, String password, String role){
        String sql = "INSERT INTO users(fullname, username, password, role, status) VALUE(?,?,?,?, 'PENDING')";

            try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){

                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, role.toUpperCase());
                pstmt.executeUpdate();
                System.out.println("-> Succesful Registration, Waiting for Approval!");
                return true;
            }catch (SQLException e){
            System.err.println("Failed: "+ e.getMessage());
            return false;
            }
    }

}
