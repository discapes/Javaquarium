package com.discape.javaquarium.backend;

import com.discape.javaquarium.backend.events.Events;
import com.discape.javaquarium.backend.misc.InvalidUsersFileException;

import javax.inject.Inject;
import java.io.*;
import java.util.Scanner;

/**
 * Is responsible for logging the user in an out.
 */
public class AccountManager {

    @Inject private Cryptographer cryptographer;
    private final String accountsPath = System.getProperty("user.home") + "/.javaquariumusers.txt";
    private String currentAccount;
    /* "username base64salt base64hash" */

    /** Creates a file in the home directory named .javaquariumusers.txt */
    public AccountManager() {
        try {
            //noinspection ResultOfMethodCallIgnored
            new File(accountsPath).createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create users file: " + e.getMessage());
        }
    }

    /** Logs in as an arbitrary guest user, doesn't read any files. */
    public void loginAsGuest() {
        currentAccount = "Guest N/A N/A";
        Events.LOGIN.e().fire(currentAccount);
    }

    /** Logs out and stops the sessionManager. */
    public void logout() {
        Events.LOGOUT.e().fire(currentAccount);
        currentAccount = "not_logged_in N/A N/A";
    }

    /**
     * Attempts to login.
     * @param username username.
     * @param password password.
     * @return A boolean indicating if the login was successful
     * @throws FileNotFoundException if .javaquariumusers.txt doesn't exist.
     * @throws InvalidUsersFileException if .javaquariumusers.txt is invalid.
     */
    public boolean login(String username, String password) throws FileNotFoundException, InvalidUsersFileException {
        Scanner scanner = new Scanner(new File(accountsPath));
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String lineUsername;
            String hash;
            try {
                lineUsername = line.substring(0, line.indexOf(" "));
                hash = line.substring(line.indexOf(" ") + 1);
            } catch (StringIndexOutOfBoundsException e) {
                throw new InvalidUsersFileException(line, e);
            }
            if (username.equalsIgnoreCase(lineUsername)) {
                if (cryptographer.testPassword(hash, password)) {
                    currentAccount = line;
                    Events.LOGIN.e().fire(currentAccount);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Attempts to register a new account.
     * @param username username to be registered.
     * @param password password to be registered with the username.
     * @return A boolean indicating if the registration was successful or not.
     * @throws IOException if the .javaquariumusers.txt can't be written to.
     * @throws InvalidUsersFileException if .javaquariumusers.txt is invalid.
     */
    public boolean register(String username, String password) throws IOException, InvalidUsersFileException {
        File file = new File(accountsPath);
        PrintWriter pw = new PrintWriter(new FileWriter(file, true));
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String lineUsername;
            try {
                lineUsername = line.substring(0, line.indexOf(" "));
            } catch (StringIndexOutOfBoundsException e) {
                throw new InvalidUsersFileException(line, e);
            }
            if (lineUsername.equalsIgnoreCase(username)) {
                return false;
            }
        }
        pw.println(username + " " + cryptographer.saltHashPassword(password));
        pw.close();
        return true;
    }

    /** Returns the username of the currently logged in account. */
    public String getUsername() {
        return currentAccount.substring(0, currentAccount.indexOf(" "));
    }
    /** Returns the salt of the currently logged in account. */
    public String getSalt() {
        return currentAccount.substring(currentAccount.indexOf(' ') + 1, currentAccount.lastIndexOf(' '));
    }
    /** Returns the salted hash of the currently logged in account. */
    public String getHash() {
        return currentAccount.substring(currentAccount.lastIndexOf(' ') + 1);
    }
}

