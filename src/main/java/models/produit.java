package models;

public class produit {
    private int id;
    private String nom;
    private String description;
    private String categorie;
    private double prix;
    private int stock_actuel;
    private int stock_min;
    private String date_creation;
    private String ressources_necessaires;
    private String image_path; // Nouveau champ

    // Constructeurs
    public produit() {}

    public produit(int id, String nom, String description, String categorie, double prix,
                   int stock_actuel, int stock_min, String date_creation,
                   String ressources_necessaires, String image_path) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.categorie = categorie;
        this.prix = prix;
        this.stock_actuel = stock_actuel;
        this.stock_min = stock_min;
        this.date_creation = date_creation;
        this.ressources_necessaires = ressources_necessaires;
        this.image_path = image_path;
    }

    // Getters et Setters existants...

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getStock_actuel() { return stock_actuel; }
    public void setStock_actuel(int stock_actuel) { this.stock_actuel = stock_actuel; }

    public int getStock_min() { return stock_min; }
    public void setStock_min(int stock_min) { this.stock_min = stock_min; }

    public String getDate_creation() { return date_creation; }
    public void setDate_creation(String date_creation) { this.date_creation = date_creation; }

    public String getRessources_necessaires() { return ressources_necessaires; }
    public void setRessources_necessaires(String ressources_necessaires) {
        this.ressources_necessaires = ressources_necessaires;
    }

    // Nouveaux getter/setter pour l'image
    public String getImage_path() { return image_path; }
    public void setImage_path(String image_path) { this.image_path = image_path; }
    public String toString() {
        return "produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", categorie='" + categorie + '\'' +
                ", prix=" + prix +
                ", stock_actuel=" + stock_actuel +
                ", stock_min=" + stock_min +
                ", date_creation='" + date_creation + '\'' +
                ", ressources_necessaires='" + ressources_necessaires + '\'' +
                ", image_path='" + image_path + '\'' +
                '}';
    }
}