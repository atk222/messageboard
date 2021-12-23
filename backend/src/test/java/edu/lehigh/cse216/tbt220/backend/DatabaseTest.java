package edu.lehigh.cse216.tbt220.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class DatabaseTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DatabaseTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DatabaseTest.class);
    }

    /**
     * Ensure that the constructor for RowData populates every field of the object
     * it creates
     */
    public void testRowDataConstructor() {
        int postID = 1;
        String user_id = "243r1rr3r";
        String username = "test";
        String subject = "Test Title";
        String message = "Test Content";
        int likes = 1;
        int dislikes = 1;
        String imageURL = "24r3tewffw";
        String link = "2r3eqfqfeq";
        String fileID = "2433rrqwf";
        String fileString = "2refeffe";
        String fileName = "TestName";
        String fileLink = "3r3tq";
        String location = "location";
        double[] coords = new double[2];
        RowData d = new RowData(postID, user_id, username, subject, message, likes, dislikes, imageURL, link, fileID,
                fileString, fileName, fileLink, location, coords);

        assertTrue(d.mId == postID);
        assertTrue(d.mUserId.equals(user_id));
        assertTrue(d.mUsername.equals(username));
        assertTrue(d.mSubject.equals(subject));
        assertTrue(d.mMessage.equals(message));
        assertTrue(d.mLikes == likes);
        assertTrue(d.mDislikes == dislikes);
        assertTrue(d.mImageURL.equals(imageURL));
        assertTrue(d.mLink.equals(link));
        assertTrue(d.mFileId.equals(fileID));
        assertTrue(d.mFileString.equals(fileString));
        assertTrue(d.mFileName.equals(fileName));
        assertTrue(d.mFileLink.equals(fileLink));
        assertTrue(d.mLocation.equals(location));
        assertTrue(d.mCoords.equals(coords));
    }

    /**
     * Ensure that the copy constructor for RowData works correctly
     */
    public void testRowDataCopyConstructor() {
        int postID = 1;
        String user_id = "243r1rr3r";
        String username = "test";
        String subject = "Test Title";
        String message = "Test Content";
        int likes = 1;
        int dislikes = 1;
        String imageURL = "24r3tewffw";
        String link = "2r3eqfqfeq";
        String fileID = "2433rrqwf";
        String fileString = "2refeffe";
        String fileName = "TestName";
        String fileLink = "3r3tq";
        String location = "location";
        double[] coords = new double[2];
        RowData d = new RowData(postID, user_id, username, subject, message, likes, dislikes, imageURL, link, fileID,
                fileString, fileName, fileLink, location, coords);

        RowData a = new RowData(d);

        assertTrue(d.mId == a.mId);
        assertTrue(d.mUserId.equals(a.mUserId));
        assertTrue(d.mUsername.equals(a.mUsername));
        assertTrue(d.mSubject.equals(a.mSubject));
        assertTrue(d.mMessage.equals(a.mMessage));
        assertTrue(d.mLikes == a.mLikes);
        assertTrue(d.mDislikes == a.mDislikes);
        assertTrue(d.mImageURL.equals(a.mImageURL));
        assertTrue(d.mLink.equals(a.mLink));
        assertTrue(d.mFileId.equals(a.mFileId));
        assertTrue(d.mFileString.equals(a.mFileString));
        assertTrue(d.mFileName.equals(a.mFileName));
        assertTrue(d.mFileLink.equals(a.mFileLink));
        assertTrue(d.mLocation.equals(a.mLocation));
        assertTrue(d.mCoords.equals(a.mCoords));
    }

    public void testUserProfileConstructor() {
        String uUserID = "test";
        String uName = "test";
        String uEmail = "test";
        String uImageURL = "test";
        String uComment = "test";

        UserProfile t = new UserProfile(uUserID, uName, uEmail, uImageURL, uComment);

        assertTrue(t.uUserID.equals(uUserID));
        assertTrue(t.uName.equals(uName));
        assertTrue(t.uEmail.equals(uEmail));
        assertTrue(t.uImageURL.equals(uImageURL));
        assertTrue(t.uComment.equals(uComment));
    }

    public void testUserProfileCopyConstructor() {
        String uUserID = "test";
        String uName = "test";
        String uEmail = "test";
        String uImageURL = "test";
        String uComment = "test";

        UserProfile t = new UserProfile(uUserID, uName, uEmail, uImageURL, uComment);
        UserProfile tf = new UserProfile(t);

        assertTrue(t.uUserID.equals(tf.uUserID));
        assertTrue(t.uName.equals(tf.uName));
        assertTrue(t.uEmail.equals(tf.uEmail));
        assertTrue(t.uImageURL.equals(tf.uImageURL));
        assertTrue(t.uComment.equals(tf.uComment));
    }

    public void testCommentConstructor() {
        int mCommentID = 1;
        String mUserID = "test";
        int mPostID = 1;
        String mComment = "test";
        String mImageURL = "test";
        String mUsername = "test";
        String mLink = "test";
        String mFileID = "test";
        String mFileString = "test";
        String mFileName = "test";
        String mFileLink = "test";

        Comment t = new Comment(mCommentID, mUserID, mPostID, mComment, mImageURL, mUsername, mLink, mFileID,
                mFileString, mFileName, mFileLink);

        assertTrue(t.mCommentID == mCommentID);
        assertTrue(t.mUserID.equals(mUserID));
        assertTrue(t.mPostID == mPostID);
        assertTrue(t.mComment.equals(mComment));
        assertTrue(t.mImageURL.equals(mImageURL));
        assertTrue(t.mUsername.equals(mUsername));
        assertTrue(t.mLink.equals(mLink));
        assertTrue(t.mFileID.equals(mFileID));
        assertTrue(t.mFileString.equals(mFileString));
        assertTrue(t.mFileName.equals(mFileName));
        assertTrue(t.mFileLink.equals(mFileLink));
    }

    public void testCommentCopyConstructor() {
        int mCommentID = 1;
        String mUserID = "test";
        int mPostID = 1;
        String mComment = "test";
        String mImageURL = "test";
        String mUsername = "test";
        String mLink = "test";
        String mFileID = "test";
        String mFileString = "test";
        String mFileName = "test";
        String mFileLink = "test";

        Comment t = new Comment(mCommentID, mUserID, mPostID, mComment, mImageURL, mUsername, mLink, mFileID,
                mFileString, mFileName, mFileLink);

        Comment tf = new Comment(t);

        assertTrue(t.mCommentID == tf.mCommentID);
        assertTrue(t.mUserID.equals(tf.mUserID));
        assertTrue(t.mPostID == tf.mPostID);
        assertTrue(t.mComment.equals(tf.mComment));
        assertTrue(t.mImageURL.equals(tf.mImageURL));
        assertTrue(t.mUsername.equals(tf.mUsername));
        assertTrue(t.mLink.equals(tf.mLink));
        assertTrue(t.mFileID.equals(tf.mFileID));
        assertTrue(t.mFileString.equals(tf.mFileString));
        assertTrue(t.mFileName.equals(tf.mFileName));
        assertTrue(t.mFileLink.equals(tf.mFileLink));
    }
}