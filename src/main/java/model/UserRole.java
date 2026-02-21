package model;

public class UserRole {
    private static UserRole instance;
    private String role;

    private UserRole() {}

    public static UserRole getInstance() {
        if (instance == null) {
            instance = new UserRole();
        }
        return instance;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public boolean isEmployee() {
        return "employee".equals(role);
    }
}