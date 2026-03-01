package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

public class PasswordValidator {
    
    // Politique de mot de passe
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 50;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
    
    /**
     * Valider la force du mot de passe
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Le mot de passe ne peut pas être vide");
        }
        
        if (password.length() < MIN_LENGTH) {
            return new ValidationResult(false, "Le mot de passe doit contenir au moins " + MIN_LENGTH + " caractères");
        }
        
        if (password.length() > MAX_LENGTH) {
            return new ValidationResult(false, "Le mot de passe ne peut pas dépasser " + MAX_LENGTH + " caractères");
        }
        
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return new ValidationResult(false, "Le mot de passe doit contenir au moins une majuscule");
        }
        
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return new ValidationResult(false, "Le mot de passe doit contenir au moins une minuscule");
        }
        
        if (!DIGIT_PATTERN.matcher(password).find()) {
            return new ValidationResult(false, "Le mot de passe doit contenir au moins un chiffre");
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return new ValidationResult(false, "Le mot de passe doit contenir au moins un caractère spécial (!@#$%^&*...)");
        }
        
        return new ValidationResult(true, "Mot de passe valide");
    }
    
    /**
     * Hasher le mot de passe avec SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }
    
    /**
     * Générer un token de réinitialisation sécurisé
     */
    public static String generateResetToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    /**
     * Générer un code 2FA à 6 chiffres
     */
    public static String generate2FACode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    /**
     * Classe pour le résultat de validation
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
