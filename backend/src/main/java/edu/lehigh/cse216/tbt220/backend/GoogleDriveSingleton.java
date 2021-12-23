
package edu.lehigh.cse216.tbt220.backend;

import java.util.Collections;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

// Import Google's JSON library
import com.google.gson.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

//imports for drive upload/download
import com.google.api.client.auth.oauth2.Credential;
// import com.google.api.client.googleapis.auth.oauth2.GoogleCredential.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.client.http.FileContent;

public class GoogleDriveSingleton {
    //Main Drive object
    private static Drive driveService;

    //Global Vars for file io
    private static final String APPLICATION_NAME = "Google Drive API Java App";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String serviceAccountID = "googledrive@grindersapp-phase2.iam.gserviceaccount.com";
    // private static final String CREDENTIALS_CONTENT = "{\"web\":{\"client_id\":\"649625495044-a7b17r9ktheh82dgbflb5f18a9k34dea.apps.googleusercontent.com\",\"project_id\":\"grindersapp-phase2\",\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\",\"token_uri\":\"https://oauth2.googleapis.com/token\",\"auth_provider_x509_cert_url\":\"https://www.googleapis.com/oauth2/v1/certs\",\"client_secret\":\"-C9L3WbVSUpM4dpRC7LVEFC9\",\"redirect_uris\":[\"https://cse216-phase1.herokuapp.com/messages\",\"https://cse216-phase1.herokuapp.com/spec_runner.html\",\"https://cse216project.herokuapp.com\",\"http://127.0.0.1:8080\",\"http://localhost:8080\",\"https://cse216-phase1.herokuapp.com\"],\"javascript_origins\":[\"https://cse216project.herokuapp.com\",\"http://localhost:8080\",\"http://127.0.0.1:8080\",\"https://cse216-phase1.herokuapp.com\"]}}";
    
    //Get Drive instance
    public static Drive getDriveInstance() {
        
        //Check if instance has already been created
        if (driveService == null) {
            // Build a new authorized API client service.
            NetHttpTransport HTTP_TRANSPORT = null;
            try {
                HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(JSON_FACTORY, serviceAccountID))
                .setApplicationName(APPLICATION_NAME)
                .build(); 
        }

        return driveService;
    }

    //Creates an authorized Credential object.
    private static GoogleCredential getCredentials(JsonFactory jsonFactory, String serviceAccountId) {
        try {
            System.out.println("Getting credentials");

            NetHttpTransport HTTP_TRANSPORT = null;
            try {
                HTTP_TRANSPORT =  GoogleNetHttpTransport.newTrustedTransport();
            } catch (GeneralSecurityException ex) {
                ex.printStackTrace();
            }

            java.io.File p12File = new java.io.File("/app/target/classes/web/credentials.p12");

            GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream("/app/target/classes/web/credentials.json"))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));
           
            return credential;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}