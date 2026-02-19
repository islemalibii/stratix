import controllers.DashboardAdminController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Utilisateur;
import Services.UtilisateurService;
import utils.SessionManager;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        boolean shouldOpenDashboard = false;
        Utilisateur loggedUser = null;
        
        try {
            SessionManager sessionManager = SessionManager.getInstance();
            
            // Vérifier si l'utilisateur est déjà connecté
            if (sessionManager.isLoggedIn()) {
                int userId = sessionManager.getUserId();
                
                if (userId > 0) {
                    try {
                        // Récupérer l'utilisateur depuis la base de données
                        UtilisateurService utilisateurService = UtilisateurService.getInstance();
                        Utilisateur user = utilisateurService.getById(userId);
                        
                        if (user != null && "ADMIN".equals(user.getRole().name())) {
                            shouldOpenDashboard = true;
                            loggedUser = user;
                        } else {
                            // Session invalide, supprimer
                            sessionManager.logout();
                        }
                    } catch (Exception e) {
                        // Erreur de connexion à la base, supprimer la session
                        System.err.println("Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
                        sessionManager.logout();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de la session: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Ouvrir le dashboard ou la page de login
        if (shouldOpenDashboard && loggedUser != null) {
            openDashboard(primaryStage, loggedUser);
        } else {
            openLogin(primaryStage);
        }
    }
    
    private void openLogin(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("Stratix - Gestion Utilisateurs");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    
    private void openDashboard(Stage primaryStage, Utilisateur user) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard_admin.fxml"));
        Parent root = loader.load();
        
        DashboardAdminController controller = loader.getController();
        controller.setCurrentUser(user);
        
        primaryStage.setTitle("Stratix - Dashboard Admin");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

