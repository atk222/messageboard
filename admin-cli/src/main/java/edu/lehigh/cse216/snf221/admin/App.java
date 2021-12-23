package edu.lehigh.cse216.snf221.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * App is our basic admin app. For now, it is a demonstration of the six key
 * operations on a database: connect, insert, update, query, delete, disconnect
 */
public class App {

    /**
     * Print the menu for our program
     */
    static void menu() {
        System.out.println("Main Menu");
        System.out.println("  [T] Create all tables");
        System.out.println("  [D] Drop all tables");

        // SELECT
        System.out.println("  [P] Query for a specific row from Post");
        System.out.println("  [U] Query for a specific row from Users");
        System.out.println("  [C] Query for a specific row from Comment");

        // SELECT ALL
        System.out.println("  [*] Query for all rows for posts");
        System.out.println("  [@] Query for all rows for users");
        System.out.println("  [#] Query for all rows for comments");
        System.out.println("  [L] List documents original owners and most recent activtiy (date posted)");

        // INSERT
        System.out.println("  [1] Insert a new row into posts");
        System.out.println("  [2] Insert a new row into users");
        System.out.println("  [3] Insert a new row into comments");
        System.out.println("  [4] Insert a new row into session_store");
        System.out.println("  [5] Insert a new row into likeDislike table");

        // DELETE
        System.out.println("  [6] Delete a row from posts");
        System.out.println("  [7] Delete a row from users");
        System.out.println("  [8] Delete a row from comments");
        System.out.println("  [9] Delete a row from session_store");
        System.out.println("  [0] Delete a row from likeDislike table");
        System.out.println("  [R] Delete most recently posted file");

        // UPDATE
        System.out.println("  [x] Update number of likes for message using its id in post");
        System.out.println("  [z] Update number of dislikes for message using its id in post");
        System.out.println("  [~] Update a row in post");
        System.out.println("  [$] Update a user comment in user");
        System.out.println("  [&] Update a row in comment");

        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");

    }

    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * 
     * @return The character corresponding to the chosen menu option
     */
    static char prompt(BufferedReader in) {
        // The valid actions:
        String actions = "TDPUC*@#1234567890xz~$&qLR?";

        // We repeat until a valid single-character option is selected
        while (true) {
            System.out.println("[" + actions + "] :> ");

            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (action.length() != 1) {
                continue;
            }
            if (actions.contains(action)) {
                return action.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Ask the user to enter a String message
     * 
     * @param in      A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided. May be "".
     */
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.println(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * 
     * @param in      A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided. On error, it will be -1
     */
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.println(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Incorrect input: Need an integer");
            // e.printStackTrace();
        }
        return i;
    }



    /**
     * The main routine runs a loop that gets a request from the user and processes
     * it
     * 
     * @param argv Command-line options. Ignored by this program.
     */
    public static void main(String[] argv) {
        // get the Postgres configuration from the environment
        // Map<String, String> env = System.getenv();
        // String ip = "127.0.0.1";// env.get("POSTGRES_IP");
        // String port = "5432";// env.get("POSTGRES_PORT");
        // String user = "sam";// env.get("POSTGRES_USER");
        // String pass = "pass";// env.get("POSTGRES_PASS");

        // For new heroku (cse216project)
        final String db_url = "postgres://tbjjhpzywmqvqt:5f868f4e412fe0fab2b9eefdcf0e86c6f8f5d883cf4317dd2611c975df0dcc54@ec2-34-233-186-251.compute-1.amazonaws.com:5432/dc7qo15d00p8ju";

        // For old heroku (cse216-phase1)
        // final String db_url = "postgres://sstiamnqgrvjkh:ac8c7de5eb5131b2cb68547e5580e033f9ce0105eb22ab66df654f6e47c6f61f@ec2-184-72-236-57.compute-1.amazonaws.com:5432/d6vvd2k8onovfv";
        // final String memcachier_servers = "mc4.c1.nyc1.do.memcachier.com:11211";
        // final String memcachier_username = "8CDBEB";
        // final String memcachier_password = "C5A7E2CBA181D426A6F7E36F24329096";

        //cse216Test
        final String memcachier_servers = "mc5.dev.ec2.memcachier.com:11211";
        final String memcachier_username = "680FBF";
        final String memcachier_password = "A7EF33FF12D15A967A67B1FA054264BF";


        // Get a fully-configured connection to the database, or exit
        // immediately
        Database db = Database.getDatabase(db_url, memcachier_servers, memcachier_username, memcachier_password);
        if (db == null)
            return;

        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            char action = prompt(in);
            if (action == '?') {
                menu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                db.createTables();
            } else if (action == 'D') {
                db.dropTables();
            } else if (action == 'P') {
                // Select one post
                int id = getInt(in, "Enter the post ID");
                if (id == -1)
                    continue;
                Database.RowData res = db.selectPost(id);
                if (res != null) {
                    System.out.println("  [" + res.mId + "]");
                    System.out.println(" --> user_id" + res.mUserId);
                    System.out.println(" --> subject " + res.mSubject);
                    System.out.println(" --> message " + res.mMessage);
                    System.out.println(" --> Likes: " + res.mLikes);
                    System.out.println(" --> Dislikes: " + res.mDislikes);
                    System.out.println(" --> FileID: " + res.mFileId);
                    System.out.println(" ---> Location: "+ res.mLocation);
                    System.out.print("-->Long: "+res.mCoords[0]+"  Lang: "+res.mCoords[1]);
                }
            } else if (action == 'U') {
                // Select one user
                String id = getString(in, "Enter the user ID");
                if (id.equals(""))
                    continue;
                Database.UserProfile res = db.getUser(id);
                if (res != null) {
                    System.out.println("  [" + res.uUserID + "] ");
                    System.out.println(" --> Name" + res.uName);
                    System.out.println(" --> Email " + res.uEmail);
                    System.out.println(" --> Comment " + res.uComment);
                }
            } else if (action == 'C') {
                // Select one comment
                int postID = getInt(in, "Enter the post ID");
                int commentID = getInt(in, "Enter the comment ID");
                if (postID == -1 || commentID == -1)
                    continue;
                Database.Comment res = db.selectComment(postID, commentID);
                if (res != null) {
                    System.out.println("  [" + res.mCommentID + "] ");
                    System.out.println(" --> user_id" + res.mUserID);
                    System.out.println(" --> post_id" + res.mPostID);
                    System.out.println(" --> comment_id" + res.mCommentID);
                    System.out.println(" --> username" + res.mUsername);
                    System.out.println(" --> comment" + res.mComment);
                    System.out.println(" --> file_id" + res.mFileID);
                }
            } else if (action == '1') {
                // Insert post
                String user_id = getString(in, "Enter the user_id");
                String subject = getString(in, "Enter the subject");
                String message = getString(in, "Enter the message");
                String link = getString(in, "Enter the link");
                String fileID = getString(in, "Enter the file_id");
                String fileName = getString(in, "Enter the fileName");
                String fileString = getString(in, "Enter the fileString");
                String fileLink = getString(in, "Enter the fileLink");
                String location = getString(in, "Enter a dining hall");

                if (subject.equals("") || message.equals("") || user_id.equals(""))
                    continue;
                int res = db.insertPost(user_id, subject, message, link, fileID, fileString,fileLink,fileName,location);
                System.out.println(res + " rows added");
            } else if (action == '2') {
                // Insert new user
                String user_id = getString(in, "Enter the user_id");
                String name = getString(in, "Enter the name");
                String email = getString(in, "Enter the email");
                String imgURL = getString(in, "Enter the imgURL");

                if (user_id.equals("") || name.equals("") || email.equals(""))
                    continue;
                int res = db.insertNewUser(user_id, name, email, imgURL);
                System.out.println(res + " rows added");
            } else if (action == '3') {
                // Insert comment
                String user_id = getString(in, "Enter the user_id");
                int post_id = getInt(in, "Enter the post_id");
                String comment = getString(in, "Enter the comment");
                String link = getString(in, "Enter the link");
                String file_id = getString(in, "Enter the file_id");
                String fileName = getString(in, "Enter the fileName");
                String fileString = getString(in, "Enter the fileString");

                if (user_id.equals("") || comment.equals("") || post_id == -1)
                    continue;
                int res = db.insertComment(user_id, post_id, comment, link, file_id, fileString, fileName);
                System.out.println(res + " rows added");
            } else if (action == '4') {
                // Insert session_store
                String user_id = getString(in, "Enter the user_id");
                String session_id = getString(in, "Enter the session_id");

                if (user_id.equals("") || session_id.equals(""))
                    continue;
                String res = db.loginUser(user_id, session_id);
                System.out.println("The session inserted is " + res);
            } else if (action == '5') {
                // Insert likeDislike table
                String user_id = getString(in, "Enter the user_id");
                int post_id = getInt(in, "Enter the post_id");
                String status = getString(in, "Enter the status");

                if (user_id.equals("") || status.equals("") || post_id == -1)
                    continue;
                int res = db.insertLikeDislikeStore(user_id, post_id, status);
                System.out.println(res + " rows added");
            } else if (action == '*') {
                // Get all posts
                ArrayList<Database.RowData> res = db.selectAllPosts();
                if (res == null)
                    continue;
                System.out.println("  Current Post table Contents");
                System.out.println("  -------------------------");
                for (Database.RowData rd : res) {
                    System.out.println("  [" + rd.mId + "] " + rd.mUserId + " subject " + rd.mSubject + " message: "
                            + rd.mMessage + " likes: " + rd.mLikes + " dislikes: " + rd.mDislikes);
                }
            } else if (action == '@') {
                // Get all users
                ArrayList<Database.UserProfile> res = db.getAllUsers();
                if (res == null)
                    continue;
                System.out.println("  Current Users table Contents");
                System.out.println("  -------------------------");
                for (Database.UserProfile rd : res) {
                    System.out.println("  [" + rd.uUserID + "] " + " name " + rd.uName + " email " + rd.uEmail
                            + " imgURL: " + rd.uImageURL + " Comments: " + rd.uComment);
                }
            } else if (action == '#') {
                // Get all comments
                ArrayList<Database.Comment> res = db.selectAllComments();
                if (res == null)
                    continue;
                System.out.println("  Current Comments table Contents");
                System.out.println("  -------------------------");
                for (Database.Comment rd : res) {
                    System.out.println("  [" + rd.mCommentID + "] " + rd.mUserID + " post_id: " + rd.mPostID
                            + " Comments: " + rd.mComment);
                }
            } else if (action == '6') {
                // Delete a row from posts
                //This also deletes the file from the cache associated with the post
                int id = getInt(in, "Enter the post ID");
                if (id == -1)
                    continue;
                int res = db.deletePost(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '7') {
                // Delete row from users
                String id = getString(in, "Enter the user ID");
                if (id.equals(""))
                    continue;
                int res = db.deleteUser(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '8') {
                //Delete a row from comments
                //This also deletes the file from the cache associated with the comment
                
                int postID = getInt(in, "Enter the post ID");
                int commentID = getInt(in, "Enter the comment ID");

                if (postID == -1 || commentID == -1)
                    continue;
                int res = db.deleteComment(postID, commentID);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '9') {
                // Delete session store
                String id = getString(in, "Enter the session ID");
                if (id.equals(""))
                    continue;
                db.logoutUser(id);
                System.out.println("Session_id " + id + " deleted");
            } else if (action == '0') {
                // Delete row from likeDislike
                String uid = getString(in, "Enter the user ID");
                int pid = getInt(in, "Enter the post ID");
                if (uid.equals("") || pid == -1)
                    continue;
                int res = db.deleteLikeDislikeStore(uid, pid);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '~') {
                // Update row in post
                int postID = getInt(in, "Enter the post ID");
                if (postID == -1)
                    continue;
                String subject = getString(in, "Enter the new subject");
                String message = getString(in, "Enter new message");
                String link = getString(in, "Enter new link");
                String fileID = getString(in, "Enter new fileID");
                String fileString = getString(in, "Enter new fileString");
                String fileName = getString(in, "Enter new fileName");
                String location = getString(in, "Enter a dining hall");

                int res = db.updatePost(postID, subject, message, link, fileID, fileString,fileName,location);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            } else if (action == '$') {
                // Update user comment
                String id = getString(in, "Enter the user ID ");
                // id += "%";
                System.out.println(id);
                if (id.equals(""))
                    continue;
                String comment = getString(in, "Enter the new comment");
                int res = db.updateUserComment(id, comment);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            } else if (action == '&') {
                // Update row for comment
                int comment_id = getInt(in, "Enter the comment_id");
                int post_id = getInt(in, "Enter the post_id");
                if (comment_id == -1 || post_id == -1)
                    continue;
                String comment = getString(in, "Enter the new comment");
                String link = getString(in, "Enter the new link");
                String fileID = getString(in, "Enter the new file_id");
                String fileString = getString(in, "Enter the new fileString");
                String fileName = getString(in, "Enter the new fileName");

                int res = db.updateComment(post_id, comment_id, comment, link, fileID, fileString, fileName);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            } else if (action == 'x') {
                // Update number of likes for message
                int post_id = getInt(in, "Enter the post_id");
                String likeAction = getString(in, "Enter the action (\"up\" or \"down\")");

                if (post_id == -1)
                    continue;
                if (!(likeAction.equals("up") || likeAction.equals("down")))
                    continue;
                int res = db.updateLikes(post_id, likeAction);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            } else if (action == 'z') {
                // Update the number of dislikes for a post
                int post_id = getInt(in, "Enter the post_id");
                String dislikeAction = getString(in, "Enter the action (\"up\" or \"down\")");

                if (post_id == -1)
                    continue;
                if (!(dislikeAction.equals("up") || dislikeAction.equals("down")))
                    continue;
                if (post_id == -1)
                    continue;
                int res = db.updateDislikes(post_id, dislikeAction);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            } else if (action == 'L') {
                String listAction = getString(in, "List documents from \"posts\" or \"comments\"?");

                if (listAction.equals("posts")) {
                    ArrayList<Database.PostDocuments> res = db.getPostDocuments();
                    if (res == null) continue;

                    System.out.println("  List of Documents for posts");
                    System.out.println("  -------------------------");
                    System.out.printf("%-19s%-19s%-19s%-19s%-19s\n", "post_id", "name","file_id", "file_name","date_posted");

                    for (Database.PostDocuments rd : res) {
                        System.out.printf("%-19s%-19s%-19s%-19s%-19s\n", rd.pPostID, rd.pUsername, rd.pFileID, rd.pFilename, rd.pDatePosted);
                    }
                } else if (listAction.equals("comments")) {
                    // List documents original owners and most recent activtiy (date posted)
                    ArrayList<Database.CommentDocuments> res = db.getCommentDocuments();
                    if (res == null) continue;

                    System.out.println("  List of Documents for comments");
                    System.out.println("  -------------------------");
                    System.out.printf("%-19s%-19s%-19s%-19s%-19s%-19s\n", "post_id", "comment_id","name", "file_id", "file_name","date_posted");

                    for (Database.CommentDocuments rd : res) {
                        System.out.printf("%-19s%-19s%-19s%-19s%-19s%-19s\n", rd.cPostID, rd.cCommentID, rd.cUsername, rd.cFileID, rd.cFilename, rd.cDatePosted);
                    }
                } else if (listAction.equals("all")) {
                    ArrayList<Database.PostDocuments> res = db.getAllDocuments();

                    if (res == null) continue;
                   
                    System.out.println("  List of All Documents");
                    System.out.println("  -------------------------");
                    System.out.printf("%-19s%-19s%-19s%-19s\n", "file_id", "filename","name","date_posted");

                    for (Database.PostDocuments rd : res) {
                        System.out.printf("%-19s%-19s%-19s%-19s\n", rd.pFileID, rd.pFilename, rd.pUsername, rd.pDatePosted);
                    }

                } else {
                    System.out.println("Invalid option");
                }
            } else if (action == 'R') {
                // Remove uploaded files from both memcachier and tables
                // Can also remove most recently uploaded by listing the documents
                // and deleting the first entry as the listing is in descending order
                // of date posted

                //Get file id and remove from memcachier
                System.out.println("Removing most recent content from cache...");
                String fileID = db.getMostRecentFileID();

                if (fileID != null) {
                    db.deleteMemcachier(fileID);
                    System.out.println("Deleted successfully");
                } else {
                    System.out.println("Content does not exist in cache");
                }
            }
        }
        // Always remember to disconnect from the database and memcachier when the program
        // exits
        db.disconnect();
    }
}