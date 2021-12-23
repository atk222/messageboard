package edu.lehigh.cse216.snf221.admin;
//DONT USE THIS ANYMORE USE CLASSES BUILT INTO Database.java

public class DataRow {

    public static class PostRow {
        /**
         * The unique identifier associated with this element. It's final, because we
         * never want to change it.
         */
        public final int mId;

        /**
         * The title for this row of data
         */
        public String mTitle;

        /**
         * The content for this row of data
         */
        public String mContent;

        /**
         * The like for this row of data
         */
        public int mLike;

        /**
         * The dislike for this row of data
         */
        public int mDislike;

        /**
         * Create a new DataRow with the provided id and title/content, and a creation
         * date based on the system clock at the time the constructor was called
         * 
         * @param id      The id to associate with this row. Assumed to be unique
         *                throughout the whole program.
         * 
         * @param title   The title string for this row of data
         * 
         * @param content The content string for this row of data
         * 
         * @param like    The like for this row of data
         * 
         * @param dislike The dislike for this row of data
         */
        PostRow(int id, String title, String content, int like, int dislike) {
            mId = id;
            mTitle = title;
            mContent = content;
            mLike = like;
            mDislike = dislike;
        }

        /**
         * Copy constructor to create one datarow from another
         */
        PostRow(PostRow data) {
            mId = data.mId;
            // NB: Strings and Dates are immutable, so copy-by-reference is safe
            mTitle = data.mTitle;
            mContent = data.mContent;
            mLike = data.mLike;
            mDislike = data.mDislike;
        }
    }
    // ADDED TEST CASES FOR PHASE 2

    public static class UserRow {
        /**
         * The user_id of this row of the database
         */
        String uId;
        /**
         * The name stored in this row
         */
        String uName;
        /**
         * The email stored in this row
         */
        String uEmail;

        // The image url stored in this row
        String uImg;

        // The comment stored in this row
        String uComment;

        /**
         * Construct a RowData object by providing values for its fields
         */
        public UserRow(String user_id, String name, String email, String imagURL, String comment) {
            uId = user_id;
            uName = name;
            uEmail = email;
            uImg = imagURL;
            uComment = comment;
        }

        /**
         * Copy constructor to create one datarow from another
         */
        UserRow(UserRow data) {
            uId = data.uId;
            // NB: Strings and Dates are immutable, so copy-by-reference is safe
            uName = data.uName;
            uEmail = data.uEmail;
            uImg = data.uImg;
            uComment = data.uComment;
        }
    }

    
}
