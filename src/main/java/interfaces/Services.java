package interfaces;

import java.util.List;
import model.Projet;

public interface Services {

    void ajouterProjet(Projet p);

    List<Projet> listerTousLesProjets();

    List<Projet> listerArchives();

    void archiverUnProjet(int id);

    void mettreAJourProjet(Projet p);

    void supprimerUnProjet(int id);
}
