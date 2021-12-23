
package edu.lehigh.cse216.tbt220.backend;

public class Comment {
    int mCommentID;
    String mUserID;
    int mPostID;
    String mComment;
    String mImageURL;
    String mUsername;
    String mLink;
    String mFileID;
    String mFileString;
    String mFileName;
    String mFileLink;

    public Comment(int commentID, String userID, int postID, String comment, String imageURL, String name, String link,
            String fileID, String fileString, String fileName, String fileLink) {
        mCommentID = commentID;
        mUserID = userID;
        mUsername = name;
        mPostID = postID;
        mComment = comment;
        mImageURL = imageURL;
        mLink = link;
        mFileID = fileID;
        mFileString = fileString;
        mFileName = fileName;
        mFileLink = fileLink;
    }

    public Comment(Comment comment) {
        mCommentID = comment.mCommentID;
        mUserID = comment.mUserID;
        mUsername = comment.mUsername;
        mPostID = comment.mPostID;
        mComment = comment.mComment;
        mImageURL = comment.mImageURL;
        mLink = comment.mLink;
        mFileID = comment.mFileID;
        mFileString = comment.mFileString;
        mFileName = comment.mFileName;
        mFileLink = comment.mFileLink;
    }
}