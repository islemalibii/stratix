package models;

import java.time.LocalDate;

public class EventFeedback {
    private int id;
    private int evenementId;
    private int rating;
    private String commentaire;
    private LocalDate dateFeedback;

    public EventFeedback() {
    }

    public EventFeedback(int id, int evenementId, int rating, String commentaire, LocalDate dateFeedback) {
        this.id = id;
        this.evenementId = evenementId;
        this.rating = rating;
        this.commentaire = commentaire;
        this.dateFeedback = dateFeedback;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDate getDateFeedback() {
        return dateFeedback;
    }

    public void setDateFeedback(LocalDate dateFeedback) {
        this.dateFeedback = dateFeedback;
    }
}
