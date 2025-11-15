/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

public class NumberUtils {
    
    /**
     * Arrondit un nombre à N décimales
     */
    public static double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }
    
    /**
     * Formate un nombre avec séparateurs de milliers
     */
    public static String formatNumber(int number) {
        return String.format("%,d", number);
    }
    
    /**
     * Formate un nombre décimal
     */
    public static String formatDecimal(double number, int decimals) {
        return String.format("%." + decimals + "f", number);
    }
    
    /**
     * Parse un double de manière sécurisée
     */
    public static Double parseDouble(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(str.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Parse un int de manière sécurisée
     */
    public static Integer parseInt(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Vérifie si une chaîne est un nombre
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Calcule un pourcentage
     */
    public static double calculatePercentage(double part, double total) {
        if (total == 0) return 0;
        return round((part * 100.0) / total, 2);
    }
}
