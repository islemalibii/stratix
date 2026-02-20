package model;

public class CategorieService {
    private int id;
    private String nom;
    private String description;
    private String dateCreation;
    private boolean archive;

    public CategorieService() {}

    public CategorieService(int id, String nom, String description, String dateCreation, boolean archive) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateCreation = dateCreation;
        this.archive = archive;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDateCreation() { return dateCreation; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }

    public boolean isArchive() { return archive; }
    public void setArchive(boolean archive) { this.archive = archive; }

    @Override
    public String toString() {
        return nom;
    }
}