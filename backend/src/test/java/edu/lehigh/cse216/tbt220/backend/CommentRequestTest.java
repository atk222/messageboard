package edu.lehigh.cse216.tbt220.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class CommentRequestTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CommentRequestTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CommentRequestTest.class);
    }

    /**
     * Ensure that the constructor populates every field of the object it
     * creates
     */
    public void testConstructor() {
        int commentID = 21213;
        String comment = "Test comment";
        String userID = "2132132131";
        int postID = 17;
        CommentRequest d = new CommentRequest();
        d.mCommentID = commentID;
        d.mUserID = userID;
        d.mPostID = postID;
        d.mComment = comment;

        assertTrue(d.mCommentID == commentID);
        assertTrue(d.mUserID.equals(userID));
        assertTrue(d.mPostID == postID);
        assertTrue(d.mComment == comment);
    }
}