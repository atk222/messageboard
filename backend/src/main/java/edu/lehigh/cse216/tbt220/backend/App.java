package edu.lehigh.cse216.tbt220.backend;

import java.util.Collections;
import java.util.Collection;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

//ByteStream Conversion
import java.util.Base64;
import java.util.Base64.Decoder;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

import java.nio.file.Path;
import java.nio.file.FileSystems;

/**
 * For now, our app creates an HTTP server that can only get and add data.
 */
public class App {
    public static void main(String[] args) {
        // gson provides us with a way to turn JSON into objects, and objects
        // into JSON.
        //
        // NB: it must be final, so that it can be accessed from our lambdas
        //
        // NB: Gson is thread-safe. See
        // https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse
        final Gson gson = new Gson();
        final HttpTransport htrans = new NetHttpTransport();
        final JsonFactory jfac = new GsonFactory();

        // dataStore holds all of the data that has been provided via HTTP
        // requests
        //
        // NB: every time we shut down the server, we will lose all data, and
        // every time we start the server, we'll have an empty dataStore,
        // with IDs starting over from 0.
        // Get the port on which to listen for requests
        Spark.port(getIntFromEnv("PORT", 5432));
        String db_url = System.getenv("DATABASE_URL");

        // Get a fully-configured connection to the database, or exit
        // immediately
        Database db = Database.getDatabase(db_url);
        if (db == null)
            return;
        db.dropTables();
        db.createTables();
        db.loadLocations();

        // Set up the location for serving static files. If the STATIC_LOCATION
        // environment variable is set, we will serve from it. Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");

        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        String cors_enabled = System.getenv("CORS_ENABLED");
        if (cors_enabled.equals("True")) {
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
        }

        Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
        System.out.println(path);

        // Set up a route for serving the main page
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        java.io.File f = new java.io.File("."); // current directory

        java.io.File[] files = f.listFiles();
        for (java.io.File file : files) {
            if (file.isDirectory()) {
                System.out.print("directory:");
            } else {
                System.out.print("     file:");
            }

            try {
                System.out.println(file.getCanonicalPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // POSTS
        // GET route that returns all message titles and Ids. All we do is get
        // the data, embed it in a StructuredResponse, turn it into JSON, and
        // return it. If there's no data, we return "[]", so there's no need
        // for error handling.
        Spark.get("/messages", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            String sessionID = request.params("session_id");

            response.status(200);
            response.type("application/json");
            return gson.toJson(new StructuredResponse("ok", null, db.selectAllPosts()));
        });

        // GET route that returns everything for a single row in the DataStore.
        // The ":id" suffix in the first parameter to get() becomes
        // request.params("id"), so that we can get the requested row ID. If
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error. Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a row with data.
        Spark.get("/messages/:post_id", (request, response) -> {
            int postID = Integer.parseInt(request.params("post_id"));

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            RowData data = db.selectPost(postID);

            if (data == null) {
                return gson.toJson(new StructuredResponse("error", postID + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });

        // POST route for adding a new element to the DataStore. This will read
        // JSON from the body of the request, turn it into a SimpleRequest
        // object, extract the title and message, insert them, and return the
        // ID of the newly created row.

        Spark.post("/messages", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            // describes the error.
            if(req.mFile != null){
                // Build a new authorized API client service.
                Drive service = GoogleDriveSingleton.getDriveInstance();  
                
                //File to have decoded bytes written to
                java.io.File decodedFile = new java.io.File(req.mFileName);
                decodedFile.createNewFile();

                //Translating web content to a byte array
                byte[] bytes = DatatypeConverter.parseBase64Binary((req.mFile).substring((req.mFile).indexOf(",") + 1));
                
                OutputStream os = new FileOutputStream(decodedFile);
                os.write(bytes);
                os.close();
         
                //This is the upload to drive process
                File fileMetadata = new File();
                fileMetadata.setName(req.mFileName);
                
                //sending the file to drive
                FileContent mediaContent = new FileContent(req.mFileContent, decodedFile);
                File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id, webViewLink")
                    //.setFields("webViewLink")
                    .execute();

                //get and set Id for drive file
                String file_Id = file.getId();

                //Get file link
                String fileLink = file.getWebViewLink();
                System.out.println(file_Id);

                // Print the names and IDs for up to 10 files.
                FileList result = service.files().list()
                    .setPageSize(10)
                    .execute();
                List<File> driveFiles = result.getFiles();
                
                if (driveFiles == null || driveFiles.isEmpty()) {
                    System.out.println("No files found.");
                } else {
                    System.out.println("Files:");
                    for (File driveFile : driveFiles) {
                    System.out.printf("%s (%s)\n", driveFile.getName(), driveFile.getId());
                    }
                }
                System.out.println("Printing title and location");
                System.out.println(req.mTitle + " " + req.mLocation);
                //storing the post data in postgres
                int newId = db.insertPost(req.mUserID, req.mTitle, req.mMessage, req.mLink, file_Id, req.mFile, req.mFileName, fileLink, req.mLocation);
                if(newId == -1){
                    return gson.toJson(new StructuredResponse("error", "error performing file insertion", null));
                }
                else{
                    return gson.toJson(new StructuredResponse("Ok", "File insert success "+ newId, null));
                }
            }
            else{
                response.status(200);
                response.type("application/json");
                // NB: createEntry checks for null title and message
                int newId = db.insertPost(req.mUserID, req.mTitle, req.mMessage, req.mLink, null, req.mFile, req.mFileName, null, req.mLocation);
                if (newId == -1) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "" + newId, null));
                }
            } 
        });

        // PUT route for updating a row in the Database. This is almost
        // exactly the same as POST
        Spark.put("/messages/:post_id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int postID = Integer.parseInt(request.params("post_id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            
            //getting old fileID
            String fileId = db.getPostFileID(postID);
            if(req.mFile != null){
                
                // Build a new authorized API client service.
                Drive service = GoogleDriveSingleton.getDriveInstance();  
                
                    //File to have decoded bytes written to
                java.io.File decodedFile = new java.io.File(req.mFileName);
                decodedFile.createNewFile();
                
                //Translating web content to a byte array
                byte[] bytes = DatatypeConverter.parseBase64Binary((req.mFile).substring((req.mFile).indexOf(",") + 1));
                
                OutputStream os = new FileOutputStream(decodedFile);
                os.write(bytes);
                os.close();
                
                //Creating drive file to replace old
                File fileMetadata = new File();
                fileMetadata.setName(req.mFileName);
                
                //updating the file
                File newFile = service.files().update(fileId, fileMetadata)
                    .setFields("id")
                    .execute();
                
                    //getting new file Id
                String new_file_Id = newFile.getId();
                
                // ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                
                int result = db.updatePost(postID, req.mTitle, req.mMessage, req.mLink, new_file_Id, req.mFile, req.mFileName, req.mLocation);
                
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update row " + postID, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "updated file successfully", result));
                }
            }
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            
            int result = db.updatePost(postID, req.mTitle, req.mMessage, req.mLink, null, req.mFile, req.mFileName, req.mLocation);

            if (result == -1) {
                return gson.toJson(new StructuredResponse("error", "unable to update row " + postID, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, result));
            }
        });

