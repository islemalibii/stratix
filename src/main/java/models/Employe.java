package models;

public class Employe {
    private int id;
    private String username;
    private String email;
    private String tel;
    private String password;
    private String role;
    private String dateAjout;
    private String statut;
    private String department;
    private String poste;
    private String dateEmbauche;
    private double salaire;
    private String competences;

    // Champs de sécurité
    private int failedLoginAttempts;
    private boolean accountLocked;
    private String lockedUntil;
    private boolean twoFactorEnabled;
    private String twoFactorSecret;

    public Employe() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDateAjout() { return dateAjout; }
    public void setDateAjout(String dateAjout) { this.dateAjout = dateAjout; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public String getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(String dateEmbauche) { this.dateEmbauche = dateEmbauche; }

    public double getSalaire() { return salaire; }
    public void setSalaire(double salaire) { this.salaire = salaire; }

    public String getCompetences() { return competences; }
    public void setCompetences(String competences) { this.competences = competences; }

    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public boolean isAccountLocked() { return accountLocked; }
    public void setAccountLocked(boolean accountLocked) { this.accountLocked = accountLocked; }

    public String getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(String lockedUntil) { this.lockedUntil = lockedUntil; }

    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }

    public String getTwoFactorSecret() { return twoFactorSecret; }
    public void setTwoFactorSecret(String twoFactorSecret) { this.twoFactorSecret = twoFactorSecret; }

    // Méthodes utilitaires
    public String getDisplayName() {
        return username != null ? username : "Employé " + id;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}