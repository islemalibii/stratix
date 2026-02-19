package org.example;

import model.Service;
import service.ServiceService;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        ServiceService service = null;

        try {

            service = new ServiceService();
            System.out.println(" Connexion établie");

            System.out.println("Ajout de services:");

            Service s1 = new Service(0, "Developpement Web",
                    "Creation site web", "2026-02-08", "2026-03-01", "2026-06-30",
                    101, 5000.00, 1);

            Service s2 = new Service(0, "Formation Java",
                    "Cours programmation Java", "2026-02-08", "2026-02-15", "2026-04-15",
                    102, 3000.00, 2);

            Service s3 = new Service(0, "Maintenance",
                    "Maintenance serveurs", "2026-02-08", "2026-02-10", "2026-12-31",
                    103, 2000.00, 3);

            service.ajouter(s1);
            service.ajouter(s2);
            service.ajouter(s3);
            System.out.println("   3 services ajoutés");

            System.out.println(" Liste des services:");
            List<Service> services = service.afficherAll();

            if (services.isEmpty()) {
                System.out.println("   Aucun service trouvé");
            } else {
                System.out.println("   Nombre de services: " + services.size());
                for (Service s : services) {
                    String catName = (s.getCategorie() != null) ? s.getCategorie().getNom() : "Non catégorisé";
                    System.out.println("   - ID: " + s.getId() +
                            " | Titre: " + s.getTitre() +
                            " | Catégorie: " + catName +
                            " | Budget: " + s.getBudget() + " DT");
                }
            }

            System.out.println(" Modification du premier service:");
            if (!services.isEmpty()) {
                Service sModif = services.get(0);
                sModif.setTitre("Developpement Web Avancé");
                sModif.setBudget(5500.00);
                service.updateTitre(sModif);
                System.out.println("   Service ID " + sModif.getId() + " modifié");
            }

            System.out.println(" Liste après modification:");
            services = service.afficherAll();
            for (Service s : services) {
                System.out.println("   - " + s.getTitre() + " (ID: " + s.getId() + ") - " + s.getBudget() + " DT");
            }

            System.out.println(" Suppression du dernier service:");
            if (!services.isEmpty()) {
                int dernierId = services.get(services.size() - 1).getId();
                service.delete(dernierId);
                System.out.println("  Service ID " + dernierId + " supprimé");
            }

            System.out.println("État final:");
            services = service.afficherAll();
            System.out.println("   Services restants: " + services.size());

            System.out.println(" Recherche par ID:");
            if (!services.isEmpty()) {
                Service sTrouve = service.getById(services.get(0).getId());
                if (sTrouve != null) {
                    System.out.println(" Service trouvé: " + sTrouve.getTitre());
                }
            }

        } catch (SQLException e) {
            System.out.println(" Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (service != null) {
                try {
                    service.close();
                    System.out.println("Connexion fermée");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}