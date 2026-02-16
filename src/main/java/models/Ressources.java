package models;

public class Ressources {

    private int idRessources;
    private String nom;
    private String typeRessources;
    private int quantite;
    private String fournisseur;

    public Ressources() {}

    public Ressources(int idRessources, String nom, int quantite) {
        this.idRessources = idRessources;
        this.nom = nom;
        this.quantite = quantite;
    }

    public int getIdRessources() {
        return idRessources;
    }
    public void setIdRessources(int idRessources) {
        this.idRessources = idRessources;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getQuantite() {
        return quantite;
    }
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "Ressource{" + "id=" + idRessources + ", nom='" + nom + '\'' + '}';
    }
}
