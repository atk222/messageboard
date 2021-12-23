package edu.lehigh.cse216.tbt220.backend;

public class InvalidSessionIDException extends Exception {
    InvalidSessionIDException() {
        super("Invalid Session ID");
    }
}