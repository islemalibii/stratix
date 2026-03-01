package services;

import utils.MyDataBase;
import utils.PasswordValidator;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AuthenticationService {
    private static AuthenticationService instance;
    private Connection connection;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    private AuthenticationService() {
        connection = MyDataBase.getInstance().getCnx();
    }

    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Vérifier si le compte est verrouillé
     */
    public LockStatus isAccountLocked(String email) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }

        String query = "SELECT account_locked, locked_until, failed_login_attempts FROM utilisateur WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            boolean locked = rs.getBoolean("account_locked");
            Timestamp lockedUntil = rs.getTimestamp("locked_until");
            int attempts = rs.getInt("failed_login_attempts");

            if (locked && lockedUntil != null) {
                LocalDateTime unlockTime = lockedUntil.toLocalDateTime();
                LocalDateTime now = LocalDateTime.now();
                
                // Vérifier si la période de verrouillage est expirée
                if (now.isAfter(unlockTime)) {
                    unlockAccount(email);
                    return new LockStatus(false, 0, null);
                }
                
                // Calculer le temps restant
                long minutesRemaining = java.time.Duration.between(now, unlockTime).toMinutes();
                return new LockStatus(true, minutesRemaining, unlockTime);
            }
        }
        return new LockStatus(false, 0, null);
    }

    /**
     * Enregistrer une tentative de connexion échouée
     */
    public void recordFailedLogin(String email) throws SQLException {
        if (connection == null) return;

        String query = "UPDATE utilisateur SET failed_login_attempts = failed_login_attempts + 1 WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, email);
        ps.executeUpdate();

        // Vérifier si le compte doit être verrouillé
        checkAndLockAccount(email);
    }

    /**
     * Vérifier et verrouiller le compte si nécessaire
     */
    private void checkAndLockAccount(String email) throws SQLException {
        String query = "SELECT failed_login_attempts FROM utilisateur WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int attempts = rs.getInt("failed_login_attempts");
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                lockAccount(email);
            }
        }
    }

    /**
     * Verrouiller le compte pour 15 minutes
     */
    private void lockAccount(String email) throws SQLException {
        LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
        String query = "UPDATE utilisateur SET account_locked = TRUE, locked_until = ? WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setTimestamp(1, Timestamp.valueOf(lockUntil));
        ps.setString(2, email);
        ps.executeUpdate();
    }

    /**
     * Déverrouiller le compte
     */
    public void unlockAccount(String email) throws SQLException {
        if (connection == null) return;

        String query = "UPDATE utilisateur SET account_locked = FALSE, locked_until = NULL, failed_login_attempts = 0 WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, email);
        ps.executeUpdate();
    }

    /**
     * Réinitialiser les tentatives après connexion réussie
     */
    public void resetFailedAttempts(String email) throws SQLException {
        if (connection == null) return;

        String query = "UPDATE utilisateur SET failed_login_attempts = 0 WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, email);
        ps.executeUpdate();
    }

    /**
     * Générer et enregistrer un token de réinitialisation
     */
    public String generatePasswordResetToken(String email) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }

        String token = PasswordValidator.generateResetToken();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1); // Token valide 1 heure

        String query = "UPDATE utilisateur SET password_reset_token = ?, password_reset_expiry = ? WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, token);
        ps.setTimestamp(2, Timestamp.valueOf(expiry));
        ps.setString(3, email);
        ps.executeUpdate();

        return token;
    }

    /**
     * Vérifier le token de réinitialisation
     */
    public boolean validateResetToken(String token) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }

        String query = "SELECT password_reset_expiry FROM utilisateur WHERE password_reset_token = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, token);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Timestamp expiry = rs.getTimestamp("password_reset_expiry");
            if (expiry != null) {
                return LocalDateTime.now().isBefore(expiry.toLocalDateTime());
            }
        }
        return false;
    }

    /**
     * Réinitialiser le mot de passe avec token
     */
    public boolean resetPassword(String token, String newPassword) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }

        if (!validateResetToken(token)) {
            return false;
        }

        String hashedPassword = PasswordValidator.hashPassword(newPassword);
        String query = "UPDATE utilisateur SET password = ?, password_reset_token = NULL, " +
                      "password_reset_expiry = NULL, last_password_change = ? WHERE password_reset_token = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, hashedPassword);
        ps.setDate(2, Date.valueOf(LocalDate.now()));
        ps.setString(3, token);
        
        int updated = ps.executeUpdate();
        return updated > 0;
    }

    /**
     * Activer 2FA pour un utilisateur
     */
    public String enable2FA(int userId) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }

        String secret = PasswordValidator.generate2FACode();
        String query = "UPDATE utilisateur SET two_factor_enabled = TRUE, two_factor_secret = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, secret);
        ps.setInt(2, userId);
        ps.executeUpdate();

        return secret;
    }

    /**
     * Désactiver 2FA
     */
    public void disable2FA(int userId) throws SQLException {
        if (connection == null) return;

        String query = "UPDATE utilisateur SET two_factor_enabled = FALSE, two_factor_secret = NULL WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, userId);
        ps.executeUpdate();
    }

    /**
     * Vérifier le code 2FA
     */
    public boolean verify2FACode(int userId, String code) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }

        String query = "SELECT two_factor_secret FROM utilisateur WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String secret = rs.getString("two_factor_secret");
            return secret != null && secret.equals(code);
        }
        return false;
    }

    /**
     * Générer un nouveau code 2FA
     */
    public String generate2FACode(int userId) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion à la base de données non disponible");
        }

        String code = PasswordValidator.generate2FACode();
        String query = "UPDATE utilisateur SET two_factor_secret = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, code);
        ps.setInt(2, userId);
        ps.executeUpdate();

        return code;
    }
    
    /**
     * Classe pour le statut de verrouillage
     */
    public static class LockStatus {
        private final boolean locked;
        private final long minutesRemaining;
        private final LocalDateTime unlockTime;
        
        public LockStatus(boolean locked, long minutesRemaining, LocalDateTime unlockTime) {
            this.locked = locked;
            this.minutesRemaining = minutesRemaining;
            this.unlockTime = unlockTime;
        }
        
        public boolean isLocked() {
            return locked;
        }
        
        public long getMinutesRemaining() {
            return minutesRemaining;
        }
        
        public LocalDateTime getUnlockTime() {
            return unlockTime;
        }
        
        public String getFormattedTimeRemaining() {
            if (minutesRemaining < 60) {
                return minutesRemaining + " minute(s)";
            } else {
                long hours = minutesRemaining / 60;
                long mins = minutesRemaining % 60;
                return hours + " heure(s) et " + mins + " minute(s)";
            }
        }
    }
}