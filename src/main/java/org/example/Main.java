package org.example;

import models.Projet;
import services.ProjetService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ProjetService service = new ProjetService();

        //Création
        //Projet p = new Projet(14, "Projet2", "barra trah 2", new Date(), new Date(), 1234, "En cours", 98);
        //service.ajouterProjet(p);


        //update
        //Projet p1 = new Projet(16, "ProjetLouwel", "barra trah nupdatou", new Date(), new Date(), 11111, "en cours", 80);
        //service.mettreAJourProjet(p1);


        //Affichage des projets disponibles
        System.out.println("\nListe des projets disponibles:");
        service.listerTousLesProjets().forEach(System.out::println);
        //Affichage des projets archivés avec verif (ken feragh wella)
        System.out.println("\nListe des projets archivés:");
        List<Projet> archives = service.listerArchives();
        if (archives.isEmpty()) {
            System.out.println("Aucun projet dans les archives.");
        } else {
            archives.forEach(System.out::println);
        }

        //Suppression
        //service.archiverUnProjet(1);


    }
    }
