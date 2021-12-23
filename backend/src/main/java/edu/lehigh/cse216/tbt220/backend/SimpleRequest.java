package edu.lehigh.cse216.tbt220.backend;

/**
 * SimpleRequest provides a format for clients to present title and message 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class SimpleRequest {
    public String mUserID;
    /**
     * The title being provided by the client.
     */
    public String mTitle;

    /**
     * The message being provided by the client.
     */
    public String mMessage;

    public int mLikes;

    public int mDislikes;

    public String mLink;

    public String mFileLink;

    public String mFileName;

    public String mFileContent;

    public String mFile;
    
    public String mLocation;
}