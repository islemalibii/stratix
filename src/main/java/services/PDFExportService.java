package services;

import models.Tache;
import models.Employe;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFExportService {

    private static final EmployeeService employeService = new EmployeeService();

    /**
     * Exporte les tâches vers un fichier CSV (compatible Excel)
     * @param taches Liste des tâches à exporter
     * @param filePath Chemin du fichier (peut être .pdf ou .csv)
     */
    public static void exportTachesToPDF(List<Tache> taches, String filePath) {
        PrintWriter writer = null;
        try {
            // Si le chemin se termine par .pdf, on le remplace par .csv
            String csvPath = filePath;
            if (filePath.toLowerCase().endsWith(".pdf")) {
                csvPath = filePath.substring(0, filePath.length() - 4) + ".csv";
            } else if (!filePath.toLowerCase().endsWith(".csv")) {
                csvPath = filePath + ".csv";
            }

            System.out.println("📁 Création du fichier: " + csvPath);

            writer = new PrintWriter(new FileWriter(csvPath));

            // En-tête du fichier
            writer.println("==========================================");
            writer.println("    STRATIX - Gestion des Tâches");
            writer.println("==========================================");
            writer.println();

            // Date d'export
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            writer.println("Exporté le : " + LocalDateTime.now().format(dateTimeFormatter));
            writer.println();

            // Statistiques
            int total = taches.size();
            int aFaire = 0, enCours = 0, terminees = 0;

            for (Tache t : taches) {
                switch(t.getStatut()) {
                    case "A_FAIRE": aFaire++; break;
                    case "EN_COURS": enCours++; break;
                    case "TERMINEE": terminees++; break;
                }
            }

            writer.println("📊 RÉSUMÉ DES TÂCHES");
            writer.println("────────────────────");
            writer.println("Total tâches : " + total);
            writer.println("À faire      : " + aFaire);
            writer.println("En cours     : " + enCours);
            writer.println("Terminées    : " + terminees);
            writer.println();

            // En-tête du tableau (format CSV)
            writer.println("📋 LISTE DÉTAILLÉE DES TÂCHES");
            writer.println("─────────────────────────────────────────────────────────────────────────────");
            writer.println("ID;Titre;Employé;Deadline;Priorité;Statut");
            writer.println("─────────────────────────────────────────────────────────────────────────────");

            // Données
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Tache t : taches) {
                Employe emp = employeService.getEmployeById(t.getEmployeId());
                String employeName = (emp != null) ? emp.getUsername() : "Employé " + t.getEmployeId();

                String deadline = t.getDeadline() != null ?
                        t.getDeadline().toLocalDate().format(dateFormatter) : "Non définie";

                // Écrire la ligne CSV
                writer.println(
                        t.getId() + ";" +
                                t.getTitre() + ";" +
                                employeName + ";" +
                                deadline + ";" +
                                t.getPriorite() + ";" +
                                t.getStatut()
                );
            }

            writer.println("─────────────────────────────────────────────────────────────────────────────");
            writer.println();
            writer.println("Document généré automatiquement par Stratix");
            writer.println("Total: " + total + " tâches");

            System.out.println("✅ Fichier CSV créé avec succès: " + csvPath);
            System.out.println("📊 Taille du fichier: " + new File(csvPath).length() + " octets");

            // Ouvrir le dossier contenant le fichier
            openFolder(csvPath);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'export: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Ouvre le dossier contenant le fichier dans l'explorateur Windows
     */
    private static void openFolder(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                String folderPath = file.getParent();
                System.out.println("📂 Ouverture du dossier: " + folderPath);
                Runtime.getRuntime().exec("explorer.exe " + folderPath);
            } else {
                System.err.println("❌ Le fichier n'existe pas: " + path);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Impossible d'ouvrir le dossier: " + e.getMessage());
        }
    }

    /**
     * Version alternative qui garde l'extension .pdf mais crée un vrai fichier texte
     */
    public static void exportTachesToPDFLegacy(List<Tache> taches, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            writer.println("=== STRATIX - Gestion des Tâches ===");
            writer.println("Exporté le : " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            writer.println();

            // Statistiques
            int total = taches.size();
            int aFaire = 0, enCours = 0, terminees = 0;

            for (Tache t : taches) {
                switch(t.getStatut()) {
                    case "A_FAIRE": aFaire++; break;
                    case "EN_COURS": enCours++; break;
                    case "TERMINEE": terminees++; break;
                }
            }

            writer.println("Total tâches : " + total);
            writer.println("À faire : " + aFaire);
            writer.println("En cours : " + enCours);
            writer.println("Terminées : " + terminees);
            writer.println();

            // En-tête
            writer.println("ID | Titre | Employé | Deadline | Priorité | Statut");
            writer.println("----------------------------------------------------");

            // Données
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Tache t : taches) {
                Employe emp = employeService.getEmployeById(t.getEmployeId());
                String employeName = (emp != null) ? emp.getUsername() : "Employé " + t.getEmployeId();

                String deadline = t.getDeadline() != null ?
                        t.getDeadline().toLocalDate().format(formatter) : "Non définie";

                writer.printf("%d | %s | %s | %s | %s | %s%n",
                        t.getId(),
                        t.getTitre(),
                        employeName,
                        deadline,
                        t.getPriorite(),
                        t.getStatut()
                );
            }

            writer.println();
            writer.println("Document généré automatiquement par Stratix");

            System.out.println("✅ Fichier exporté: " + filePath);
            openFolder(filePath);

        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}