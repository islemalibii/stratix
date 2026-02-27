package services;

import models.Role;
import models.Utilisateur;
import java.util.List;
import java.util.stream.Collectors;

public class ChatbotService {
    
    private static ChatbotService instance;
    private UtilisateurService utilisateurService;
    
    private ChatbotService() {
        utilisateurService = UtilisateurService.getInstance();
    }
    
    public static ChatbotService getInstance() {
        if (instance == null) {
            instance = new ChatbotService();
        }
        return instance;
    }
    
    public String processQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "Posez-moi une question! 😊";
        }
        
        String q = question.toLowerCase().trim();
        
        // Questions d'aide
        if (q.contains("aide") || q.contains("help") || q.equals("?")) {
            return getHelp();
        }
        
        // Questions statistiques
        if (q.contains("combien") && q.contains("employé")) {
            return getEmployeesCount();
        }
        if (q.contains("combien") && q.contains("admin")) {
            return getAdminsCount();
        }
        if (q.contains("combien") && q.contains("utilisateur")) {
            return getTotalUsersCount();
        }
        if (q.contains("combien") && q.contains("inactif")) {
            return getInactiveUsersCount();
        }
        
        // Questions d'identification
        if (q.contains("qui") && q.contains("responsable rh")) {
            return getResponsablesRH();
        }
        if (q.contains("qui") && q.contains("ceo")) {
            return getCEOs();
        }
        if (q.contains("liste") && q.contains("admin")) {
            return getAdminsList();
        }
        if (q.contains("liste") && q.contains("employé")) {
            return getEmployeesList();
        }
        
        // Question non reconnue
        return "Je n'ai pas compris votre question. Tapez 'aide' pour voir ce que je peux faire. 🤔";
    }
    
    private String getHelp() {
        return "🤖 Je peux répondre à ces questions:\n\n" +
               "📊 Statistiques:\n" +
               "• Combien d'employés actifs?\n" +
               "• Combien d'admins?\n" +
               "• Combien d'utilisateurs?\n" +
               "• Combien d'utilisateurs inactifs?\n\n" +
               "👥 Identification:\n" +
               "• Qui est responsable RH?\n" +
               "• Qui est CEO?\n" +
               "• Liste des admins\n" +
               "• Liste des employés";
    }
    
    private String getEmployeesCount() {
        try {
            List<Utilisateur> employes = utilisateurService.getByRole(Role.EMPLOYE);
            long actifs = employes.stream().filter(Utilisateur::isActif).count();
            return String.format("📊 Il y a %d employés actifs sur %d au total.", actifs, employes.size());
        } catch (Exception e) {
            return "❌ Erreur lors de la récupération des données.";
        }
    }
    
    private String getAdminsCount() {
        try {
            List<Utilisateur> admins = utilisateurService.getByRole(Role.ADMIN);
            long actifs = admins.stream().filter(Utilisateur::isActif).count();
            return String.format("🔐 Il y a %d administrateurs actifs.", actifs);
        } catch (Exception e) {
            return "❌ Erreur lors de la récupération des données.";
        }
    }
    
    private String getTotalUsersCount() {
        try {
            List<Utilisateur> users = utilisateurService.getAll();
            long actifs = users.stream().filter(Utilisateur::isActif).count();
            return String.format("👥 Il y a %d utilisateurs au total (%d actifs, %d inactifs).", 
                               users.size(), actifs, users.size() - actifs);
        } catch (Exception e) {
            return "❌ Erreur lors de la récupération des données.";
        }
    }
    
    private String getInactiveUsersCount() {
        try {
            List<Utilisateur> users = utilisateurService.getAll();
            long inactifs = users.stream().filter(u -> !u.isActif()).count();
            return String.format("💤 Il y a %d utilisateurs inactifs.", inactifs);
        } catch (Exception e) {
            return "❌ Erreur lors de la récupération des données.";
        }
    }
    
    private String getResponsablesRH() {
        try {
            List<Utilisateur> responsables = utilisateurService.getByRole(Role.RESPONSABLE_RH);
            if (responsables.isEmpty()) {
                return "❌ Aucun responsable RH trouvé.";
            }
            StringBuilder sb = new StringBuilder("👔 Responsables RH:\n\n");
            for (Utilisateur u : responsables) {
                if (u.isActif()) {
                    sb.append(String.format("• %s %s\n  📧 %s\n  📱 %s\n\n", 
                                          u.getNom(), u.getPrenom(), u.getEmail(), u.getTel()));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "❌ Erreur lors de la récupération des données.";
        }
    }
    
    private String getCEOs() {
        try {
            List<Utilisateur> ceos = utilisateurService.getByRole(Role.CEO);
            if (ceos.isEmpty()) {
                return "❌ Aucun CEO trouvé.";
            }
            StringBuilder sb = new StringBuilder("⭐ CEOs:\n\n");
            for (Utilisateur u : ceos) {
                if (u.isActif()) {
                    sb.append(String.format("• %s %s\n  📧 %s\n  📱 %s\n\n", 
                                          u.getNom(), u.getPrenom(), u.getEmail(), u.getTel()));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "❌ Erreur lors de la récupération des données.";
        }
    }
    
    private String getAdminsList() {
        try {
            List<Utilisateur> admins = utilisateurService.getByRole(Role.ADMIN);
            if (admins.isEmpty()) {
                return "❌ Aucun administrateur trouvé.";
            }
            StringBuilder sb = new StringBuilder("🔐 Liste des administrateurs:\n\n");
            int i = 1;
            for (Utilisateur u : admins) {
                if (u.isActif()) {
                    sb.append(String.format("%d. %s %s\n   📧 %s\n\n", 
                                          i++, u.getNom(), u.getPrenom(), u.getEmail()));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "❌ Erreur lors de la récupération des données.";
        }
    }
    
    private String getEmployeesList() {
        try {
            List<Utilisateur> employes = utilisateurService.getByRole(Role.EMPLOYE)
                    .stream()
                    .filter(Utilisateur::isActif)
                    .limit(10)
                    .collect(Collectors.toList());
            
            if (employes.isEmpty()) {
                return "❌ Aucun employé trouvé.";
            }
            
            StringBuilder sb = new StringBuilder("💼 Liste des employés (10 premiers):\n\n");
            int i = 1;
            for (Utilisateur u : employes) {
                sb.append(String.format("%d. %s %s - %s\n", 
                                      i++, u.getNom(), u.getPrenom(), u.getEmail()));
            }
            return sb.toString();
        } catch (Exception e) {
            return "❌ Erreur lors de la récupération des données.";
        }
    }
}
