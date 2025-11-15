/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

public class ColorUtils {
    
    /**
     * Retourne une couleur selon le statut
     */
    public static java.awt.Color getStatutColor(String statut) {
        if (statut == null) return java.awt.Color.GRAY;
        
        switch (statut.toLowerCase()) {
            case "admis":
                return new java.awt.Color(46, 204, 113); // Vert
            case "redoublant":
                return new java.awt.Color(230, 126, 34); // Orange
            case "exclu":
                return new java.awt.Color(231, 76, 60);  // Rouge
            default:
                return java.awt.Color.GRAY;
        }
    }
    
    /**
     * Retourne une couleur selon la note
     */
    public static java.awt.Color getNoteColor(double note) {
        if (note >= 16) return new java.awt.Color(39, 174, 96);   // Excellent (vert foncé)
        if (note >= 14) return new java.awt.Color(46, 204, 113);  // Très bien (vert)
        if (note >= 12) return new java.awt.Color(52, 152, 219);  // Bien (bleu)
        if (note >= 10) return new java.awt.Color(241, 196, 15);  // Passable (jaune)
        if (note >= 8)  return new java.awt.Color(230, 126, 34);  // Insuffisant (orange)
        return new java.awt.Color(231, 76, 60);                   // Mauvais (rouge)
    }
    
    /**
     * Éclaircit une couleur
     */
    public static java.awt.Color lighten(java.awt.Color color, double factor) {
        int r = Math.min(255, (int)(color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int)(color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int)(color.getBlue() + (255 - color.getBlue()) * factor));
        return new java.awt.Color(r, g, b);
    }
    
    /**
     * Assombrit une couleur
     */
    public static java.awt.Color darken(java.awt.Color color, double factor) {
        int r = Math.max(0, (int)(color.getRed() * (1 - factor)));
        int g = Math.max(0, (int)(color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int)(color.getBlue() * (1 - factor)));
        return new java.awt.Color(r, g, b);
    }
}