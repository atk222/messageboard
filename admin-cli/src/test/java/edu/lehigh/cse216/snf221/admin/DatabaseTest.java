package edu.lehigh.cse216.snf221.admin;

import edu.lehigh.cse216.snf221.admin.Database.*;
import edu.lehigh.cse216.snf221.admin.Database.RowData;
import edu.lehigh.cse216.snf221.admin.Database.UserProfile;
import junit.framework.*;
import java.sql.Date;
import java.sql.Timestamp;



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
     * Ensure that the constructor populates every field of the object it
     * creates
     */
    public void testConstructor() {
        String title = "Test Title";
        String content = "Test Content";
        int id = 17;
        int like = 1;
        int dislike = 1;

        int post_id = 4;
        String user_id = "1";
        String name = "Margret";
        String email = "tbt220@Lehigh.edu";
        String imageURL = "gwrgergerher";
        String comment = "I love 216";
        int comment_id = 33;

        String link = "3info3owqmfoe";
        String fileID = "3unf939eqjf3";
        String fileString = "343ref24f42f";
        String file_name = "example_name";
        String location ="RathBone Hall";
        String fileLink="I mean i dont know what how u have set them up";
        double[] coords= {40.606927, -75.372896};
        Timestamp date = new Timestamp(System.currentTimeMillis());

        RowData d = new RowData(id, user_id, name, title, content, like, dislike, imageURL, link, fileID, fileString, file_name,fileLink,location,coords);
        UserProfile u = new UserProfile(user_id, name, email, imageURL, comment);   
        Comment c = new Comment(comment_id, user_id, name, post_id, comment, imageURL, link, fileID, fileString, file_name);
        PostDocuments pd = new PostDocuments(post_id, fileID, name, fileString, file_name, date);
        CommentDocuments cd = new CommentDocuments(post_id, comment_id, name, fileID, fileString, file_name, date);


        assertTrue(d.mSubject.equals(title));
        assertTrue(d.mMessage.equals(content));
        assertTrue(d.mId == id);
        assertTrue(d.mLikes == like);
        assertTrue(d.mDislikes == dislike);
        assertTrue(d.mCoords == coords);
        assertTrue(d.mLocation == location);


        assertTrue(u.uUserID.equals(user_id));
        assertTrue(u.uName.equals(name));
        assertTrue(u.uEmail.equals(email));
        assertTrue(u.uImageURL.equals(imageURL));
        assertTrue(u.uComment.equals(comment));

        assertTrue(c.mCommentID == comment_id);
        assertTrue(c.mUserID.equals(user_id));
        assertTrue(c.mPostID == post_id);
        assertTrue(c.mComment.equals(comment));

        assertTrue(pd.pPostID == post_id);
        assertTrue(pd.pFileID.equals(fileID));
        assertTrue(pd.pUsername.equals(name));
        assertTrue(pd.pFileString.equals(fileString));
        assertTrue(pd.pFilename.equals(file_name));
        assertTrue(pd.pDatePosted.equals(date));
        
        assertTrue(cd.cPostID == post_id);
        assertTrue(cd.cCommentID == comment_id);
        assertTrue(cd.cUsername.equals(name));
        assertTrue(cd.cFileID.equals(fileID));
        assertTrue(cd.cFileString.equals(fileString));
        assertTrue(cd.cFilename.equals(file_name));
        assertTrue(cd.cDatePosted.equals(date));


    }


    /**
     * Ensure that the copy constructor works correctly
     */
    public void testCopyconstructor() {
        String title = "Test Title For Copy";
        String content = "Test Content For Copy";
        int id = 177;
        int like = 1;
        int dislike = 2;

        int post_id = 4;
        String user_id = "TC1";
        String name = "TCMargret";
        String email = "TCtbt220@Lehigh.edu";
        String imageURL = "TCgwrgergerher";
        String comment = "TCI love 216";
        int comment_id = 31;

        String link = "SAIdsajd";
        String fileID = "41uifn3431ri";
        String fileString = "43regwdfsdfd";
        String file_name = "example_name";
        String location ="RathBone Hall";
        String fileLink="I mean i dont know what how u have set them up";
        double[] coords= {40.606927, -75.372896};
        Timestamp date = new Timestamp(System.currentTimeMillis());

        RowData d = new RowData(id, user_id, name, title, content, like, dislike, imageURL, link, fileID, fileString, file_name,fileLink,location,coords);
        RowData d2 = new RowData(d);
        assertTrue(d2.mSubject.equals(d.mSubject));
        assertTrue(d2.mMessage.equals(d.mMessage));
        assertTrue(d2.mId == d.mId);
        assertTrue(d2.mLikes == d.mLikes);
        assertTrue(d2.mDislikes == d.mDislikes);
        assertTrue(d2.mCoords == d.mCoords);
        assertTrue(d2.mLocation == d.mLocation);

        UserProfile u = new UserProfile(user_id, name, email, imageURL, comment);
        UserProfile u2 = new UserProfile(u);
        assertTrue(u2.uUserID.equals(u.uUserID));
        assertTrue(u2.uName.equals(u.uName));
        assertTrue(u2.uEmail.equals(u.uEmail));
        assertTrue(u2.uImageURL.equals(u.uImageURL));
        assertTrue(u2.uComment.equals(u.uComment));

        Comment c = new Comment(comment_id, user_id, name, post_id, comment, imageURL, link, fileID, fileString, file_name);
        Comment c2 = new Comment(c);
        assertTrue(c2.mCommentID == c.mCommentID);
        assertTrue(c2.mUserID.equals(c.mUserID));
        assertTrue(c2.mPostID == c.mPostID);
        assertTrue(c2.mComment.equals(c.mComment));

        PostDocuments pd = new PostDocuments(post_id, fileID, name, fileString, file_name, date);
        PostDocuments pd2 = new PostDocuments(pd);
        assertTrue(pd.pPostID == pd2.pPostID);
        assertTrue(pd.pFileID.equals(pd2.pFileID));
        assertTrue(pd.pUsername.equals(pd2.pUsername));
        assertTrue(pd.pFileString.equals(pd2.pFileString));
        assertTrue(pd.pFilename.equals(pd2.pFilename));
        assertTrue(pd.pDatePosted.equals(pd2.pDatePosted));
        

        CommentDocuments cd = new CommentDocuments(post_id, comment_id, name, fileID, fileString, file_name, date);
        CommentDocuments cd2 = new CommentDocuments(cd);
        assertTrue(cd.cPostID == cd2.cPostID);
        assertTrue(cd.cCommentID == cd2.cCommentID);
        assertTrue(cd.cFileID.equals(cd2.cFileID));
        assertTrue(cd.cUsername.equals(cd2.cUsername));
        assertTrue(cd.cFileString.equals(cd2.cFileString));
        assertTrue(cd.cDatePosted.equals(cd2.cDatePosted));

    }
}