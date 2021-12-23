package edu.lehigh.cse216.tbt220.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.security.SecureRandom;
import java.util.Base64.Encoder;
import java.util.Base64;

//Memcachier
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;
import java.lang.InterruptedException;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class Database {

    //USER PROFILES
    
    //Creste users table
    private PreparedStatement uCreateTable;

    //Drop users table
    private PreparedStatement uDropTable;

    //Get user info
    private PreparedStatement uGetUser;
    
    //Insert new user into users table
    private PreparedStatement uInsertNewUser;
    
    //Delete user from users table
    private PreparedStatement uDeleteUser;
    
    //Update comment for user profile
    private PreparedStatement uUpdateComment;

    //Check if user exists 
    private PreparedStatement uCheckIfUserExists;

    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    //MESSAGES
    /**
     * A prepared statement for getting all data in the database
     */
    private PreparedStatement mSelectAllPosts;

    /**
     * A prepared statement for getting one row from the database
     */
    private PreparedStatement mSelectOnePost;

    /**
     * A prepared statement for deleting a row from the database
     */
    private PreparedStatement mDeleteOnePost;

    /**
     * A prepared statement for inserting into the database
     */
    private PreparedStatement mInsertOnePost;

    /**
     * A prepared statement for updating a single row in the database
     */
    private PreparedStatement mUpdateOnePost;

    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreateTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private PreparedStatement mDropTable;

    /**
     * A prepared statement for updating a like in our database
     */
    private PreparedStatement mUpdateLikeIncrement;
    private PreparedStatement mUpdateLikeDecrement;

    /**
     * A prepared statement for updating a dislike in our database
     */
    private PreparedStatement mUpdateDislikeIncrement;
    private PreparedStatement mUpdateDislikeDecrement;

    /**
     * Get file ID of the post
     */
    private PreparedStatement mGetPostFileID;


    //AUTHENTICATION
    /**
     * Create table for sessionStore
     */
    private PreparedStatement sCreateTable;

    /**
     * Drop table for sessionStore
     */
    private PreparedStatement sDropTable;

    /**
     * Login user
     */
    private PreparedStatement sLoginUser;

    /**
     * Logout user
     */
    private PreparedStatement sLogoutUser;

    /**
     * Check login
     */
    private PreparedStatement sCheckLogin;

    /**
     * Remove old login state?
     */
    private PreparedStatement sRemoveOldLogin;

    //COMMENT SECTION
    private PreparedStatement cCreateTable;
    private PreparedStatement cDropTable;
    private PreparedStatement cInsertOne;
    private PreparedStatement cSelectOne;
    private PreparedStatement cDeleteOne;
    private PreparedStatement cSelectAllFromOne;
    private PreparedStatement cSelectAll;
    private PreparedStatement cUpdateOne;
    private PreparedStatement cGetFileID;

    //LIKE DISLIKE STORE FIGURE OUT LOGIC
    private PreparedStatement ldCreateTable;
    private PreparedStatement ldDropTable;
    private PreparedStatement ldInsert;
    private PreparedStatement ldDelete;
    private PreparedStatement ldGetStatus;
    
    //LOCATIONS TABLE
    private PreparedStatement locationsCreateTable;
    private PreparedStatement locationsDropTable;
    private PreparedStatement locationsInsert;
    private PreparedStatement locationsDelete;
    private PreparedStatement locationsGetCoords;

    private MemcachedClient memCachier;

    //Locations
    private String[] locations = {
        "Rathbone Hall",
        "Lower/Upper Court",
        "Common Grounds Cafe",
        "FML The Grind Cafe",
        "Williams Global Cafe",
        "Lucy's Cafe"
    };

    //Ask for new coords
    //Each index in locations correspond
    //to each index in coords
    private double[][] coords = {
        {40.606927, -75.372896},
        {40.606135, -75.378465},
        {40.608203, -75.373904},
        {40.608877, -75.377935},
        {40.606741, -75.375591},
        {40.606630, -75.377047}
    };
    
    /**
     * The Database constructor is private: we only create Database objects 
     * through the getDatabase() method.
     */
    private Database() {
    }

    /**
     * Get a fully-configured connection to the database
     * 
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String db_url) {
        // Create an un-configured Database object
        Database db = new Database();
        //MemCachier
        List<InetSocketAddress> servers = AddrUtil.getAddresses(System.getenv("MEMCACHIER_SERVERS").replace(",", " "));
        AuthInfo authInfo = AuthInfo.plain(System.getenv("MEMCACHIER_USERNAME"), System.getenv("MEMCACHIER_PASSWORD"));
        
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);
        // Configure SASL auth for each server
        for(InetSocketAddress server : servers) {
            builder.addAuthInfo(server, authInfo);
        }
        // Use binary protocol
        builder.setCommandFactory(new BinaryCommandFactory());
        // Connection timeout in milliseconds (default: )
        builder.setConnectTimeout(1000);
        // Reconnect to servers (default: true)
        builder.setEnableHealSession(true);
        // Delay until reconnect attempt in milliseconds (default: 2000)
        builder.setHealSessionInterval(2000);

        try{
            db.memCachier = builder.build();
        } catch (IOException ioe){
            System.err.println("Couldn't create a connection to Memcachier: "+ ioe.getMessage());
        }
        System.out.println("Memcachier connected successfully");
        // Give the Database object a connection, fail if we cannot get one
        try {
            Class.forName("org.postgresql.Driver");
            URI dbUri = new URI(db_url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Unable to find postgresql driver");
            return null;
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
            return null;
        }

        // Attempt to create all of our prepared statements.  If any of these 
        // fail, the whole getDatabase() call should fail
        try {
            // NB: we can easily get ourselves in trouble here by typing the
            //     SQL incorrectly.  We really should have things like "tblData"
            //     as constants, and then build the strings for the statements
            //     from those constants.

            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table 
            // creation/deletion, so multiple executions will cause an exception
            
            //AUTHENTICATION
            db.sCreateTable = db.mConnection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS sessionStore (" +
                    "user_id VARCHAR(50)," +
                    "session_id VARCHAR(30))"
            );
            //Session Store
            db.sDropTable = db.mConnection.prepareStatement("DROP TABLE sessionStore");
            db.sRemoveOldLogin = db.mConnection.prepareStatement("DELETE FROM sessionStore WHERE user_id = ?");
            db.sLoginUser = db.mConnection.prepareStatement("INSERT INTO sessionStore VALUES(?,?)");
            db.sLogoutUser = db.mConnection.prepareStatement("DELETE FROM sessionStore WHERE session_id = ?");
            
            //https://stackoverflow.com/questions/46061989/jdbc-check-if-entry-exists
            db.sCheckLogin = db.mConnection.prepareStatement("SELECT session_id FROM sessionStore WHERE user_id = ?");

            //USER PROFILES
            db.uCreateTable = db.mConnection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS users (" +
                "user_id VARCHAR(50) PRIMARY KEY," +
                "name VARCHAR(100)," +
                "email VARCHAR(100)," +
                "imageURL VARCHAR(500)," +
                "comment VARCHAR(500))"
            );

            db.uDropTable = db.mConnection.prepareStatement("DROP TABLE users");
            //'Your comment' = default user comment on profile page
            db.uGetUser = db.mConnection.prepareStatement("SELECT * FROM users WHERE user_id = ?");
            db.uInsertNewUser = db.mConnection.prepareStatement("INSERT INTO users VALUES(?,?,?,?,'Your comment')");
            db.uDeleteUser = db.mConnection.prepareStatement("DELETE FROM users WHERE user_id = ?");
            db.uUpdateComment = db.mConnection.prepareStatement("UPDATE users SET comment = ? WHERE user_id = ?");
            db.uCheckIfUserExists = db.mConnection.prepareStatement("SELECT user_id FROM users WHERE user_id = ?");

            //POSTS
            db.mCreateTable = db.mConnection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS posts (" +
                "post_id SERIAL PRIMARY KEY," + 
                "user_id VARCHAR(50)," +
                "subject VARCHAR(50) NOT NULL," +
                "message VARCHAR(500) NOT NULL," +
                "likes INT," +
                "dislikes INT," +
                "date_posted TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "link VARCHAR(100)," +                
                "file_id VARCHAR(100)," +
                "file_name VARCHAR(50)," +
                "file_link VARCHAR(100)," +
                "location VARCHAR(100)," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)");
            db.mDropTable = db.mConnection.prepareStatement("DROP TABLE posts");
            
            // Standard CRUD operations
            db.mGetPostFileID = db.mConnection.prepareStatement("SELECT file_id FROM posts WHERE post_id = ?");
            db.mDeleteOnePost = db.mConnection.prepareStatement("DELETE FROM posts WHERE post_id = ?");
            db.mInsertOnePost = db.mConnection.prepareStatement("INSERT INTO posts VALUES (default,?, ?, ?, 0, 0, default, ?, ?, ?, ?, ?)");
            db.mSelectAllPosts = db.mConnection.prepareStatement("SELECT * FROM posts JOIN users ON posts.user_id = users.user_id JOIN locations ON locations.location = posts.location ORDER BY post_id ASC");
            db.mSelectOnePost = db.mConnection.prepareStatement("SELECT * FROM posts JOIN users ON posts.user_id = users.user_id JOIN locations ON locations.location = posts.location WHERE post_id = ?");
            db.mUpdateOnePost = db.mConnection.prepareStatement("UPDATE posts "+ 
            "SET subject = ?, message = ?, link = ?, file_id = ?, file_name = ? date_posted = CURRENT_TIMESTAMP  WHERE post_id = ?");
        
            //For incrementing likes/dislikes
            db.mUpdateLikeIncrement = db.mConnection.prepareStatement("UPDATE posts SET likes= likes + 1 WHERE post_id = ?");
            db.mUpdateDislikeIncrement = db.mConnection.prepareStatement("UPDATE posts SET dislikes = dislikes + 1 WHERE post_id = ?");

            //For decrementing likes/dislikes
            db.mUpdateLikeDecrement = db.mConnection.prepareStatement("UPDATE posts SET likes= likes - 1 WHERE post_id = ?");
            db.mUpdateDislikeDecrement = db.mConnection.prepareStatement("UPDATE posts SET dislikes = dislikes - 1 WHERE post_id = ?");

            //COMMENTS
            db.cCreateTable = db.mConnection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS comments (" +
                "comment_id SERIAL PRIMARY KEY," +
                "user_id VARCHAR(50)," +
                "post_id INTEGER," +
                "comment VARCHAR(500)," +
                "date_posted TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "link VARCHAR(100)," +
                "file_id VARCHAR(100)," +
                "file_name VARCHAR(50)," +
                "file_link VARCHAR(100)," +
                "FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)"
            );

            db.cDropTable = db.mConnection.prepareStatement("DROP TABLE comments");
            db.cGetFileID = db.mConnection.prepareStatement("SELECT file_id FROM comments WHERE post_id = ? AND comment_id = ?");
            db.cInsertOne = db.mConnection.prepareStatement("INSERT INTO comments VALUES(default,?,?,?,default,?,?,?,?)");
            db.cSelectOne = db.mConnection.prepareStatement("SELECT * FROM comments,users WHERE comments.user_id = users.user_id AND post_id = ? AND comment_id = ?");
            db.cDeleteOne = db.mConnection.prepareStatement("DELETE FROM comments WHERE post_id = ? AND comment_id = ?");
            db.cSelectAllFromOne = db.mConnection.prepareStatement("SELECT * FROM comments,users WHERE comments.user_id = users.user_id AND post_id = ? ORDER BY comment_id ASC");
            db.cSelectAll = db.mConnection.prepareStatement("SELECT * FROM comments,users WHERE comments.user_id = users.user_id AND post_id = ? ORDER BY comment_id ASC");
            db.cUpdateOne = db.mConnection.prepareStatement("UPDATE comments SET comment = ?, link = ?, file_id = ?, file_name = ?, date_posted = CURRENT_TIMESTAMP WHERE comment_id = ? and post_id = ?");

            //LIKE DISLIKE STORE
            db.ldCreateTable = db.mConnection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS likeDislikeStore (" +
                "user_id VARCHAR(50)," +
                "post_id INTEGER," +
                "status VARCHAR(30)," +
                "FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)"
            );
            db.ldDropTable = db.mConnection.prepareStatement("DROP TABLE likeDislikeStore");
            db.ldInsert = db.mConnection.prepareStatement("INSERT INTO likeDislikeStore VALUES(?,?,?)");
            db.ldDelete = db.mConnection.prepareStatement("DELETE FROM likeDislikeStore WHERE user_id = ? and post_id = ?");
            db.ldGetStatus = db.mConnection.prepareStatement("SELECT status FROM likeDislikeStore WHERE user_id = ? and post_id = ?");

            //LOCATIONS AND COORDINATES
            db.locationsCreateTable = db.mConnection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS locations (" +
                "location VARCHAR(200)," +
                "latitude NUMERIC(12,6)," +
                "longitude NUMERIC(12,6))"
            );
            db.locationsDropTable = db.mConnection.prepareStatement("DROP TABLE locations");
            db.locationsDelete = db.mConnection.prepareStatement("DELETE FROM locations WHERE location = ?");
            db.locationsGetCoords = db.mConnection.prepareStatement("SELECT * FROM locations WHERE location = ?");
            db.locationsInsert = db.mConnection.prepareStatement("INSERT INTO locations VALUES(?,?,?)");

        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
    }

    //AUTHENTICATION
    
    //Check session ID associated with user ID to make sure it matches
    //If they do not match from front-end, then 
    //should make user logout automatically
    boolean checkLogin(String sessionID){
        ResultSet rs = null;
        try {
            sCheckLogin.setString(1, sessionID);

            rs = sCheckLogin.executeQuery();
            
            if (rs.next()) {
                if (rs.getString("session_id") == sessionID){
                    return true;
                } else{
                    return false;
                }
            }
            
        } catch(SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    //Login user
    String loginUser(String userID, String sessionID){
        try {
            //Try removing old login if there is one
            try {
                sRemoveOldLogin.setString(1, userID);
                sRemoveOldLogin.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("No old login found");
                ex.printStackTrace();
            }
            
            sLoginUser.setString(1, userID);
            sLoginUser.setString(2, sessionID);
            sLoginUser.executeUpdate();

            // sGetNewSID.setString(1, userID);
            // ResultSet rs = sGetNewSID.executeQuery();

            // String newSessionID = rs.getString("sessionID");
            return sessionID;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return "";
    }

    //Logsout users
    void logoutUser(String sessionID){
        try {
        sLogoutUser.setString(1,sessionID);
        sLogoutUser.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    //Check if user exists already on database
    boolean checkIfUserExists(String userID) {
        ResultSet rs = null;
        try {
            uCheckIfUserExists.setString(1, userID);
            rs = uCheckIfUserExists.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    //USERS
    
    //Get user information
    UserProfile getUser(String userID) {
        UserProfile res = null;
        try {
            uGetUser.setString(1, userID);
            ResultSet rs = uGetUser.executeQuery();
            if (rs.next()) {
                res = new UserProfile(rs.getString("user_id"), rs.getString("name"), rs.getString("email"), rs.getString("imageURL"), rs.getString("comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    
    //Insert new user into the users table
    int insertNewUser(String userID, String name, String email, String imageURL) {
        int rowUpdate = 0;
        try {
            uInsertNewUser.setString(1, userID);
            uInsertNewUser.setString(2, name);
            uInsertNewUser.setString(3, email);
            uInsertNewUser.setString(4, imageURL);

            rowUpdate += uInsertNewUser.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rowUpdate;
    }

    //Update comment on user profile
    int updateUserComment(String userID, String comment) {
        int rowUpdate = -1;
        try {
            uUpdateComment.setString(1, comment);
            uUpdateComment.setString(2, userID);

            rowUpdate += uUpdateComment.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return rowUpdate;
    }

    //Generates a random string of length 20 to use as session id
    String generateSessionID() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String sessionID = encoder.encodeToString(bytes);
        return sessionID;
    }

    //Memcachier
    //sets Memcachier key/values
    void setMemcachier(String key, int expirationTime, Object value) {
        try {
            memCachier.set(key, expirationTime, value);
        } catch (TimeoutException te) {
            System.err.println("Timeout during set: " + te.getMessage());
        } catch (InterruptedException ie) {
            System.err.println("Interrupt during set: " + ie.getMessage());
        } catch (MemcachedException me) {
            System.err.println("Memcached error during set: " + me.getMessage());
        }
    }

    //Returns fileString from memcachier
    String getMemcachier(String key) {
        try {
            String val = memCachier.get(key);
            //System.out.println(val);
            return val;
        } catch (TimeoutException te) {
            System.err.println("Timeout during get: " + te.getMessage());
        } catch (InterruptedException ie) {
            System.err.println("Interrupt during get: " + ie.getMessage());
        } catch (MemcachedException me) {
            System.err.println("Memcached error during get: " + me.getMessage());
        }
        return null;
    }

    //Delete key-value pair from memcachier
    void deleteMemcachier(String key) {
        try {
            memCachier.delete(key);
        } catch (TimeoutException te) {
            System.err.println("Timeout during get: " + te.getMessage());
        } catch (InterruptedException ie) {
            System.err.println("Interrupt during get: " + ie.getMessage());
        } catch (MemcachedException me) {
            System.err.println("Memcached error during get: " + me.getMessage());
        }
    }

    //POSTS
    /**
     * Insert a row into the database
     * @param subject The subject for this new row
     * @param message The message body for this new row
     * @return The number of rows that were inserted
     */
    int insertPost(String userID, String subject, String message, String link, String fileID, String fileString, String fileName, String fileLink, String location) {
        int count = 0;
        //Storing the file byte stream in the memCache with key=file_Id
        if(fileID != null) setMemcachier(fileID, 3600, fileString);
        try {
            mInsertOnePost.setString(1, userID);
            mInsertOnePost.setString(2, subject);
            mInsertOnePost.setString(3, message);
            mInsertOnePost.setString(4, link);
            mInsertOnePost.setString(5, fileID);
            mInsertOnePost.setString(6, fileName);
            mInsertOnePost.setString(7, fileLink);
            mInsertOnePost.setString(8, location);
            count += mInsertOnePost.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    //Get fileID of post so when deleting a post we can delete from memcachier
    String getPostFileID(int id) {
        String fileID =  null;
        try {
            mGetPostFileID.setInt(1, id);
            ResultSet rs = mGetPostFileID.executeQuery();
            if (rs.next()) {
                fileID = rs.getString("file_id");
            }
            return fileID;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * @return All rows, as an ArrayList
     */
    ArrayList<RowData> selectAllPosts() {
        ArrayList<RowData> res = new ArrayList<RowData>();
        try {
            ResultSet rs = mSelectAllPosts.executeQuery();
            while (rs.next()) {
                System.out.println("Inserting post into list");

                double[] coords = new double[2];
                coords[0] = rs.getDouble("latitude");
                coords[1] = rs.getDouble("longitude");
                String fileID = rs.getString("file_id");

                if(fileID != null){
                    res.add(new RowData(rs.getInt("post_id"), rs.getString("user_id"), rs.getString("name"), rs.getString("subject"),rs.getString("message"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getString("imageURL"), rs.getString("link"), fileID, getMemcachier(fileID), rs.getString("file_name"), rs.getString("file_link"), rs.getString("location"), coords));
                }
                else{
                    res.add(new RowData(rs.getInt("post_id"), rs.getString("user_id"), rs.getString("name"), rs.getString("subject"),rs.getString("message"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getString("imageURL"), rs.getString("link"), fileID, null, rs.getString("file_name"), rs.getString("file_link"), rs.getString("location"), coords));
                }
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     * @param id The id of the row being requested
     * @return The data for the requested row, or null if the ID was invalid
     */
    RowData selectPost(int id) {
        RowData res = null;
        try {
            mSelectOnePost.setInt(1, id);
            ResultSet rs = mSelectOnePost.executeQuery();
            if (rs.next()) {
                double[] coords = new double[2];
                coords[0] = rs.getDouble("latitude");
                coords[1] = rs.getDouble("longitude");
                String fileID = rs.getString("file_id");

                if(fileID != null){
                    res = new RowData(rs.getInt("post_id"), rs.getString("user_id"), rs.getString("name"), rs.getString("subject"),rs.getString("message"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getString("imageURL"), rs.getString("link"), fileID, getMemcachier(fileID), rs.getString("file_name"), rs.getString("file_link"), rs.getString("location"), coords);
                }
                else{
                    res = new RowData(rs.getInt("post_id"), rs.getString("user_id"), rs.getString("name"), rs.getString("subject"),rs.getString("message"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getString("imageURL"), rs.getString("link"), null, null, rs.getString("file_name"), rs.getString("file_link"), rs.getString("location"), coords);
                }            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row by ID
     * @param id The id of the row to delete
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deletePost(int id) {
        int res = -1;
        try {
            //Get fileID of post we are about to delete
            String fileID = getPostFileID(id);
            mDeleteOnePost.setInt(1, id);
            res = mDeleteOnePost.executeUpdate();
            //After successfully deleting post we can delete the fileString from the memcachier
            if (fileID != "") deleteMemcachier(fileID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the message for a row in the database
     * @param id The id of the row to update
     * @param message The new message contents
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int updatePost(int postID, String subject, String message, String link, String file_id, String fileString, String fileName, String location) {
        int res = -1;
        try {
            String oldFileID = getPostFileID(postID);

            mUpdateOnePost.setString(1, subject);
            mUpdateOnePost.setString(2, message);
            mUpdateOnePost.setString(3, link);
            mUpdateOnePost.setString(4, file_id);
            mUpdateOnePost.setString(5, fileName);
            mUpdateOnePost.setInt(6, postID);
            mUpdateOnePost.setString(7, location);

            res = mUpdateOnePost.executeUpdate();
            
            //If update is successful, then insert fileString into memacachier
            if (file_id != "") {
                setMemcachier(file_id, 3600, fileString);
            }
            //Need to null check the file ids so memcachier does not run into errors
            if (oldFileID != "") {
                deleteMemcachier(oldFileID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

     /**
     * Update the number of likes for a given message specified by id in the database
     * @param id The id of the message to update
     * @return The number of likes that were counted.
     */
    int updateLikes(int postID, String action) {
        int numLikes = 0;

        try {
            if (action.equals("up")) {
                mUpdateLikeIncrement.setInt(1, postID);
                numLikes = mUpdateLikeIncrement.executeUpdate();
            } else if (action.equals("down")) {
                mUpdateLikeDecrement.setInt(1, postID);
                numLikes = mUpdateLikeDecrement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numLikes;
    }

     /**
     * Update the number of dislikes for a given message specified by id in the database
     * @param id The id of the message to update
     * @return The number of dislikes that were counted.
     */
    int updateDislikes(int postID, String action) {
        int numDislikes = 0;

        try {
            if (action.equals("up")) {
                mUpdateDislikeIncrement.setInt(1, postID);
                numDislikes = mUpdateDislikeIncrement.executeUpdate();
            } else if (action.equals("down")) {
                mUpdateDislikeDecrement.setInt(1, postID);
                numDislikes = mUpdateDislikeDecrement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numDislikes;
    }

    //COMMENTS

    //Insert comment into table
    int insertComment(String userID, int postID, String comment, String link, String file_id, String fileString, String fileName, String fileLink) {
        int count = -1;
        try {
            cInsertOne.setString(1, userID);
            cInsertOne.setInt(2, postID);
            cInsertOne.setString(3, comment);
            cInsertOne.setString(4, link);
            cInsertOne.setString(5, file_id);
            cInsertOne.setString(6, fileName);
            cInsertOne.setString(7, fileLink);

            count += cInsertOne.executeUpdate();
            //Insert into memcachier after successfully inserting into database
            if(file_id != null) setMemcachier(file_id, 3600, fileString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    
    //Select all comments
    ArrayList<Comment> selectAllComments(int postID) {
        ArrayList<Comment> res = new ArrayList<>();
        try {
            cSelectAll.setInt(1, postID);

            ResultSet rs = cSelectAll.executeQuery();
            while (rs.next()) {
                if(rs.getString("file_id")!=null){
                    res.add(new Comment(rs.getInt("comment_id"), rs.getString("user_id"), rs.getInt("post_id"), rs.getString("comment"), rs.getString("imageURL"), rs.getString("name"), rs.getString("link"), rs.getString("file_id"), getMemcachier(rs.getString("file_id")), rs.getString("file_name"), rs.getString("file_link")));
                }
                else{
                    res.add(new Comment(rs.getInt("comment_id"), rs.getString("user_id"), rs.getInt("post_id"), rs.getString("comment"), rs.getString("imageURL"), rs.getString("name"), rs.getString("link"), rs.getString("file_id"), null, rs.getString("file_name"), rs.getString("file_link")));
                }          }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Get a comment
    Comment selectComment(int postID, int commentID) {
        Comment res = null;
        try {
            cSelectOne.setInt(1, postID);
            cSelectOne.setInt(2, commentID);

            ResultSet rs = cSelectOne.executeQuery();
            if (rs.next()) {
                if(rs.getString("file_id")!=null){
                    res = new Comment(rs.getInt("comment_id"), rs.getString("user_id"), rs.getInt("post_id"), rs.getString("comment"), rs.getString("imageURL"), rs.getString("name"), rs.getString("link"),rs.getString("file_id"), getMemcachier(rs.getString("file_id")), rs.getString("file_name"), rs.getString("file_link"));
                }
                else{
                    res = new Comment(rs.getInt("comment_id"), rs.getString("user_id"), rs.getInt("post_id"), rs.getString("comment"), rs.getString("imageURL"), rs.getString("name"), rs.getString("link"),rs.getString("file_id"), getMemcachier(rs.getString("file_id")), rs.getString("file_name"), rs.getString("file_link"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //Delete a comment
    int deleteComment(int postID, int commentID) {
        int res = -1;
        try {
            //Get file id so we can also delete the file from memcache
            String fileID = getCommentFileID(postID, commentID);

            cDeleteOne.setInt(1, postID);
            cDeleteOne.setInt(2, commentID);

            res = cDeleteOne.executeUpdate();

            //After successfully deleting post we can delete the fileString from the memcachier
            // Don't touch cache if fileID is null
            if (fileID != null) {
                deleteMemcachier(fileID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //Get fileID for a comment
    String getCommentFileID(int postID, int commentID) {
        String fileID = null;
        ResultSet rs = null;
        
        try {
            cGetFileID.setInt(1, postID);
            cGetFileID.setInt(2, commentID);

            rs = cGetFileID.executeQuery();
            if (rs.next()) {
                fileID = rs.getString("file_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return fileID;
    }

    //Update a comment
    int updateComment(int postID, int commentID, String comment, String link, String file_id, String fileString, String fileName) {
        int res = -1;
        try {
            String oldFileID = getCommentFileID(postID, commentID);

            cUpdateOne.setString(1, comment);
            cUpdateOne.setString(2, link);
            cUpdateOne.setString(3, file_id);
            cUpdateOne.setString(4, fileName);
            cUpdateOne.setInt(5, commentID);
            cUpdateOne.setInt(6, postID);
          
            res = cUpdateOne.executeUpdate();
            //If fileID not null then insert into memcachier
            if (file_id != null) {
                setMemcachier(file_id, 3600, fileString);
            }

            //Need to null check old file id so memcachier does not run into errors
            if (oldFileID != null) {
                deleteMemcachier(oldFileID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //LIKE AND DISLIKE STORE

    //Check whether user liked/disliked or none for a post
    String checkLikeDislikeStatus(String userID, int postID) {
        String status = "none";
        ResultSet rs = null;
        try {
            ldGetStatus.setString(1, userID);
            ldGetStatus.setInt(2, postID);
            rs = ldGetStatus.executeQuery();

            //If status exists then user has liked or disliked the post already
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }
    
    //Insert into like dislike store if user has not liked/disliked a post yet
    int insertLikeDislikeStore(String userID, int postID, String status) {
        int res = -1;
        try {
            ldInsert.setString(1, userID);
            ldInsert.setInt(2, postID);
            ldInsert.setString(3, status);
          
            res = ldInsert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //Delete row from likeDislike table
    int deleteLikeDislikeStore(String userID, int postID) {
        int res = -1;
        try {
            ldDelete.setString(1, userID);
            ldDelete.setInt(2, postID);
          
            res = ldDelete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //LOCATIONS

    //Load locations and coordinates into database
    int loadLocations() {
        //double[i][0] latitude
        //double[i][1] longitude
        int res = 0;

        for (int i = 0; i < locations.length; i++) {
            try {
                locationsInsert.setString(1, this.locations[i]);
                locationsInsert.setDouble(2, this.coords[i][0]);
                locationsInsert.setDouble(3, this.coords[i][1]);

                res += locationsInsert.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    //Get coordinates for dining location
    double[] getCoords(String location) {
        double[] coords = new double[2];
        try {
            locationsGetCoords.setString(1, location);

            ResultSet res = locationsGetCoords.executeQuery();

            if (res.next()) {
                coords[0] = res.getDouble("latitude");
                coords[1] = res.getDouble("longitude");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coords;
    }

    //CREATE AND DROP TABLES
    /**
     * Create tables. If it already exists, this will print an error
     */
    void createTables() {
        try {
            //Need to create tables in this order to satisfy key constraints
            //Create users table
            uCreateTable.execute();
            //Create posts tables
            mCreateTable.execute();
            //Create sessionStore table
            sCreateTable.execute();
            //Create comments table
            cCreateTable.execute();
            //Create likeDislikeStore table
            ldCreateTable.execute();
            //Create locations table
            locationsCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove tables from the database. If it does not exist, this will print
     * an error.
     */
    void dropTables() {
        try {
            //Need to drop tables in this order to not break key contraints
            //Drop likeDislikeStore table
            ldDropTable.execute();
            //Drop comments table
            cDropTable.execute();
            //Drop sessionStore table
            sDropTable.execute();
            //Drop posts table
            mDropTable.execute();
            //Drop users table
            uDropTable.execute();
            //Drop locations table
            locationsDropTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //CLOSE CONNECTION
    /**
     * Close the current connection to the database, if one exists.
     * NB: The connection will always be null after this call, even if an 
     *     error occurred during the closing operation.
     * @return True if the connection was cleanly closed, false otherwise
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        try {
            //close memcached client
            memCachier.shutdown();
        } catch (IOException e) {
            System.err.println("Shutdown MemcachedClient fail");
        }
        mConnection = null;
        return true;
    }
}