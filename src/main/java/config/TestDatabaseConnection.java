/*
 * Configuration de test sans vraie base de données
 * Permet de tester l'interface sans MySQL
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDatabaseConnection {
    private static TestDatabaseConnection instance;
    private Connection connection;
    
    // Configuration pour base de test H2 (en mémoire)
    private final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS gestion_scolarite";
    private final String DB_USER = "sa";
    private final String DB_PASSWORD = "";
    
    private TestDatabaseConnection() {
        connect();
    }
    
    public static synchronized TestDatabaseConnection getInstance() {
        if (instance == null) {
            instance = new TestDatabaseConnection();
        }
        return instance;
    }
    
    private void connect() {
        try {
            // Charger le driver H2
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            if (connection != null) {
                System.out.println("✅ Connexion base de test H2 établie");
                initializeTestData();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver H2 non trouvé, utilisation mode simulation");
            connection = null;
        } catch (SQLException e) {
            System.err.println("❌ Erreur connexion H2: " + e.getMessage());
            connection = null;
        }
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Erreur vérification connexion: " + e.getMessage());
        }
        return connection;
    }
    
    private void initializeTestData() {
        if (connection == null) return;
        
        try {
            // Créer tables minimales pour test
            connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS UTILISATEUR (" +
                "id_utilisateur INT AUTO_INCREMENT PRIMARY KEY, " +
                "login VARCHAR(50) UNIQUE NOT NULL, " +
                "mot_de_passe VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL, " +
                "actif BOOLEAN DEFAULT TRUE" +
                ")"
            );
            
            // Insérer utilisateurs de test
            connection.createStatement().execute(
                "INSERT INTO UTILISATEUR (login, mot_de_passe, role) VALUES " +
                "('admin', 'admin123', 'ADMIN'), " +
                "('etudiant', 'etudiant123', 'ETUDIANT'), " +
                "('enseignant', 'enseignant123', 'ENSEIGNANT'), " +
                "('scolarite', 'scolarite123', 'SCOLARITE'), " +
                "('direction', 'direction123', 'DIRECTION')"
            );
            
            System.out.println("✅ Données de test initialisées");
            
        } catch (SQLException e) {
            System.err.println("Erreur initialisation test: " + e.getMessage());
        }
    }
    
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur fermeture: " + e.getMessage());
        }
    }
}