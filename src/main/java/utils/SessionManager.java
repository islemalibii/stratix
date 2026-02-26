package utils;

import java.io.*;
import java.util.Properties;

public class SessionManager {
    private static SessionManager instance;
    private static final String SESSION_FILE = "session.properties";
    private Properties sessionProps;

    private SessionManager() {
        sessionProps = new Properties();
        loadSession();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Sauvegarder la session
    public void saveSession(int userId, String email, String role) {
        try {
            sessionProps.setProperty("userId", String.valueOf(userId));
            sessionProps.setProperty("email", email);
            sessionProps.setProperty("role", role);
            sessionProps.setProperty("isLoggedIn", "true");
            
            try (FileOutputStream fos = new FileOutputStream(SESSION_FILE)) {
                sessionProps.store(fos, "User Session");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de la session: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Charger la session
    private void loadSession() {
        try {
            File file = new File(SESSION_FILE);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(SESSION_FILE)) {
                    sessionProps.load(fis);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la session: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Vérifier si l'utilisateur est connecté
    public boolean isLoggedIn() {
        try {
            return "true".equals(sessionProps.getProperty("isLoggedIn", "false"));
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de la session: " + e.getMessage());
            return false;
        }
    }

    // Récupérer l'ID de l'utilisateur
    public int getUserId() {
        try {
            String userId = sessionProps.getProperty("userId", "0");
            return Integer.parseInt(userId);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'ID utilisateur: " + e.getMessage());
            return 0;
        }
    }

    // Récupérer l'email
    public String getEmail() {
        try {
            return sessionProps.getProperty("email", "");
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'email: " + e.getMessage());
            return "";
        }
    }

    // Récupérer le rôle
    public String getRole() {
        try {
            return sessionProps.getProperty("role", "");
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du rôle: " + e.getMessage());
            return "";
        }
    }

    // Déconnexion - supprimer la session
    public void logout() {
        try {
            sessionProps.clear();
            File file = new File(SESSION_FILE);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("Impossible de supprimer le fichier de session");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
