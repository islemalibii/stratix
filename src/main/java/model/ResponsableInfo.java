package model;

public class ResponsableInfo {
    private int id;
    private String nomComplet;
    private String email;
    private String poste;

    public ResponsableInfo(int id, String nomComplet, String email, String poste) {
        this.id = id;
        this.nomComplet = nomComplet;
        this.email = email;
        this.poste = poste;
    }

    public int getId() { return id; }
    public String getNomComplet() { return nomComplet; }
    public String getEmail() { return email; }
    public String getPoste() { return poste; }

    @Override
    public String toString() {
        return nomComplet + " (" + poste + ")";
    }
}