        // Adding a route for updating the number of likes in the Database
        Spark.put("/messages/:post_id/:user_id/likes", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            String userID = request.params("user_id");
            int postID = Integer.parseInt(request.params("post_id"));

            int numLikes = 0;
            int numDeletes = 0;
            
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            //get query to check for status on post_id
            String status = db.checkLikeDislikeStatus(userID, postID);
            
            //then if it is 'liked' do nothing 
            if (status.equals("none")) {
                numLikes = db.updateLikes(postID, "up");
                numDeletes = db.insertLikeDislikeStore(userID, postID, "liked");
            } else if (status.equals("disliked")) {
                numLikes = db.updateDislikes(postID, "down");
                numLikes = db.updateLikes(postID, "up");
                numDeletes = db.deleteLikeDislikeStore(userID, postID);
                numLikes = db.insertLikeDislikeStore(userID, postID, "liked");
            } else if (status.equals("liked")) {
                numLikes = db.updateLikes(postID, "down");
                numDeletes = db.deleteLikeDislikeStore(userID, postID);
            }

            // numLikes = db.updateLikes(postID);
            Object data = db.selectPost(postID);

            if (numLikes == -1) {
                return gson.toJson(new StructuredResponse("error", "unable to update row " + postID, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });

        // Adding a route for updating the number of dislikes in the Database
        Spark.put("/messages/:post_id/:user_id/dislikes", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            String userID = request.params("user_id");
            int postID = Integer.parseInt(request.params("post_id"));

            int numDislikes = 0;
            int numDeletes = 0;
            
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            //get query to check for status on post_id
            String status = db.checkLikeDislikeStatus(userID, postID);
            
            //then if it is 'liked' do nothing 
            if (status.equals("none")) {
                numDislikes = db.updateDislikes(postID, "up");
                numDeletes = db.insertLikeDislikeStore(userID, postID, "disliked");
            } else if (status.equals("liked")) {
                numDislikes = db.updateDislikes(postID, "up");
                numDislikes = db.updateLikes(postID, "down");
                numDeletes = db.deleteLikeDislikeStore(userID, postID);
                numDislikes = db.insertLikeDislikeStore(userID, postID, "disliked");
            } else if (status.equals("disliked")) {
                numDislikes = db.updateDislikes(postID, "down");
                numDeletes = db.deleteLikeDislikeStore(userID, postID);
            }

            // numLikes = db.updateLikes(postID);
            Object data = db.selectPost(postID);

            if (numDislikes == -1) {
                return gson.toJson(new StructuredResponse("error", "unable to update row " + postID, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });

        // DELETE route for removing a row from the DataStore
        Spark.delete("/messages/:post_id", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            int idx = Integer.parseInt(request.params("post_id"));
            
            // ensure status 200 OK, with a MIME type of JSON
            //getting old fileID
            String fileId = db.getPostFileID(idx);
            if(fileId != ""){
                //Build a new authorized API client service.
                Drive service = GoogleDriveSingleton.getDriveInstance();
                
                service.files().delete(fileId)
                    .execute();
                
                // Print the names and IDs for up to 10 files.
                FileList result = service.files().list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
                
                List<File> driveFiles = result.getFiles();
                
                if (driveFiles == null || driveFiles.isEmpty()) {
                    System.out.println("No files found.");
                } else {
                    System.out.println("Files:");
                    for (File file : driveFiles) {
                    System.out.printf("%s (%s)\n", file.getName(), file.getId());
                    }
                }
            }
            response.status(200);
            response.type("application/json");
            
            // NB: we won't concern ourselves too much with the quality of the
            // message sent on a successful delete
            int result = db.deletePost(idx);
            
            if (result == -1) {
                return gson.toJson(new StructuredResponse("error", "unable to delete row " + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "deleted msg " + idx, null));
            }
        });

        // COMMENTS
        //Get comments from a post
        Spark.get("/comments/:post_id", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            int postID = Integer.parseInt(request.params("post_id"));

            response.status(200);
            response.type("application/json");
            return gson.toJson(new StructuredResponse("ok", null, db.selectAllComments(postID)));
        });

        //Put new comment on a post
        Spark.post("/comments/:post_id/:user_id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int postID = Integer.parseInt(request.params("post_id"));
            String userID = request.params("user_id");

            CommentRequest req = gson.fromJson(request.body(), CommentRequest.class);

            if(req.fileName != null){
                // Build a new authorized API client service.
                Drive service = GoogleDriveSingleton.getDriveInstance();  
                
                //File to have decoded bytes written to
                java.io.File decodedFile = new java.io.File(req.fileName);
                decodedFile.createNewFile();

                //Translating web content to a byte array
                byte[] bytes = DatatypeConverter.parseBase64Binary((req.file).substring((req.file).indexOf(",") + 1));
                
                OutputStream os = new FileOutputStream(decodedFile);
                os.write(bytes);
                os.close();
            
                //This is the upload to drive process
                File fileMetadata = new File();
                fileMetadata.setName(req.fileName);
                
                //sending the file to drive
                FileContent mediaContent = new FileContent(req.fileContent, decodedFile);
                File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .setFields("webViewLink")
                    .execute();
                
                //get and set link and Id for drive file
                String file_Id = file.getId();
                String fileLink = file.getWebViewLink();
                
                // Print the names and IDs for up to 10 files.
                FileList result = service.files().list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
                List<File> driveFiles = result.getFiles();
                if (driveFiles == null || driveFiles.isEmpty()) {
                    System.out.println("No files found.");
                } else {
                    System.out.println("Files:");
                    for (File driveFile : driveFiles) {
                    System.out.printf("%s (%s)\n", driveFile.getName(), driveFile.getId());
                    }
                }
                
                //postgres storage
                int newId = db.insertComment(userID, postID, req.mComment, req.mLink, file_Id, req.file, req.fileName, fileLink);
                //create a new entry with the file link and Id
                if(file_Id == null){
                    return gson.toJson(new StructuredResponse("error", "error performing comment file insertion", null));
                }
                else{
                    return gson.toJson(new StructuredResponse("comment file insert success", null, null));
                }
            }
            else{
                // ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");

                int result = db.insertComment(userID, postID, req.mComment, req.mLink, null, req.file, req.fileName, null);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to insert comment for postID:" + postID, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "" + result, null));
                }
            }
        });

