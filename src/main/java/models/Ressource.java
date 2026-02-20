package models;

import java.io.StringReader;

public class Ressource {
    private int id;
    private String nom;
    private String type_ressource;
    private int quantite;
    private String fournisseur;
    public Ressource(){}
    public Ressource(int id, String nom, String type_ressource, int quatite,String fournisseur) {
        this.id = id;
        this.nom=nom;
        this.type_ressource=type_ressource;
        this.quantite=quatite;
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
        return quantite;
    }

    public void setQuatite(int quatite) {
        this.quantite = quatite;
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
                ", quatite=" + quantite +
                ", fournisseur='" + fournisseur + '\'' +
                '}';
    }

}