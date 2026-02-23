package services;

import models.ressource;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class service_ressourceTest {

    static service_ressource service;
    static int idRessourceTest;

    @BeforeAll
    static void setup() {
        service = new service_ressource();
    }

    // ---------- TEST AJOUT ----------
    @Test
    @Order(1)
    void testAjouterRessource() throws SQLException {

        ressource r = new ressource(1,
                "Clavier HP",
                "Matériel informatique",
                20,
                "HP Tunisie"
        );

        service.add(r);

        List<ressource> ressources = service.getAll();

        assertFalse(ressources.isEmpty());

        assertTrue(
                ressources.stream().anyMatch(res ->
                        res.getNom().equals("Clavier HP")
                )
        );
    }

    // ---------- TEST MODIFICATION ----------
    @Test
    @Order(2)
    void testModifierRessource() throws SQLException {

        ressource r = new ressource();
        r.setid(1);
        r.setNom("Clavier Gamer RGB");
        r.setType_ressource("Gaming");
        r.setQuatite(15);
        r.setFournisseur("MyTek");

        service.update(r);

        List<ressource> ressources = service.getAll();

        boolean trouve = ressources.stream()
                .anyMatch(res ->
                        res.getNom().equals("Clavier Gamer RGB")
                );

        assertTrue(trouve);
    }

    // ---------- CLEAN DATABASE ----------
    @AfterAll
    static void cleanUp() throws SQLException {
        service.deleteAll();
    }
}