        //Update a comment on a post
        Spark.put("/comments/:post_id/:comment_id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int postID = Integer.parseInt(request.params("post_id"));
            int commentID = Integer.parseInt(request.params("comment_id"));

            CommentRequest req = gson.fromJson(request.body(), CommentRequest.class);
            //getting old fileID
            String fileId = db.getCommentFileID(postID, commentID);
            if(req.file != null){
                
                //Get Drive object
                Drive service = GoogleDriveSingleton.getDriveInstance();

                //File to have decoded bytes written to
                java.io.File decodedFile = new java.io.File(req.fileName);
                decodedFile.createNewFile();
                //Translating web content to a byte array
                byte[] bytes = DatatypeConverter.parseBase64Binary((req.file).substring((req.file).indexOf(",") + 1));
                
                OutputStream os = new FileOutputStream(decodedFile);
                os.write(bytes);
                os.close();

                //Creating drive file to replace old
                File fileMetadata = new File();
                fileMetadata.setName(req.fileName);

                //updating the file
                File newFile = service.files().update(fileId, fileMetadata)
                    .setFields("id")
                    .execute();

                //getting new file Id
                String new_file_Id = newFile.getId();
                
                // ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                
                int result = db.updateComment(postID, commentID, req.mComment, req.mLink, new_file_Id, req.file, req.fileName);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update row " + postID, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "updated comment file successfully", result));
                }
            }

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            int result = db.updateComment(postID, commentID, req.mComment, req.mLink, null, req.file, req.fileName);

