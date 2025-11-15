/*
 * Gestionnaire de sécurité de niveau production
 * Gestion avancée de l'authentification et autorisation
 */
package security;

import models.Utilisateur;
import utils.Logger;
import org.mindrot.jbcrypt.BCrypt;
import config.AppProperties;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class SecurityManager {
    private static SecurityManager instance;
    private final ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    
    // Patterns de validation
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>/?]).{8,}$"
    );
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private SecurityManager() {
        Logger.info("🔐 Gestionnaire de sécurité initialisé");
        
        // Nettoyage périodique des tentatives expirées
        startCleanupThread();
    }
    
    public static synchronized SecurityManager getInstance() {
        if (instance == null) {
            instance = new SecurityManager();
        }
        return instance;
    }
    
    /**
     * Hache un mot de passe avec BCrypt
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mot de passe ne peut pas être vide");
        }
        
        validatePasswordStrength(plainPassword);
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
    
    /**
     * Vérifie un mot de passe haché
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            Logger.error("Erreur vérification mot de passe: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Valide la force d'un mot de passe
     */
    public void validatePasswordStrength(String password) throws SecurityException {
        AppProperties config = AppProperties.getInstance();
        int minLength = config.getIntProperty("security.password.min.length", 8);
        
        if (password == null || password.length() < minLength) {
            throw new SecurityException("Mot de passe trop court (minimum " + minLength + " caractères)");
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new SecurityException(
                "Mot de passe faible. Requis: " +
                "1 majuscule, 1 minuscule, 1 chiffre, 1 caractère spécial"
            );
        }
    }
    
    /**
     * Vérifie les tentatives de connexion (protection brute force)
     */
    public boolean checkLoginAttempts(String username) {
        AppProperties config = AppProperties.getInstance();
        int maxAttempts = config.getIntProperty("security.max.login.attempts", 3);
        int lockoutDuration = config.getIntProperty("security.lockout.duration", 900); // 15 minutes
        
        LoginAttempt attempt = loginAttempts.computeIfAbsent(username, k -> new LoginAttempt());
        
        // Vérifier si le compte est verrouillé
        if (attempt.isLocked()) {
            LocalDateTime unlockTime = attempt.getLastAttempt().plusSeconds(lockoutDuration);
            if (LocalDateTime.now().isBefore(unlockTime)) {
                long remainingMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), unlockTime);
                Logger.warning("🔒 Tentative sur compte verrouillé: " + username + 
                             " (déverrouillage dans " + remainingMinutes + " min)");
                return false;
            } else {
                // Déverrouiller le compte
                attempt.reset();
                Logger.info("🔓 Compte déverrouillé automatiquement: " + username);
            }
        }
        
        return attempt.getFailedAttempts() < maxAttempts;
    }
    
    /**
     * Enregistre une tentative de connexion échouée
     */
    public void recordFailedLogin(String username) {
        LoginAttempt attempt = loginAttempts.computeIfAbsent(username, k -> new LoginAttempt());
        attempt.addFailedAttempt();
        
        AppProperties config = AppProperties.getInstance();
        int maxAttempts = config.getIntProperty("security.max.login.attempts", 3);
        
        if (attempt.getFailedAttempts() >= maxAttempts) {
            attempt.lock();
            Logger.warning("🚨 Compte verrouillé après " + maxAttempts + " tentatives: " + username);
        } else {
            Logger.warning("❌ Tentative échouée " + attempt.getFailedAttempts() + "/" + maxAttempts + 
                         " pour: " + username);
        }
    }
    
    /**
     * Enregistre une connexion réussie
     */
    public void recordSuccessfulLogin(String username) {
        // Réinitialiser les tentatives échouées
        loginAttempts.remove(username);
        Logger.info("✅ Connexion réussie: " + username);
    }
    
    /**
     * Crée une session utilisateur
     */
    public String createSession(Utilisateur user) {
        String sessionId = generateSecureToken();
        UserSession session = new UserSession(sessionId, user);
        activeSessions.put(sessionId, session);
        
        Logger.info("🎫 Session créée pour: " + user.getLogin() + " (ID: " + sessionId.substring(0, 8) + "...)");
        return sessionId;
    }
    
    /**
     * Valide une session utilisateur
     */
    public UserSession validateSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return null;
        }
        
        UserSession session = activeSessions.get(sessionId);
        if (session == null) {
            return null;
        }
        
        AppProperties config = AppProperties.getInstance();
        int timeout = config.getIntProperty("security.session.timeout", 3600);
        
        if (session.isExpired(timeout)) {
            activeSessions.remove(sessionId);
            Logger.info("🕐 Session expirée supprimée: " + sessionId.substring(0, 8) + "...");
            return null;
        }
        
        // Mettre à jour la dernière activité
        session.updateLastActivity();
        return session;
    }
    
    /**
     * Ferme une session
     */
    public void closeSession(String sessionId) {
        if (sessionId != null) {
            UserSession session = activeSessions.remove(sessionId);
            if (session != null) {
                Logger.info("👋 Session fermée: " + session.getUser().getLogin());
            }
        }
    }
    
    /**
     * Valide un email
     */
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Nettoie les données d'entrée (prévention XSS)
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim()
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\"", "&quot;")
                    .replaceAll("'", "&#x27;")
                    .replaceAll("/", "&#x2F;");
    }
    
    /**
     * Génère un token sécurisé
     */
    private String generateSecureToken() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        
        StringBuilder token = new StringBuilder();
        for (byte b : bytes) {
            token.append(String.format("%02x", b));
        }
        
        return token.toString();
    }
    
    /**
     * Thread de nettoyage des sessions expirées
     */
    private void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300000); // 5 minutes
                    cleanupExpiredSessions();
                    cleanupExpiredAttempts();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    Logger.error("Erreur nettoyage sécurité: " + e.getMessage());
                }
            }
        });
        
        cleanupThread.setDaemon(true);
        cleanupThread.setName("SecurityCleanup");
        cleanupThread.start();
    }
    
    private void cleanupExpiredSessions() {
        AppProperties config = AppProperties.getInstance();
        int timeout = config.getIntProperty("security.session.timeout", 3600);
        
        activeSessions.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired(timeout)) {
                Logger.debug("🧹 Session expirée nettoyée: " + entry.getKey().substring(0, 8) + "...");
                return true;
            }
            return false;
        });
    }
    
    private void cleanupExpiredAttempts() {
        AppProperties config = AppProperties.getInstance();
        int lockoutDuration = config.getIntProperty("security.lockout.duration", 900);
        
        loginAttempts.entrySet().removeIf(entry -> {
            LoginAttempt attempt = entry.getValue();
            if (attempt.getLastAttempt().isBefore(LocalDateTime.now().minusSeconds(lockoutDuration * 2))) {
                Logger.debug("🧹 Tentatives expirées nettoyées: " + entry.getKey());
                return true;
            }
            return false;
        });
    }
    
    /**
     * Obtient des statistiques de sécurité
     */
    public SecurityStats getSecurityStats() {
        return new SecurityStats(
            activeSessions.size(),
            loginAttempts.size(),
            loginAttempts.values().stream().mapToInt(a -> a.getFailedAttempts()).sum()
        );
    }
    
    // Classes internes
    public static class LoginAttempt {
        private final AtomicInteger failedAttempts = new AtomicInteger(0);
        private LocalDateTime lastAttempt = LocalDateTime.now();
        private boolean locked = false;
        
        public void addFailedAttempt() {
            failedAttempts.incrementAndGet();
            lastAttempt = LocalDateTime.now();
        }
        
        public int getFailedAttempts() {
            return failedAttempts.get();
        }
        
        public LocalDateTime getLastAttempt() {
            return lastAttempt;
        }
        
        public void lock() {
            this.locked = true;
        }
        
        public boolean isLocked() {
            return locked;
        }
        
        public void reset() {
            failedAttempts.set(0);
            locked = false;
            lastAttempt = LocalDateTime.now();
        }
    }
    
    public static class UserSession {
        private final String sessionId;
        private final Utilisateur user;
        private final LocalDateTime creationTime;
        private LocalDateTime lastActivity;
        
        public UserSession(String sessionId, Utilisateur user) {
            this.sessionId = sessionId;
            this.user = user;
            this.creationTime = LocalDateTime.now();
            this.lastActivity = LocalDateTime.now();
        }
        
        public void updateLastActivity() {
            this.lastActivity = LocalDateTime.now();
        }
        
        public boolean isExpired(int timeoutSeconds) {
            return lastActivity.isBefore(LocalDateTime.now().minusSeconds(timeoutSeconds));
        }
        
        // Getters
        public String getSessionId() { return sessionId; }
        public Utilisateur getUser() { return user; }
        public LocalDateTime getCreationTime() { return creationTime; }
        public LocalDateTime getLastActivity() { return lastActivity; }
    }
    
    public static class SecurityStats {
        public final int activeSessions;
        public final int trackedAttempts;
        public final int totalFailedAttempts;
        
        public SecurityStats(int activeSessions, int trackedAttempts, int totalFailedAttempts) {
            this.activeSessions = activeSessions;
            this.trackedAttempts = trackedAttempts;
            this.totalFailedAttempts = totalFailedAttempts;
        }
    }
}