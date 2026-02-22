package org.example;

import models.enums.EventStatus;
import models.enums.EventType;
import services.ServiceEvenemnet;
import models.Evenement;
import utils.MyDataBase;
import models.Service;
import services.ServiceService;
import models.Projet;
import services.ProjetService;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // Initializing Services
        ProjetService projetService = new ProjetService();
        ServiceService serviceManager = null;
        ServiceEvenemnet evenementManager = null;

        try {
            // Establishing Connections
            serviceManager = new ServiceService();
            evenementManager = new ServiceEvenemnet();
            System.out.println("Connexion établie via Singleton");


            System.out.println("\n--- Tests Événements ---");
            Evenement e1 = new Evenement(1, "Jaw", "barchajaw", EventType.formation, EventStatus.annuler, "jarda", LocalDate.of(2026, 2, 16));
            Evenement e2 = new Evenement(2, "7aflaa", "jawbdhawedds", EventType.recrutement, EventStatus.planifier, "salleM002", LocalDate.of(2026, 2, 16));

            // evenementManager.add(e1);
            evenementManager.update(e2);

            System.out.println("Événements actifs : " + evenementManager.getAll());

            int idEventToArchive = 1;
            evenementManager.archiver(idEventToArchive);
            System.out.println("Événement ID " + idEventToArchive + " archivé.");
            System.out.println("Événements archivés : " + evenementManager.getAllArchieved());

            System.out.println("\n--- Tests Projets ---");

            // Optional Creation/Update tests
            // Projet p = new Projet(14, "Projet2", "Description", new Date(), new Date(), 1234, "En cours", 98);
            // projetService.ajouterProjet(p);

            System.out.println("Liste des projets disponibles:");
            projetService.listerTousLesProjets().forEach(System.out::println);

            System.out.println("\nListe des projets archivés:");
            List<Projet> archives = projetService.listerArchives();
            if (archives.isEmpty()) {
                System.out.println("Aucun projet dans les archives.");
            } else {
                archives.forEach(System.out::println);
            }


            System.out.println("\n--- Tests Services ---");

            Service s1 = new Service(0, "Developpement Web", "Creation site web", "2026-02-08", "2026-03-01", "2026-06-30", 101, 5000.00, 1, false);
            Service s2 = new Service(0, "Formation Java", "Cours programmation Java", "2026-02-08", "2026-02-15", "2026-04-15", 102, 3000.00, 2, false);
            Service s3 = new Service(0, "Maintenance", "Maintenance serveurs", "2026-02-08", "2026-02-10", "2026-12-31", 103, 2000.00, 3, false);

            serviceManager.ajouter(s1);
            serviceManager.ajouter(s2);
            serviceManager.ajouter(s3);
            System.out.println("3 services ajoutés avec succès.");

            List<Service> services = serviceManager.afficherAll();
            System.out.println("Nombre de services actifs : " + services.size());

            for (Service s : services) {
                String catName = (s.getCategorie() != null) ? s.getCategorie().getNom() : "Sans catégorie";
                System.out.println(" - [" + s.getId() + "] " + s.getTitre() + " | Cat: " + catName + " | Budget: " + s.getBudget() + " DT");
            }

            if (!services.isEmpty()) {
                Service sModif = services.get(0);
                sModif.setTitre("Developpement Web Avancé");
                sModif.setBudget(5500.00);
                serviceManager.updateTitre(sModif);
                System.out.println("Service ID " + sModif.getId() + " mis à jour.");
            }

            if (services.size() >= 3) {
                int dernierId = services.get(services.size() - 1).getId();
                serviceManager.archiver(dernierId);
                System.out.println("Service ID " + dernierId + " archivé.");
            }

            System.out.println("\n--- Résultat Final ---");
            System.out.println("Services actifs : " + serviceManager.afficherAll().size());
            System.out.println("Services archivés : " + serviceManager.afficherArchives().size());

        } catch (SQLException e) {
            System.err.println("Erreur SQL détectée : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nTests terminés.");
        }
    }
}