package config;

import java.sql.*;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Production configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gestion_scolarite";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    private DatabaseConnection() {
        connect();
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    private void connect() {
        try {
            Class.forName(DB_DRIVER);
            
            String url = DB_URL + 
                "?useSSL=false" +
                "&serverTimezone=UTC" +
                "&allowPublicKeyRetrieval=true" +
                "&useUnicode=true" +
                "&characterEncoding=UTF-8";
            
            connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(true);
            
            System.out.println("✅ Connexion à la base de données établie");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion MySQL");
            System.err.println("Détails: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Erreur de reconnexion: " + e.getMessage());
        }
        return connection;
    }
    
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur de fermeture: " + e.getMessage());
        }
    }
}