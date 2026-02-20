package model;

public class Service {
    private int id;
    private String titre;
    private String description;
    private String dateCreation;
    private String dateDebut;
    private String dateFin;
    private int utilisateurId;
    private double budget;
    private int categorieId;
    private CategorieService categorie;
    private boolean archive;

    public Service() {}

    public Service(int id, String titre, String description, String dateCreation,
                   String dateDebut, String dateFin, int utilisateurId,
                   double budget, int categorieId, boolean archive) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateCreation = dateCreation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.utilisateurId = utilisateurId;
        this.budget = budget;
        this.categorieId = categorieId;
        this.archive = archive;
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

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public int getCategorieId() { return categorieId; }
    public void setCategorieId(int categorieId) { this.categorieId = categorieId; }

    public CategorieService getCategorie() { return categorie; }
    public void setCategorie(CategorieService categorie) { this.categorie = categorie; }

    public boolean isArchive() { return archive; }
    public void setArchive(boolean archive) { this.archive = archive; }
}