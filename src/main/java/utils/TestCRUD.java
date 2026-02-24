package utils;

import Services.UtilisateurService;
import models.*;
import java.util.List;

public class TestCRUD {
    public static void main(String[] args) {
        // Utilisation du pattern Singleton pour obtenir l'instance unique
        UtilisateurService utilisateurService = UtilisateurService.getInstance();

        System.out.println("=== INITIALISATION DE LA BASE DE DONNÉES ===\n");

        try {
            // ========== CREATE - AJOUT DES DONNÉES ==========
            System.out.println("========== AJOUT DES UTILISATEURS ==========");
            
            // Ajouter des admins
            System.out.println("\n1. Ajout d'Admins:");
            Utilisateur admin1 = new Utilisateur("Dupont", "Jean", "admin@test.com", 
                                               "0612345678", 12345678, "admin123", Role.ADMIN);
            utilisateurService.ajouter(admin1);
            
            Utilisateur admin2 = new Utilisateur("Hamdouni", "Taha", "taha.hamdouni@esprit.tn", 
                                                "99777878", 12345679, "admin123", Role.ADMIN);
            utilisateurService.ajouter(admin2);
            System.out.println("✓ 2 Admins ajoutés");

            // Ajouter un CEO
            System.out.println("\n2. Ajout d'un CEO:");
            Utilisateur ceo = new Utilisateur("Bernard", "Michel", "ceo@stratix.com", 
                                             "0634567890", 34567890, "ceo123", Role.CEO);
            utilisateurService.ajouter(ceo);
            System.out.println("✓ CEO ajouté");

            // Ajouter des employés
            System.out.println("\n3. Ajout d'Employés:");
            Utilisateur employe1 = new Utilisateur("Martin", "Sophie", "sophie.martin@stratix.com", 
                                                  "0623456789", 23456789, "emp123", Role.EMPLOYE,
                                                  "IT", "Développeur Full Stack", 3500.00, "Java, Spring, MySQL, React");
            utilisateurService.ajouter(employe1);
            
            Utilisateur employe2 = new Utilisateur("Dubois", "Pierre", "pierre.dubois@stratix.com", 
                                                   "0645678901", 45678901, "emp123", Role.EMPLOYE,
                                                   "Marketing", "Chef de projet Marketing", 4000.00, "Marketing digital, SEO, Google Ads");
            utilisateurService.ajouter(employe2);
            
            Utilisateur employe3 = new Utilisateur("Lefebvre", "Julie", "julie.lefebvre@stratix.com", 
                                                   "0656781234", 56781234, "emp123", Role.EMPLOYE,
                                                   "IT", "Développeur Backend", 3800.00, "Python, Django, PostgreSQL");
            utilisateurService.ajouter(employe3);
            
            Utilisateur employe4 = new Utilisateur("Roux", "Antoine", "antoine.roux@stratix.com", 
                                                   "0667892345", 67892345, "emp123", Role.EMPLOYE,
                                                   "Finance", "Comptable", 3200.00, "Comptabilité, Excel, SAP");
            utilisateurService.ajouter(employe4);
            System.out.println("✓ 4 Employés ajoutés");

            // Ajouter des responsables
            System.out.println("\n4. Ajout de Responsables:");
            Utilisateur respRH = new Utilisateur("Moreau", "Claire", "claire.moreau@stratix.com", 
                                                 "0656789012", 56789012, "resp123", Role.RESPONSABLE_RH,
                                                 "RH", "Responsable Ressources Humaines", 5000.00, "Gestion RH, Recrutement, Formation");
            utilisateurService.ajouter(respRH);
            
            Utilisateur respProjet = new Utilisateur("Leroy", "Thomas", "thomas.leroy@stratix.com", 
                                                     "0667890123", 67890123, "resp123", Role.RESPONSABLE_PROJET,
                                                     "IT", "Chef de projet IT", 5500.00, "Gestion de projet, Agile, Scrum");
            utilisateurService.ajouter(respProjet);
            
            Utilisateur respProd = new Utilisateur("Petit", "Marie", "marie.petit@stratix.com", 
                                                   "0678901234", 78901234, "resp123", Role.RESPONSABLE_PRODUCTION,
                                                   "Production", "Responsable Production", 5200.00, "Gestion production, Lean, Six Sigma");
            utilisateurService.ajouter(respProd);
            System.out.println("✓ 3 Responsables ajoutés");

            // ========== VÉRIFICATION ==========
            System.out.println("\n========== VÉRIFICATION DES DONNÉES ==========");
            
            // Afficher tous les utilisateurs
            System.out.println("\nListe de TOUS les utilisateurs dans la base:");
            List<Utilisateur> tousUtilisateurs = utilisateurService.getAll();
            System.out.println("Total: " + tousUtilisateurs.size() + " utilisateurs\n");
            
            // Afficher par rôle
            List<Utilisateur> admins = utilisateurService.getByRole(Role.ADMIN);
            System.out.println("ADMINS (" + admins.size() + "):");
            admins.forEach(a -> System.out.println("  - " + a.getNom() + " " + a.getPrenom() + " | Email: " + a.getEmail() + " | Pass: admin123"));
            
            List<Utilisateur> ceos = utilisateurService.getByRole(Role.CEO);
            System.out.println("\nCEO (" + ceos.size() + "):");
            ceos.forEach(c -> System.out.println("  - " + c.getNom() + " " + c.getPrenom() + " | Email: " + c.getEmail() + " | Pass: ceo123"));
            
            List<Utilisateur> employes = utilisateurService.getByRole(Role.EMPLOYE);
            System.out.println("\nEMPLOYÉS (" + employes.size() + "):");
            employes.forEach(e -> System.out.println("  - " + e.getNom() + " " + e.getPrenom() + " | " + e.getPoste() + " | Salaire: " + e.getSalaire() + "€"));
            
            List<Utilisateur> respRHs = utilisateurService.getByRole(Role.RESPONSABLE_RH);
            System.out.println("\nRESPONSABLES RH (" + respRHs.size() + "):");
            respRHs.forEach(r -> System.out.println("  - " + r.getNom() + " " + r.getPrenom() + " | " + r.getPoste()));
            
            List<Utilisateur> respProjets = utilisateurService.getByRole(Role.RESPONSABLE_PROJET);
            System.out.println("\nRESPONSABLES PROJET (" + respProjets.size() + "):");
            respProjets.forEach(r -> System.out.println("  - " + r.getNom() + " " + r.getPrenom() + " | " + r.getPoste()));
            
            List<Utilisateur> respProds = utilisateurService.getByRole(Role.RESPONSABLE_PRODUCTION);
            System.out.println("\nRESPONSABLES PRODUCTION (" + respProds.size() + "):");
            respProds.forEach(r -> System.out.println("  - " + r.getNom() + " " + r.getPrenom() + " | " + r.getPoste()));

            System.out.println("\n=== INITIALISATION TERMINÉE ===");
            System.out.println("✓ La base de données contient maintenant " + tousUtilisateurs.size() + " utilisateurs");
            System.out.println("\nPour te connecter en tant qu'admin:");
            System.out.println("  Email: admin@test.com");
            System.out.println("  Password: admin123");
            
        } catch (Exception e) {
            System.err.println("\n✗ Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
