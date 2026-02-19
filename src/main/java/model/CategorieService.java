package model;

public class CategorieService {
    private int id;
    private String nom;
    private String description;
    private String dateCreation;

    public CategorieService() {}

    public CategorieService(int id, String nom, String description, String dateCreation) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateCreation = dateCreation;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDateCreation() { return dateCreation; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }

    @Override
    public String toString() {
        return nom;
    }
}