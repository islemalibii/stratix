package models;

import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarEvent {

    private int id;
    private int employeId;
    private String employeNom;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String typeShift;
    private String couleur; // Pour l'affichage

    public CalendarEvent() {}

    public CalendarEvent(int id, int employeId, String employeNom, LocalDate date,
                         LocalTime heureDebut, LocalTime heureFin, String typeShift) {
        this.id = id;
        this.employeId = employeId;
        this.employeNom = employeNom;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.typeShift = typeShift;
        setCouleurByShift(typeShift);
    }

    // Constructeur à partir d'un Planning
    public CalendarEvent(Planning planning, String employeNom) {
        this.id = planning.getId();
        this.employeId = planning.getEmployeId();
        this.employeNom = employeNom;
        this.date = planning.getDate().toLocalDate();
        this.heureDebut = planning.getHeureDebut().toLocalTime();
        this.heureFin = planning.getHeureFin().toLocalTime();
        this.typeShift = planning.getTypeShift();
        setCouleurByShift(planning.getTypeShift());
    }

    private void setCouleurByShift(String shift) {
        switch(shift) {
            case "JOUR":
                this.couleur = "#3b82f6"; // Bleu
                break;
            case "SOIR":
                this.couleur = "#f59e0b"; // Orange
                break;
            case "NUIT":
                this.couleur = "#8b5cf6"; // Violet
                break;
            default:
                this.couleur = "#6b7280"; // Gris
        }
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeId() { return employeId; }
    public void setEmployeId(int employeId) { this.employeId = employeId; }

    public String getEmployeNom() { return employeNom; }
    public void setEmployeNom(String employeNom) { this.employeNom = employeNom; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public String getTypeShift() { return typeShift; }
    public void setTypeShift(String typeShift) {
        this.typeShift = typeShift;
        setCouleurByShift(typeShift);
    }

    public String getCouleur() { return couleur; }

    @Override
    public String toString() {
        return employeNom + " - " + heureDebut + " à " + heureFin;
    }
}