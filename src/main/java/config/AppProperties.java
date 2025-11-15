/*
 * Gestionnaire de propriétés de l'application
 * Centralise la configuration pour production
 */
package config;

import java.io.*;
import java.util.Properties;
import utils.Logger;

public class AppProperties {
    private static AppProperties instance;
    private Properties properties;
    private final String CONFIG_FILE = "config/application.properties";
    
    private AppProperties() {
        loadProperties();
    }
    
    public static synchronized AppProperties getInstance() {
        if (instance == null) {
            instance = new AppProperties();
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        
        // Valeurs par défaut
        setDefaultProperties();
        
        // Charger depuis le fichier
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                Logger.info("✅ Configuration chargée depuis: " + CONFIG_FILE);
            } else {
                // Créer le fichier de config s'il n'existe pas
                createDefaultConfigFile();
                Logger.info("📝 Fichier de configuration créé avec valeurs par défaut");
            }
        } catch (IOException e) {
            Logger.error("❌ Erreur chargement configuration: " + e.getMessage());
        }
    }
    
    private void setDefaultProperties() {
        // Base de données
        properties.setProperty("db.host", "localhost");
        properties.setProperty("db.port", "3306");
        properties.setProperty("db.name", "gestion_scolarite");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.pool.min", "5");
        properties.setProperty("db.pool.max", "20");
        properties.setProperty("db.pool.timeout", "30000");
        
        // Application
        properties.setProperty("app.name", "Gestion Scolarité");
        properties.setProperty("app.version", "2.0.0-PROD");
        properties.setProperty("app.university", "Université Ibn Khaldoun - Tiaret");
        properties.setProperty("app.environment", "production");
        properties.setProperty("app.debug", "false");
        
        // Sécurité
        properties.setProperty("security.password.min.length", "8");
        properties.setProperty("security.session.timeout", "3600");
        properties.setProperty("security.max.login.attempts", "3");
        properties.setProperty("security.lockout.duration", "900");
        
        // Fichiers et exports
        properties.setProperty("files.upload.max.size", "10485760");
        properties.setProperty("files.export.directory", "exports");
        properties.setProperty("files.backup.directory", "backups");
        properties.setProperty("files.temp.directory", "temp");
        
        // Email (pour notifications)
        properties.setProperty("email.smtp.host", "smtp.univ-tiaret.dz");
        properties.setProperty("email.smtp.port", "587");
        properties.setProperty("email.smtp.auth", "true");
        properties.setProperty("email.smtp.starttls", "true");
        properties.setProperty("email.from", "scolarite@univ-tiaret.dz");
        
        // Logging
        properties.setProperty("log.level", "INFO");
        properties.setProperty("log.file.max.size", "50MB");
        properties.setProperty("log.file.max.history", "30");
        properties.setProperty("log.directory", "logs");
        
        // Interface
        properties.setProperty("ui.theme", "system");
        properties.setProperty("ui.language", "fr");
        properties.setProperty("ui.font.size", "12");
        properties.setProperty("ui.auto.save", "true");
        
        // Performance
        properties.setProperty("cache.enabled", "true");
        properties.setProperty("cache.size", "1000");
        properties.setProperty("cache.ttl", "3600");
        
        // Backup automatique
        properties.setProperty("backup.auto.enabled", "true");
        properties.setProperty("backup.auto.time", "02:00");
        properties.setProperty("backup.retention.days", "30");
    }
    
    private void createDefaultConfigFile() {
        try {
            File configDir = new File("src/main/resources/config");
            configDir.mkdirs();
            
            File configFile = new File(configDir, "application.properties");
            try (FileOutputStream output = new FileOutputStream(configFile)) {
                properties.store(output, "Configuration Gestion Scolarité - Université Ibn Khaldoun");
            }
        } catch (IOException e) {
            Logger.error("Erreur création fichier config: " + e.getMessage());
        }
    }
    
    // Getters avec validation
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            Logger.warning("Propriété invalide " + key + ", utilisation valeur par défaut: " + defaultValue);
            return defaultValue;
        }
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }
    
    // Configuration spécifique
    public String getDatabaseUrl() {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            getProperty("db.host"),
            getProperty("db.port"),
            getProperty("db.name"));
    }
    
    public boolean isProduction() {
        return "production".equals(getProperty("app.environment"));
    }
    
    public boolean isDebugMode() {
        return getBooleanProperty("app.debug", false);
    }
    
    // Sauvegarde des propriétés
    public void saveProperty(String key, String value) {
        properties.setProperty(key, value);
        saveProperties();
    }
    
    private void saveProperties() {
        try {
            File configFile = new File("src/main/resources/config/application.properties");
            try (FileOutputStream output = new FileOutputStream(configFile)) {
                properties.store(output, "Configuration mise à jour - " + java.time.LocalDateTime.now());
                Logger.info("✅ Configuration sauvegardée");
            }
        } catch (IOException e) {
            Logger.error("❌ Erreur sauvegarde configuration: " + e.getMessage());
        }
    }
    
    // Validation de la configuration
    public boolean validateConfiguration() {
        boolean valid = true;
        StringBuilder errors = new StringBuilder();
        
        // Vérifier les propriétés critiques
        if (getProperty("db.host") == null || getProperty("db.host").trim().isEmpty()) {
            errors.append("- Host de base de données manquant\n");
            valid = false;
        }
        
        if (getProperty("db.name") == null || getProperty("db.name").trim().isEmpty()) {
            errors.append("- Nom de base de données manquant\n");
            valid = false;
        }
        
        if (getIntProperty("db.pool.min", 0) <= 0) {
            errors.append("- Taille minimale du pool de connexions invalide\n");
            valid = false;
        }
        
        if (getIntProperty("security.password.min.length", 0) < 6) {
            errors.append("- Longueur minimale du mot de passe trop faible\n");
            valid = false;
        }
        
        if (!valid) {
            Logger.error("❌ Configuration invalide:\n" + errors.toString());
        }
        
        return valid;
    }
    
    public void printConfiguration() {
        Logger.info("📋 Configuration de l'application:");
        Logger.info("   App: " + getProperty("app.name") + " v" + getProperty("app.version"));
        Logger.info("   Environnement: " + getProperty("app.environment"));
        Logger.info("   Base: " + getProperty("db.host") + ":" + getProperty("db.port") + "/" + getProperty("db.name"));
        Logger.info("   Debug: " + isDebugMode());
    }
}