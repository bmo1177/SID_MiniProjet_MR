/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import config.DatabaseConnection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== Test de Connexion MySQL ===");
        
        DatabaseConnection db = DatabaseConnection.getInstance();
        
        if (db.testConnection()) {
            System.out.println("✅ Connexion réussie !");
            System.out.println("Base de données : gestion_scolarite");
        } else {
            System.out.println("❌ Échec de la connexion");
            System.out.println("Vérifiez :");
            System.out.println("1. XAMPP MySQL est démarré");
            System.out.println("2. database.properties est correct");
            System.out.println("3. Le driver MySQL est ajouté");
        }
    }
}