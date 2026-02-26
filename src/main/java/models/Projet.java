package models;

import java.util.Date;

public class Projet {
    private int id;
    private String nom;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private double budget;
    private String statut;
    private int progression;
    private boolean isArchived;

    private int responsableId;
    private String equipeMembres;

    // Nouveau champ pour stocker le nom récupéré de la DB
    private String nomResponsable;

    public Projet() {}

    public Projet(int id, String nom, String description, Date dateDebut,
                  Date dateFin, double budget, String statut, int progression,
                  boolean isArchived, int responsableId, String equipeMembres) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.budget = budget;
        this.statut = statut;
        this.progression = progression;
        this.isArchived = isArchived;
        this.responsableId = responsableId;
        this.equipeMembres = equipeMembres;
    }

    // --- ALIAS POUR CORRIGER LES ERREURS DU CONTROLLER ---
    public String getChefProjet() {
        return (nomResponsable != null) ? nomResponsable : "ID: " + responsableId;
    }

    public String getEquipe() { return equipeMembres; }

    // --- Getters et Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getProgression() { return progression; }
    public void setProgression(int progression) { this.progression = progression; }
    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }
    public int getResponsableId() { return responsableId; }
    public void setResponsableId(int responsableId) { this.responsableId = responsableId; }
    public String getEquipeMembres() { return equipeMembres; }
    public void setEquipeMembres(String equipeMembres) { this.equipeMembres = equipeMembres; }
    public String getNomResponsable() { return nomResponsable; }
    public void setNomResponsable(String nomResponsable) { this.nomResponsable = nomResponsable; }

    @Override
    public String toString() {
        return "Projet: " + nom + " | Chef: " + getChefProjet() + " (" + progression + "%)";
    }
}