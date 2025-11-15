package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {
    
    /**
     * Hash un mot de passe avec SHA-256
     */
    public static String hashPasswordSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithme SHA-256 non disponible", e);
        }
    }
    
    /**
     * Vérifie si un mot de passe correspond au hash stocké
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        String inputHash = hashPasswordSHA256(plainPassword);
        return inputHash.equals(storedHash);
    }
    
    /**
     * Vérifie si un mot de passe est valide
     * Critères: min 8 caractères, au moins une lettre et un chiffre
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        
        return hasLetter && hasDigit;
    }
    
    /**
     * Retourne un message d'erreur pour un mot de passe invalide
     */
    public static String getPasswordErrorMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Le mot de passe ne peut pas être vide";
        }
        if (password.length() < 8) {
            return "Le mot de passe doit contenir au moins 8 caractères";
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            return "Le mot de passe doit contenir au moins une lettre";
        }
        if (!password.matches(".*\\d.*")) {
            return "Le mot de passe doit contenir au moins un chiffre";
        }
        return "Mot de passe valide";
    }
    
    /**
     * Génère un mot de passe aléatoire sécurisé
     */
    public static String generatePassword(int length) {
        if (length < 8) length = 8;
        
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*";
        
        String allChars = upperCase + lowerCase + digits + specialChars;
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        // S'assurer d'avoir au moins un de chaque type
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Compléter avec des caractères aléatoires
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Mélanger le mot de passe
        return shuffleString(password.toString());
    }
    
    /**
     * Mélange une chaîne de caractères
     */
    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        java.util.Random random = new java.util.Random();
        for (int i = characters.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            // Échanger les caractères
            char temp = characters[index];
            characters[index] = characters[i];
            characters[i] = temp;
        }
        return new String(characters);
    }
    
    /**
     * Méthode de test
     */
    public static void main(String[] args) {
        System.out.println("=== Test PasswordUtils ===");
        
        // Test hash
        String testPassword = "admin123";
        String hash = hashPasswordSHA256(testPassword);
        System.out.println("Mot de passe: " + testPassword);
        System.out.println("Hash SHA-256: " + hash);
        
        // Test validation
        System.out.println("\n=== Test validation ===");
        String[] tests = {"short", "nodigits", "12345678", "Valid123", "SuperP@ss123"};
        for (String pwd : tests) {
            boolean valid = isValidPassword(pwd);
            String message = getPasswordErrorMessage(pwd);
            System.out.printf("'%-12s': %b - %s%n", pwd, valid, message);
        }
        
        // Test génération
        System.out.println("\n=== Test génération ===");
        String generated = generatePassword(12);
        System.out.println("Mot de passe généré: " + generated);
        System.out.println("Valide: " + isValidPassword(generated));
    }
}