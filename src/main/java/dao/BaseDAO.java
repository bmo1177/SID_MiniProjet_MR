/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de base pour tous les DAO
 * Fournit les méthodes CRUD communes
 * @param <T> Type de l'entité
 */
public abstract class BaseDAO<T> {
    protected Connection connection;
    
    public BaseDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Récupère toutes les entités
     */
    public abstract List<T> findAll() throws SQLException;
    
    /**
     * Récupère une entité par son ID
     */
    public abstract T findById(int id) throws SQLException;
    
    /**
     * Insère une nouvelle entité
     */
    public abstract int insert(T entity) throws SQLException;
    
    /**
     * Met à jour une entité existante
     */
    public abstract boolean update(T entity) throws SQLException;
    
    /**
     * Supprime une entité
     */
    public abstract boolean delete(int id) throws SQLException;
    
    /**
     * Obtient la connexion de base de données
     * @return Connection active
     */
    public Connection getConnection() {
        return this.connection;
    }
    
    /**
     * Ferme les ressources JDBC
     */
    protected void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
        }
    }
    
    /**
     * Exécute une requête SELECT et retourne une liste de résultats
     */
    public <R> List<R> executeQuery(String sql, RowMapper<R> mapper, Object... params) 
            throws SQLException {
        List<R> results = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(sql);
            setParameters(stmt, params);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                results.add(mapper.mapRow(rs));
            }
        } finally {
            closeResources(rs, stmt);
        }
        
        return results;
    }
    
    /**
     * Exécute une requête UPDATE/INSERT/DELETE
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = null;
        
        try {
            stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setParameters(stmt, params);
            int affectedRows = stmt.executeUpdate();
            
            // Retourne l'ID généré pour les INSERT
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            
            return affectedRows;
        } finally {
            if (stmt != null) stmt.close();
        }
    }
    
    /**
     * Définit les paramètres d'une PreparedStatement
     */
    protected void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            
            if (param == null) {
                stmt.setNull(i + 1, Types.NULL);
            } else if (param instanceof String) {
                stmt.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                stmt.setInt(i + 1, (Integer) param);
            } else if (param instanceof Double) {
                stmt.setDouble(i + 1, (Double) param);
            } else if (param instanceof java.time.LocalDate) {
                stmt.setDate(i + 1, Date.valueOf((java.time.LocalDate) param));
            } else if (param instanceof java.time.LocalDateTime) {
                stmt.setTimestamp(i + 1, Timestamp.valueOf((java.time.LocalDateTime) param));
            } else if (param instanceof Boolean) {
                stmt.setBoolean(i + 1, (Boolean) param);
            } else {
                stmt.setObject(i + 1, param);
            }
        }
    }
    
    /**
     * Interface fonctionnelle pour mapper un ResultSet vers un objet
     */
    @FunctionalInterface
    public interface RowMapper<R> {
        R mapRow(ResultSet rs) throws SQLException;
    }
}