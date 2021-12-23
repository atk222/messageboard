
package edu.lehigh.cse216.tbt220.backend;

/**
 * RowData is like a struct in C: we use it to hold data, and we allow direct
 * access to its fields. In the context of this Database, RowData represents the
 * data we'd see in a row.
 * 
 * We make RowData a static class of Database because we don't really want to
 * encourage users to think of RowData as being anything other than an abstract
 * representation of a row of the database. RowData and the Database are tightly
 * coupled: if one changes, the other should too.
 */
public class RowData {
    /**
     * The ID of this row of the database
     */
    int mId;
    /**
     * The ID of user that made the post
     */
    String mUserId;
    /**
     * Username
     */
    String mUsername;
    /**
     * The subject stored in this row
     */
    String mSubject;
    /**
     * The message stored in this row
     */
    String mMessage;

    // The number of likes the message has
    int mLikes;

    // the number of dislikes the message has
    int mDislikes;

    // Image url
    String mImageURL;

    // user Link
    String mLink;

    // file Id
    String mFileId;

    // fileString
    String mFileString;

    // fileName
    String mFileName;

    // link to file
    String mFileLink;

    String mLocation;

    double[] mCoords;

    /**
     * Construct a RowData object by providing values for its fields
     */
    public RowData(int post_id, String user_id, String username, String subject, String message, int likes,
            int dislikes, String imageURL, String link, String fileID, String fileString, String fileName,
            String fileLink, String location, double[] coords) {
        mId = post_id;
        mUserId = user_id;
        mUsername = username;
        mSubject = subject;
        mMessage = message;
        mLikes = likes;
        mDislikes = dislikes;
        mImageURL = imageURL;
        mLink = link;
        mFileId = fileID;
        mFileString = fileString;
        mFileName = fileName;
        mFileLink = fileLink;
        mLocation = location;
        mCoords = coords;
    }

    public RowData(RowData row) {
        mId = row.mId;
        mUserId = row.mUserId;
        mUsername = row.mUsername;
        mSubject = row.mSubject;
        mMessage = row.mMessage;
        mLikes = row.mLikes;
        mDislikes = row.mDislikes;
        mImageURL = row.mImageURL;
        mLink = row.mLink;
        mFileId = row.mFileId;
        mFileString = row.mFileString;
        mFileName = row.mFileName;
        mFileLink = row.mFileLink;
        mLocation = row.mLocation;
        mCoords = row.mCoords;
    }
}