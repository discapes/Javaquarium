package com.discape.javaquarium.backend;

/** An exception thrown when .javaquariumusers.txt is invalid */
public class InvalidUsersFileException extends Exception {
    /**
     * @param line line the error occurred on.
     * @param err probably a stringIndexOutOfBounds error.
     */
    public InvalidUsersFileException(String line, Throwable err) {
        super("Invalid line in users file: " + line, err);
    }
}
