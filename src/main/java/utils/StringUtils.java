/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

public class StringUtils {
    
    /**
     * Capitalise la première lettre
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Capitalise chaque mot
     */
    public static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return str;
        
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(capitalize(word)).append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    /**
     * Tronque un texte à une longueur maximale
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Génère des initiales à partir d'un nom
     */
    public static String getInitials(String nom, String prenom) {
        StringBuilder initials = new StringBuilder();
        
        if (prenom != null && !prenom.isEmpty()) {
            initials.append(prenom.charAt(0));
        }
        if (nom != null && !nom.isEmpty()) {
            initials.append(nom.charAt(0));
        }
        
        return initials.toString().toUpperCase();
    }
    
    /**
     * Formate un numéro de téléphone
     */
    public static String formatPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) return "";
        
        // Supprimer tous les caractères non-numériques
        String clean = phone.replaceAll("[^0-9+]", "");
        
        // Format : +213 X XX XX XX XX
        if (clean.startsWith("+213") && clean.length() == 13) {
            return clean.substring(0, 4) + " " + 
                   clean.substring(4, 5) + " " +
                   clean.substring(5, 7) + " " +
                   clean.substring(7, 9) + " " +
                   clean.substring(9, 11) + " " +
                   clean.substring(11);
        }
        
        // Format : 0X XX XX XX XX
        if (clean.startsWith("0") && clean.length() == 10) {
            return clean.substring(0, 2) + " " +
                   clean.substring(2, 4) + " " +
                   clean.substring(4, 6) + " " +
                   clean.substring(6, 8) + " " +
                   clean.substring(8);
        }
        
        return phone;
    }
}
