package com.javaquarium.backend;

/**
 * An exception thrown by AccountService when the users file is invalid
 */
public class InvalidUsersFileException extends Exception {
    /**
     * @param line line the error occurred on.
     * @param err  probably a StringIndexOutOfBounds error.
     */
    public InvalidUsersFileException(String line, Throwable err) {
        super("Invalid line in users file: " + line, err);
    }
}
