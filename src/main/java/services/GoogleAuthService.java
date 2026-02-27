package services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import java.io.IOException;
import java.util.Collections;

public class GoogleAuthService {
    private static GoogleAuthService instance;
    
    // Configuration OAuth Google
    private static final String CLIENT_ID = "333706582213-1ubk3268msr4cc8agvtsovn64eb7sjji.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8888/Callback";
    
    private final NetHttpTransport httpTransport;
    private final GsonFactory jsonFactory;
    
    private GoogleAuthService() {
        this.httpTransport = new NetHttpTransport();
        this.jsonFactory = GsonFactory.getDefaultInstance();
    }
    
    public static GoogleAuthService getInstance() {
        if (instance == null) {
            instance = new GoogleAuthService();
        }
        return instance;
    }
    
    /**
     * Authentifier avec Google et récupérer les informations utilisateur
     */
    public GoogleUserInfo authenticateWithGoogle() throws Exception {
        try {
            // Configuration du flux OAuth
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
                .setInstalled(new GoogleClientSecrets.Details()
                    .setClientId(CLIENT_ID)
                    .setClientSecret(CLIENT_SECRET)
                    .setRedirectUris(Collections.singletonList(REDIRECT_URI)));
            
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                jsonFactory,
                clientSecrets,
                Collections.singleton("https://www.googleapis.com/auth/userinfo.email")
            ).setAccessType("online").build();
            
            // Ouvrir le navigateur pour l'authentification
            LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888)
                .build();
            
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            
            // Récupérer les informations utilisateur
            Oauth2 oauth2 = new Oauth2.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Stratix")
                .build();
            
            Userinfo userInfo = oauth2.userinfo().get().execute();
            
            return new GoogleUserInfo(
                userInfo.getEmail(),
                userInfo.getGivenName(),
                userInfo.getFamilyName(),
                userInfo.getPicture()
            );
            
        } catch (Exception e) {
            System.err.println("Erreur d'authentification Google: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Classe pour stocker les informations utilisateur Google
     */
    public static class GoogleUserInfo {
        private final String email;
        private final String firstName;
        private final String lastName;
        private final String pictureUrl;
        
        public GoogleUserInfo(String email, String firstName, String lastName, String pictureUrl) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.pictureUrl = pictureUrl;
        }
        
        public String getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPictureUrl() { return pictureUrl; }
    }
}
