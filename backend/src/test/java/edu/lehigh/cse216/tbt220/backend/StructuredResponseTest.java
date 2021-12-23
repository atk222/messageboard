package edu.lehigh.cse216.tbt220.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class StructuredResponseTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public StructuredResponseTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(StructuredResponseTest.class);
    }

    /**
     * Ensure that the constructor populates every field of the object it
     * creates
     */
    public void testConstructor() {
        String status = "Test status";
        String message = "Test message";
        Object testObject = new Object();
        StructuredResponse d = new StructuredResponse(status, message, testObject);
        d.mStatus = status;
        d.mMessage = message;
        d.mData = testObject;

        assertTrue(d.mStatus.equals(status));
        assertTrue(d.mMessage.equals(message));
        assertTrue(d.mData == testObject);
    }
}