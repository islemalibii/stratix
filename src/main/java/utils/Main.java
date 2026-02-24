package utils;

import models.Planning;
import models.Tache;
import Services.SERVICEPlanning;
import Services.SERVICETache;
import utils.MyDataBase; // Import your Singleton

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== Test de connexion via Singleton MyDataBase ===");

        // Use the Singleton to get the connection
        Connection conn = MyDataBase.getInstance().getCnx();

        if (conn == null) {
            System.err.println("❌ Connexion échouée. Arrêt du programme.");
            return;
        }
        System.out.println("✅ Connexion réussie à la base 'stratix' \n");

        // ================== TACHE ==================
        // Since we updated SERVICETache, it will now automatically use the Singleton connection
        SERVICETache tacheDAO = new SERVICETache();

        Tache nouvelleTache = new Tache(
                "Préparer planning",
                "Organisation des tâches semaine",
                Date.valueOf("2026-03-10"),
                "EN_COURS",
                1,   // Assurez-vous que cet employe_id existe en base
                3,   // Assurez-vous que ce projet_id existe en base
                "HAUTE"
        );
        tacheDAO.addTache(nouvelleTache);
        System.out.println("✅ Tâche ajoutée ");

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
            System.out.println("✅ Tâche mise à jour ");
        }

        // ================== PLANNING ==================
        // SERVICEPlanning is also updated to use the Singleton
        SERVICEPlanning planningDAO = new SERVICEPlanning();

        Planning planning = new Planning(
                1,
                Date.valueOf("2026-03-11"),
                Time.valueOf("08:00:00"),
                Time.valueOf("16:00:00"),
                "JOUR"
        );
        planningDAO.addPlanning(planning);
        System.out.println("✅ Planning ajouté ");

        System.out.println("\n--- Liste des Plannings ---");
        planningDAO.getAllPlannings().forEach(p -> System.out.println(
                p.getId() + " | Employé: " +
                        p.getEmployeId() + " | " +
                        p.getDate() + " | " +
                        p.getTypeShift()
        ));

        System.out.println("\n=== Tests CRUD terminés avec succès sur la base unique ! ===");
    }
}