package com.discape.javaquarium.logic;

public class InvalidUsersFileException extends Exception {
    public InvalidUsersFileException(String line, Throwable err) {
        super("Invalid line in users file: " + line, err);
    }
}
