package model;

import java.time.LocalDate;
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

    public Projet() {}

    public Projet(int id, String nom, String description, Date dateDebut,
                  Date dateFin, double budget, String statut, int progression) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.budget = budget;
        this.statut = statut;
        this.progression = progression;
    }



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

    @Override
    public String toString() {
        return String.format("ID: %d | Nom: %-15s | Statut: %-10s | Prog: %d%% | Budget: %.2f",
                id, nom, statut, progression, budget);
    }


}