            if (result == -1) {
                return gson.toJson(new StructuredResponse("error", "unable to update row " + postID, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, result));
            }
        });

        //USERS
        //Getting user profile info
        Spark.get("/user/:user_id", (request, response) -> {
            String userID = request.params("user_id");
            
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            Object data = db.getUser(userID);
            if (data == null) {
                return gson.toJson(new StructuredResponse("error", userID + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });

        //Editing comment on user profile
        Spark.put("/user/:user_id", (request, response) -> {
            String userID = request.params(("user_id"));
            
            // String newUserComment = gson.fromJson(request.body(), String.class);
            String newUserComment = request.body();

            response.status(200);
            response.type("application/json");
            int result = db.updateUserComment(userID, newUserComment);

            if (result == -1) {
                return gson.toJson(new StructuredResponse("error", "unable to update row userID:" + userID, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, result));
            }
        });

        // AUTHENTICATION
        Spark.post("/login", (request, response) -> {

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(htrans, jfac)
                .setAudience(Collections
                .singletonList("649625495044-a7b17r9ktheh82dgbflb5f18a9k34dea.apps.googleusercontent.com"))
                .build();

            // (Receive idTokenString by HTTPS POST)
            String idTokenString = gson.fromJson(request.body(), String.class);

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Check of domain is lehigh.edu
                String hostedDomain = payload.getHostedDomain();
                System.out.println(hostedDomain);
                if (!hostedDomain.equals("lehigh.edu")) {
                    return gson.toJson(new StructuredResponse("error", "invalid domain", null));
                }

                // Get profile information from payload
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String imageURL = (String) payload.get("picture");
                
                // Get userID
                String userId = payload.getSubject();

                //Check if user exists in DB. If they don't then enter user info into DB
                if (!db.checkIfUserExists(userId)) {
                    db.insertNewUser(userId, name, email, imageURL);
                }

                // Generate new session ID
                String newSessionID = db.generateSessionID();

                String sessionID = db.loginUser(userId, newSessionID);
                return gson.toJson(new StructuredResponse("ok", null, newSessionID));

            } else {
                return gson.toJson(new StructuredResponse("error", "error during login", null));
            }

        });

        //Logout
        //TODO: Fix error where it becomes OPTIONS request instead of DELETE
        Spark.delete("/logout/:session_id", (request, response) -> {
            String sessionID = request.params("session_id");
            System.out.println(sessionID);

            response.status(200);
            response.type("application/json");

            if (db.checkLogin(sessionID)) {
                db.logoutUser(sessionID);
                return gson.toJson(new StructuredResponse("ok", null, sessionID));
            } else {
                return gson.toJson(new StructuredResponse("error", "error while logging out", null));
            }
        });
    }

    /**
     * Get an integer environment varible if it exists, and otherwise return the
     * default value.
     * @envar The name of the environment variable to get.
     * @defaultVal The integer value to use as the default if envar isn't found
     * @returns The best answer we could come up with for a value for envar
     */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }

    /**
     * Set up CORS headers for the OPTIONS verb, and for every response that the
     * server sends. This only needs to be called once.
     * @param origin  The server that is allowed to send requests to this server
     * @param methods The allowed HTTP verbs from the above origin
     * @param headers The headers that can be sent with a request from the above
     *                origin
     */
    private static void enableCORS(String origin, String methods, String headers) {
        // Create an OPTIONS route that reports the allowed CORS headers and methods
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // 'before' is a decorator, which will run before any
        // get/post/put/delete. In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
}