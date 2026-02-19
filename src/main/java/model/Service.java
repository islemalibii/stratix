package model;

public class Service {
    private int id;
    private String titre;
    private String description;
    private String dateCreation;
    private String dateDebut;
    private String dateFin;
    private int responsableId;
    private double budget;
    private int categorieId;
    private CategorieService categorie;

    public Service() {}

    public Service(int id, String titre, String description, String dateCreation,
                   String dateDebut, String dateFin, int responsableId,
                   double budget, int categorieId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateCreation = dateCreation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.responsableId = responsableId;
        this.budget = budget;
        this.categorieId = categorieId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDateCreation() { return dateCreation; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }

    public String getDateDebut() { return dateDebut; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }

    public String getDateFin() { return dateFin; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }

    public int getResponsableId() { return responsableId; }
    public void setResponsableId(int responsableId) { this.responsableId = responsableId; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public int getCategorieId() { return categorieId; }
    public void setCategorieId(int categorieId) { this.categorieId = categorieId; }

    public CategorieService getCategorie() { return categorie; }
    public void setCategorie(CategorieService categorie) { this.categorie = categorie; }

    @Override
    public String toString() {
        return "Service [id=" + id + ", titre=" + titre + "]";
    }
}