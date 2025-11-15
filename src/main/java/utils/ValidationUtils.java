/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(\\+213|0)[5-7][0-9]{8}$");
    
    /**
     * Valide une adresse email
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Valide un numéro de téléphone algérien
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Téléphone optionnel
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Valide une note (entre 0 et 20)
     */
    public static boolean isValidNote(double note) {
        return note >= 0 && note <= 20;
    }
    
    /**
     * Valide un coefficient (supérieur à 0)
     */
    public static boolean isValidCoefficient(double coefficient) {
        return coefficient > 0 && coefficient <= 10;
    }
    
    /**
     * Vérifie si une chaîne est vide
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Vérifie si une chaîne contient au moins X caractères
     */
    public static boolean hasMinLength(String str, int minLength) {
        return str != null && str.trim().length() >= minLength;
    }
    
    /**
     * Nettoie une chaîne (trim + suppression caractères dangereux)
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("[<>\"'`;]", "");
    }
    
    /**
     * Valide un nom (lettres, espaces, tirets uniquement)
     */
    public static boolean isValidName(String name) {
        if (isEmpty(name)) return false;
        return name.matches("^[a-zA-ZÀ-ÿ\\s-]+$");
    }
    
    /**
     * Valide un code programme (lettres, chiffres, underscore)
     */
    public static boolean isValidCode(String code) {
        if (isEmpty(code)) return false;
        return code.matches("^[A-Z0-9_]{2,20}$");
    }
    
    /**
     * Formate une note en string
     */
    public static String formatNote(double note) {
        return String.format("%.2f/20", note);
    }
    
    /**
     * Formate un pourcentage
     */
    public static String formatPourcentage(double pourcentage) {
        return String.format("%.1f%%", pourcentage);
    }
    
    /**
     * Valide une date de naissance (pas dans le futur, pas trop vieille)
     */
    public static boolean isValidDateNaissance(LocalDate date) {
        if (date == null) return false;
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(100);
        return date.isAfter(minDate) && date.isBefore(now);
    }
}
