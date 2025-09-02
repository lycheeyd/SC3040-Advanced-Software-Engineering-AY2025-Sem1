package com.example.CalowinTrip;

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException; 

public class DatabaseConnection { 

    private static String url = "jdbc:sqlserver://172.21.146.188:1000;database=Calowin; encrypt=false;encrypt=true;trustServerCertificate=true";  
    private static String user = "calowin"; 
    private static String password = "meowmeow"; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    

    // For testing connection (optional)
    public static void main(String[] args) { 
        try (Connection connection = getConnection()) { 
            System.out.println("Connection successful!"); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } 
    } 
}