/*
 * Système d'exceptions métier pour gestion d'erreurs robuste
 * Classes d'exceptions spécialisées pour production
 */
package exceptions;

public class BusinessException extends Exception {
    private final String errorCode;
    private final Object[] parameters;
    
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = new Object[0];
    }
    
    public BusinessException(String errorCode, String message, Object... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters != null ? parameters : new Object[0];
    }
    
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = new Object[0];
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object[] getParameters() {
        return parameters;
    }
    
    public String getLocalizedMessage(java.util.Locale locale) {
        // Ici on pourrait charger les messages depuis des fichiers de ressources
        return getMessage();
    }
}

// Exceptions spécialisées
class ValidationException extends BusinessException {
    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", "Erreur de validation sur " + field + ": " + message);
    }
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}

class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super("AUTH_ERROR", message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super("AUTH_ERROR", message, cause);
    }
}

class AuthorizationException extends BusinessException {
    public AuthorizationException(String action, String resource) {
        super("AUTHORIZATION_ERROR", "Accès refusé pour l'action '" + action + "' sur '" + resource + "'");
    }
}

class DataNotFoundException extends BusinessException {
    public DataNotFoundException(String entityType, Object id) {
        super("NOT_FOUND", entityType + " avec l'ID '" + id + "' non trouvé");
    }
    
    public DataNotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}

class DataIntegrityException extends BusinessException {
    public DataIntegrityException(String message) {
        super("DATA_INTEGRITY", message);
    }
    
    public DataIntegrityException(String message, Throwable cause) {
        super("DATA_INTEGRITY", message, cause);
    }
}

class ServiceUnavailableException extends BusinessException {
    public ServiceUnavailableException(String serviceName) {
        super("SERVICE_UNAVAILABLE", "Service '" + serviceName + "' temporairement indisponible");
    }
    
    public ServiceUnavailableException(String serviceName, Throwable cause) {
        super("SERVICE_UNAVAILABLE", "Service '" + serviceName + "' temporairement indisponible", cause);
    }
}