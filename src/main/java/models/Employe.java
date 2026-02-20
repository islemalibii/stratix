package models;

public class Employe {
    private int id;
    private String username;
    private String email;
    private String tel;
    private String password;
    private String role;
    private String dateAjout;

    // Champs supplémentaires optionnels (peuvent être null selon le rôle)
    private String department;
    private String poste;
    private String dateEmbauche;
    private double salaire;
    private String competences;

    public Employe() {}

    // Getters et Setters basiques
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

    // Champs optionnels
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

    // Méthodes utilitaires
    public String getDisplayName() {
        if (username != null && !username.isEmpty()) {
            return username;
        }
        return "Employé " + id;
    }

    public String getFullInfo() {
        StringBuilder info = new StringBuilder();
        info.append(username);
        if (poste != null && !poste.isEmpty()) {
            info.append(" (").append(poste).append(")");
        }
        if (department != null && !department.isEmpty()) {
            info.append(" - ").append(department);
        }
        return info.toString();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}