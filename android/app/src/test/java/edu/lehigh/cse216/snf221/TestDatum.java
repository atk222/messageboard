package edu.lehigh.cse216.snf221;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TestDatum {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void Datum_constructor_sets_fields() throws Exception {
        //Testing functionality of Datum object type that is used
        Datum d = new Datum(7, "Title", "Message", 25, 25, "message", "john", "myfile", "image", "JPEG");
        assertEquals(d.mId, 7);
        assertEquals(d.mTitle, "Title");
        assertEquals(d.mMessage, "Message");
        assertEquals(d.mLikes, 25);
        assertEquals(d.mDislikes, 25);
        assertEquals(d.mUserId, "message");
        assertEquals(d.mUsername, "john");
        assertEquals(d.fileName, "myfile");
        assertEquals(d.fileContent, "image");
        assertEquals(d.file, "JPEG");


    }
}