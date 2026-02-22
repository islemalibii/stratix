package services;

import models.Projet;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjetServiceTest {
    static ProjetService service;
    static int idProjTest;

    @BeforeAll
    static void setup() {
        service = new ProjetService();
    }

    @BeforeEach
    void init() throws SQLException {
        Date d = new Date();

        Projet p = new Projet();
        p.setNom("ProjetTest");
        p.setDescription("Description test");
        p.setBudget(1000.0);
        p.setStatut("En cours");
        p.setProgression(10);
        p.setDateDebut(d);
        p.setDateFin(d);

        service.ajouterProjet(p);

        List<Projet> projets = service.listerTousLesProjets();
        if (!projets.isEmpty()) {
            Projet last = projets.get(projets.size() - 1);
            idProjTest = last.getId();
        }
    }

    @Test
    @Order(1)
    void testAjouterProjet() throws SQLException {
        List<Projet> projets = service.listerTousLesProjets();
        assertTrue(
                projets.stream().anyMatch(projet -> projet.getNom().equals("ProjetTest")),
                "Le projet devrait être présent dans la base de données."
        );
    }

    @Test
    @Order(2)
    void testModifierProjet() throws SQLException {
        Date nouvelleDate = new Date();

        Projet p = new Projet();
        p.setId(idProjTest);
        p.setNom("NomModifie");
        p.setDescription("DescriptionModifiee");
        p.setBudget(5000.0);
        p.setStatut("En cours");
        p.setProgression(50);
        p.setDateDebut(nouvelleDate);
        p.setDateFin(nouvelleDate);

        service.mettreAJourProjet(p);

        List<Projet> projets = service.listerTousLesProjets();

        boolean trouve = projets.stream()
                .anyMatch(proj ->
                        proj.getId() == idProjTest && proj.getNom().equals("NomModifie")
                );

        assertTrue(trouve, "Le nom du projet devrait avoir été modifié en base.");
    }

    @Test
    @Order(3)
    void testSupprimerProjet() throws SQLException {
        service.archiverUnProjet(idProjTest);

        List<Projet> projets = service.listerTousLesProjets();

        boolean existe = projets.stream()
                .anyMatch(p -> p.getId() == idProjTest);

        assertFalse(existe, "Le projet archivé ne devrait plus figurer dans la liste des projets actifs.");
    }

    @AfterAll
    static void cleanUp() throws SQLException {
        if (idProjTest > 0) {
            service.supprimerUnProjet(idProjTest);
        }
        System.out.println("Testing done safely :)");
    }
}