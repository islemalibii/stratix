package models;

import java.sql.Date;

import java.sql.Time;

public class Planning {

    private int id;
    private int employeId;
    private Date date;
    private Time heureDebut;
    private Time heureFin;
    private String typeShift;

    public Planning() {}

    public Planning(int employeId, Date date, Time heureDebut, Time heureFin, String typeShift) {
        this.employeId = employeId;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.typeShift = typeShift;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeId() { return employeId; }
    public void setEmployeId(int employeId) { this.employeId = employeId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Time getHeureDebut() { return heureDebut; }
    public void setHeureDebut(Time heureDebut) { this.heureDebut = heureDebut; }

    public Time getHeureFin() { return heureFin; }
    public void setHeureFin(Time heureFin) { this.heureFin = heureFin; }

    public String getTypeShift() { return typeShift; }
    public void setTypeShift(String typeShift) { this.typeShift = typeShift; }
}
