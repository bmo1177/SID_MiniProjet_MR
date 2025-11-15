/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

public class Logger {
    private static final boolean DEBUG_MODE = true;
    
    public static void info(String message) {
        if (DEBUG_MODE) {
            System.out.println("[INFO] " + getTimestamp() + " - " + message);
        }
    }
    
    public static void error(String message) {
        System.err.println("[ERROR] " + getTimestamp() + " - " + message);
    }
    
    public static void error(String message, Exception e) {
        System.err.println("[ERROR] " + getTimestamp() + " - " + message);
        e.printStackTrace();
    }
    
    public static void debug(String message) {
        if (DEBUG_MODE) {
            System.out.println("[DEBUG] " + getTimestamp() + " - " + message);
        }
    }
    
    public static void warn(String message) {
        System.out.println("[WARN] " + getTimestamp() + " - " + message);
    }
    
    public static void warning(String message) {
        warn(message);
    }
    
    private static String getTimestamp() {
        return java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
