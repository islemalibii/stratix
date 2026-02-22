package utils;

import models.Employe;

public class SessionManager {

    private static Employe currentUser;

    public static void setCurrentUser(Employe user) {
        currentUser = user;
    }

    public static Employe getCurrentUser() {
        // Si aucun utilisateur n'est connecté, en créer un fictif pour les tests
        if (currentUser == null) {
            System.out.println("⚠️ Aucun utilisateur connecté - Création d'un utilisateur de test");
            currentUser = createTestUser();
        }
        return currentUser;
    }

    private static Employe createTestUser() {
        Employe testUser = new Employe();
        testUser.setId(1);  // ID d'un employé qui existe dans ta BD
        testUser.setUsername("jdoe");
        testUser.setRole("employe");
        return testUser;
    }

    public static boolean isResponsable() {
        return currentUser != null &&
                ("responsable".equals(currentUser.getRole()) ||
                        "admin".equals(currentUser.getRole()) ||
                        "ceo".equals(currentUser.getRole()) ||
                        "responsable_rh".equals(currentUser.getRole()) ||
                        "responsable_projet".equals(currentUser.getRole()) ||
                        "responsable_production".equals(currentUser.getRole()));
    }

    public static boolean isEmploye() {
        return currentUser != null && "employe".equals(currentUser.getRole());
    }

    public static void logout() {
        currentUser = null;
    }
}