package services;

import models.produit;
import org.junit.jupiter.api.*;
import service.service_produit;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class service_produitTest {
    static service_produit service;
    static int idProduitTest ;
    @BeforeAll
    static void setup() {
        service = new service_produit();

    }
    @Test
    @Order(1)
    void testAjouterproduit() throws SQLException {
        produit p = new produit(1,
                "Laptop Dell Inspiron 15",
                "Ordinateur portable Intel i5, 8GB RAM, SSD 512GB",
                "Matériel informatique",
                1850.000,
                30,
                10,
                "2026-03-15",
                "Chargeur, carton emballage, étiquette garantie");
        service.add(p);
        List<produit> produits = service.getAll();
        assertFalse(produits.isEmpty());
        assertTrue(
                produits.stream().anyMatch(prod ->
                        prod.getNom().equals("Laptop Dell Inspiron 15")
                )
        );
    }
    @Test
    @Order(2)
    void testModifierproduit() throws SQLException {
        produit p = new produit();
        p.setId(1);
        p.setNom("NomModifie");
        p.setDescription("DéscriptionModifie");
        p.setCategorie("Catégorie Modifie");
        p.setPrix(20000);
        p.setStock_actuel(100);
        p.setStock_min(90);
        p.setDate_creation("2026-03-09");
        p.setRessources_necessaires("ressourcesnécessaireModifie");

        service.update(p);
        List<produit> produits = service.getAll();
        boolean trouve = produits.stream()
                .anyMatch(per ->
                        per.getNom().equals("NomModifie"));
        assertTrue(trouve);
    }






    @AfterAll
    static void cleanUp() throws SQLException {
        service.deleteAll();
    }
}


