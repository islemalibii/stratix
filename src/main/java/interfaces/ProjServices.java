package interfaces;

import models.Projet;

import java.util.List;

public interface ProjServices {

    void ajouterProjet(Projet p);

    List<Projet> listerTousLesProjets();

    List<Projet> listerArchives();

    void archiverUnProjet(int id);

    void mettreAJourProjet(Projet p);

    void supprimerUnProjet(int id);

    Projet chercherProjetParId(int idProjet);
}