package services;

import config.DatabaseConnection;
import utils.PasswordUtils;
import java.sql.*;

public class LoginService {
    
    public boolean authenticate(String login, String password) {
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            Connection conn = db.getConnection();
            
            String sql = "SELECT password_hash FROM utilisateur WHERE login = ? AND actif = TRUE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return PasswordUtils.verifyPassword(password, storedHash);
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur d'authentification: " + e.getMessage());
            return false;
        }
    }
    
    public String getUserRole(String login) {
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            Connection conn = db.getConnection();
            
            String sql = "SELECT role FROM utilisateur WHERE login = ? AND actif = TRUE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next() ? rs.getString("role") : null;
        } catch (SQLException e) {
            System.err.println("Erreur récupération rôle: " + e.getMessage());
            return null;
        }
    }
}