package models;

import java.time.LocalDate;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String tel;
    private int cin;
    private String password;
    private Role role;
    private LocalDate dateAjout;
    
    // Champs pour employe/responsable (peuvent être NULL)
    private String department;
    private String poste;
    private LocalDate dateEmbauche;
    private String competences;
    private double salaire;

    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, String tel, int cin, String password, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.tel = tel;
        this.cin = cin;
        this.password = password;
        this.role = role;
        this.dateAjout = LocalDate.now();
    }

    // Constructeur complet pour employe/responsable
    public Utilisateur(String nom, String prenom, String email, String tel, int cin, String password, 
                      Role role, String department, String poste, double salaire, String competences) {
        this(nom, prenom, email, tel, cin, password, role);
        this.department = department;
        this.poste = poste;
        this.salaire = salaire;
        this.competences = competences;
        this.dateEmbauche = LocalDate.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public int getCin() { return cin; }
    public void setCin(int cin) { this.cin = cin; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDate getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDate dateAjout) { this.dateAjout = dateAjout; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }

    public String getCompetences() { return competences; }
    public void setCompetences(String competences) { this.competences = competences; }

    public double getSalaire() { return salaire; }
    public void setSalaire(double salaire) { this.salaire = salaire; }

    public void authentifier() {
        // Logique d'authentification
    }
    
    // Méthodes utilitaires
    public boolean isEmploye() {
        return role == Role.EMPLOYE || isResponsable();
    }
    
    public boolean isResponsable() {
        return role == Role.RESPONSABLE_RH || 
               role == Role.RESPONSABLE_PROJET || 
               role == Role.RESPONSABLE_PRODUCTION;
    }
    
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
    
    public boolean isCEO() {
        return role == Role.CEO;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", tel='" + tel + '\'' +
                ", cin=" + cin +
                ", role=" + role +
                ", department='" + department + '\'' +
                ", poste='" + poste + '\'' +
                ", salaire=" + salaire +
                ", dateAjout=" + dateAjout +
                '}';
    }
}
