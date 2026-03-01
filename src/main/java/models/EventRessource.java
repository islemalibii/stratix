package models;

public class EventRessource {
    private int id;
    private int eventId;
    private int ressourceId;
    private int quantite;

    public EventRessource() {}

    public EventRessource(int eventId, int ressourceId, int quantite) {
        this.eventId = eventId;
        this.ressourceId = ressourceId;
        this.quantite = quantite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getRessourceId() {
        return ressourceId;
    }

    public void setRessourceId(int ressourceId) {
        this.ressourceId = ressourceId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
