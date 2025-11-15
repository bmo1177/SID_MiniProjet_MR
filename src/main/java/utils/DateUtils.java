/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class DateUtils {
    
    private static final DateTimeFormatter FRENCH_FORMAT = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private static final DateTimeFormatter SQL_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static final DateTimeFormatter FRENCH_LONG_FORMAT = 
        DateTimeFormatter.ofPattern("dd MMMM yyyy");
    
    /**
     * Formate une date au format français (dd/MM/yyyy)
     */
    public static String formatFrench(LocalDate date) {
        if (date == null) return "";
        return date.format(FRENCH_FORMAT);
    }
    
    /**
     * Formate une date au format SQL (yyyy-MM-dd)
     */
    public static String formatSQL(LocalDate date) {
        if (date == null) return "";
        return date.format(SQL_FORMAT);
    }
    
    /**
     * Formate une date en français long (25 janvier 2025)
     */
    public static String formatFrenchLong(LocalDate date) {
        if (date == null) return "";
        return date.format(FRENCH_LONG_FORMAT);
    }
    
    /**
     * Parse une date au format français
     */
    public static LocalDate parseFrench(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr.trim(), FRENCH_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Parse une date au format SQL
     */
    public static LocalDate parseSQL(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr.trim(), SQL_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Vérifie si une date est entre deux autres
     */
    public static boolean isBetween(LocalDate date, LocalDate start, LocalDate end) {
        if (date == null || start == null || end == null) return false;
        return !date.isBefore(start) && !date.isAfter(end);
    }
    
    /**
     * Calcule l'âge à partir d'une date de naissance
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    /**
     * Retourne la date d'aujourd'hui
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Retourne l'année scolaire courante (ex: "2025/2026")
     */
    public static String getCurrentAcademicYear() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        
        // Si on est entre janvier et août, on est dans l'année N-1/N
        if (now.getMonthValue() < 9) {
            return (year - 1) + "/" + year;
        } else {
            return year + "/" + (year + 1);
        }
    }
}
