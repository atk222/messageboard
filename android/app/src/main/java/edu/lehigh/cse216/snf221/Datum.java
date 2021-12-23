package edu.lehigh.cse216.snf221;

class Datum {
    /**
     * id of the message
     */
    int mId;

    /**
     * getting the userId of the person that posted the message
     */
    String mUserId;

    /**
     * getting the username
     */
    String mUsername;

    /**
     * The title of the message
     * **mTitle refers to mSubject in database**
     */

    String mTitle;

    /**
     * The message
     */
    String mMessage;

    /**
     * Number of likes
     */
    int mLikes;

    /**
     * Number of dislikes
     */
    int mDislikes;

    String mImageURL;   //changed to mImageUrl

    String mLink;

    String mFileId;

    String mFileString;

    String mFileName;

    String mFileLink;

    String mLocation;

    double[] mCoords = {0, 0};






    /**
     * Construct a message by settings its title and message
     *
     * @param title The index of this piece of data
     * @param message The string contents for this piece of data
     */
    Datum(int id, String user_id, String userName, String title, String message, int likes, int dislikes, String imageURL, String link, String fileID,  String fileString, String fileName, String fileLink, String location, double[] coords) {
        mId = id;
        mUserId = user_id;
        mUsername = userName;
        mTitle = title;
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
        mCoords[0] = coords[0];
        mCoords[1] = coords[1];
    }
}