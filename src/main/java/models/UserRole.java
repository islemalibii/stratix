package models;

public class UserRole {
    private static UserRole instance;
    private Utilisateur currentUser;

    private UserRole() {}

    public static UserRole getInstance() {
        if (instance == null) instance = new UserRole();
        return instance;
    }

    public void setUser(Utilisateur user) {
        this.currentUser = user;
    }

    public Utilisateur getUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        // Vérifie si l'utilisateur est ADMIN, CEO ou Responsable (selon votre besoin)
        return currentUser != null && (currentUser.isAdmin() || currentUser.isCEO() || currentUser.isResponsable());
    }

    public boolean isEmployee() {
        return currentUser != null && currentUser.getRole() == Role.EMPLOYE;
    }
    public boolean isResponsable() {
        return currentUser != null && currentUser.isResponsable();
    }
}