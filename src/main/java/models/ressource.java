package models;


public class ressource {
    private int id;
    private String nom;
    private String type_ressource;
    private int quatite;
    private String fournisseur;
    public ressource(){}
    public ressource(int id, String nom, String type_ressource, int quatite,String fournisseur) {
        this.id = id;
        this.nom=nom;
        this.type_ressource=type_ressource;
        this.quatite=quatite;
        this.fournisseur=fournisseur;

    }

    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType_ressource() {
        return type_ressource;
    }

    public void setType_ressource(String type_ressource) {
        this.type_ressource = type_ressource;
    }

    public int getQuatite() {
        return quatite;
    }

    public void setQuatite(int quatite) {
        this.quatite = quatite;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    @Override
    public String toString() {
        return "ressource{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type_ressource='" + type_ressource + '\'' +
                ", quatite=" + quatite +
                ", fournisseur='" + fournisseur + '\'' +
                '}';
    }

}