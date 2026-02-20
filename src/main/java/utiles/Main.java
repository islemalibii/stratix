package utiles;

import models.Planning;
import models.Tache;
import services.SERVICEPlanning;
import services.SERVICETache;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== Test de connexion à la base de données ===");
        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            System.err.println("Connexion échouée. Arrêt du programme.");
            return;
        }
        System.out.println("Connexion réussie \n");

        // ================== TACHE ==================
        SERVICETache tacheDAO = new SERVICETache();

        Tache nouvelleTache = new Tache(
                "Préparer planning",
                "Organisation des tâches semaine",
                Date.valueOf("2026-03-10"),
                "EN_COURS",
                1,   // employe_id EXISTANT
                3,
                "HAUTE"
        );
        tacheDAO.addTache(nouvelleTache);
        System.out.println("Tâche ajoutée ");

        System.out.println("\n--- Liste des Tâches ---");
        List<Tache> taches = tacheDAO.getAllTaches();
        taches.forEach(t -> System.out.println(
                t.getId() + " | " +
                        t.getTitre() + " | " +
                        t.getStatut() + " | Employé: " +
                        t.getEmployeId()
        ));

        if (!taches.isEmpty()) {
            Tache tacheToUpdate = taches.get(0);
            tacheToUpdate.setStatut("TERMINEE");
            tacheDAO.updateTache(tacheToUpdate);
            System.out.println("Tâche mise à jour ");
        }

        // ================== PLANNING ==================
        SERVICEPlanning planningDAO = new SERVICEPlanning();

        Planning planning = new Planning(
                1,
                Date.valueOf("2026-03-11"),
                Time.valueOf("08:00:00"),
                Time.valueOf("16:00:00"),
                "JOUR"
        );
        planningDAO.addPlanning(planning);
        System.out.println("Planning ajouté ");

        System.out.println("\n--- Liste des Plannings ---");
        planningDAO.getAllPlannings().forEach(p -> System.out.println(
                p.getId() + " | Employé: " +
                        p.getEmployeId() + " | " +
                        p.getDate() + " | " +
                        p.getTypeShift()
        ));

        System.out.println("\n=== Tests CRUD terminés avec succès ! ===");
    }
}
