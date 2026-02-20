package models;

import java.util.List;
import java.util.Map;

public class DashboardStats {

    // Statistiques générales
    private int totalEmployes;
    private int totalTaches;
    private int totalPlannings;
    private int totalProjets;

    // Statistiques des tâches
    private int tachesAFaire;
    private int tachesEnCours;
    private int tachesTerminees;
    private int tachesEnRetard;

    // Statistiques des plannings
    private int employesEnPoste;
    private int employesAbsents;
    private int planningsMatin;
    private int planningsSoir;
    private int planningsNuit;

    // Statistiques par employé
    private Map<String, Integer> tachesParEmploye;
    private Map<String, Integer> planningsParEmploye;

    // Dernières activités
    private List<String> dernieresTaches;
    private List<String> prochainsPlannings;

    // Constructeur par défaut
    public DashboardStats() {}

    // Getters et Setters
    public int getTotalEmployes() { return totalEmployes; }
    public void setTotalEmployes(int totalEmployes) { this.totalEmployes = totalEmployes; }

    public int getTotalTaches() { return totalTaches; }
    public void setTotalTaches(int totalTaches) { this.totalTaches = totalTaches; }

    public int getTotalPlannings() { return totalPlannings; }
    public void setTotalPlannings(int totalPlannings) { this.totalPlannings = totalPlannings; }

    public int getTotalProjets() { return totalProjets; }
    public void setTotalProjets(int totalProjets) { this.totalProjets = totalProjets; }

    public int getTachesAFaire() { return tachesAFaire; }
    public void setTachesAFaire(int tachesAFaire) { this.tachesAFaire = tachesAFaire; }

    public int getTachesEnCours() { return tachesEnCours; }
    public void setTachesEnCours(int tachesEnCours) { this.tachesEnCours = tachesEnCours; }

    public int getTachesTerminees() { return tachesTerminees; }
    public void setTachesTerminees(int tachesTerminees) { this.tachesTerminees = tachesTerminees; }

    public int getTachesEnRetard() { return tachesEnRetard; }
    public void setTachesEnRetard(int tachesEnRetard) { this.tachesEnRetard = tachesEnRetard; }

    public int getEmployesEnPoste() { return employesEnPoste; }
    public void setEmployesEnPoste(int employesEnPoste) { this.employesEnPoste = employesEnPoste; }

    public int getEmployesAbsents() { return employesAbsents; }
    public void setEmployesAbsents(int employesAbsents) { this.employesAbsents = employesAbsents; }

    public int getPlanningsMatin() { return planningsMatin; }
    public void setPlanningsMatin(int planningsMatin) { this.planningsMatin = planningsMatin; }

    public int getPlanningsSoir() { return planningsSoir; }
    public void setPlanningsSoir(int planningsSoir) { this.planningsSoir = planningsSoir; }

    public int getPlanningsNuit() { return planningsNuit; }
    public void setPlanningsNuit(int planningsNuit) { this.planningsNuit = planningsNuit; }

    public Map<String, Integer> getTachesParEmploye() { return tachesParEmploye; }
    public void setTachesParEmploye(Map<String, Integer> tachesParEmploye) { this.tachesParEmploye = tachesParEmploye; }

    public Map<String, Integer> getPlanningsParEmploye() { return planningsParEmploye; }
    public void setPlanningsParEmploye(Map<String, Integer> planningsParEmploye) { this.planningsParEmploye = planningsParEmploye; }

    public List<String> getDernieresTaches() { return dernieresTaches; }
    public void setDernieresTaches(List<String> dernieresTaches) { this.dernieresTaches = dernieresTaches; }

    public List<String> getProchainsPlannings() { return prochainsPlannings; }
    public void setProchainsPlannings(List<String> prochainsPlannings) { this.prochainsPlannings = prochainsPlannings; }

    // Méthode utilitaire pour afficher un résumé
    @Override
    public String toString() {
        return String.format(
                "DashboardStats{totalEmployes=%d, totalTaches=%d, tachesAFaire=%d, tachesEnCours=%d, tachesTerminees=%d, tachesEnRetard=%d}",
                totalEmployes, totalTaches, tachesAFaire, tachesEnCours, tachesTerminees, tachesEnRetard
        );
    }
}//