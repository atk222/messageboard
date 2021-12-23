package edu.lehigh.cse216.tbt220.backend;

public class UserProfile {
    String uUserID;
    String uName;
    String uEmail;
    String uImageURL;
    String uComment;

    public UserProfile(String userID, String name, String email, String imageURL, String comment) {
        uUserID = userID;
        uName = name;
        uEmail = email;
        uImageURL = imageURL;
        uComment = comment;
    }

    public UserProfile(UserProfile user) {
        uUserID = user.uUserID;
        uName = user.uName;
        uEmail = user.uEmail;
        uImageURL = user.uImageURL;
        uComment = user.uComment;
    }
}