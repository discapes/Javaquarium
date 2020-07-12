package com.javaquarium.backend.services;

import com.firework.Dependency;
import com.firework.Firework;
import com.firework.Service;
import com.javaquarium.Events;
import com.javaquarium.backend.InvalidUsersFileException;

import java.io.*;
import java.util.Scanner;

/**
 * Is responsible for managing accounts in ~/.javaquariumusers.txt.
 */
@SuppressWarnings("unused")
@Service
public class AccountService {

    private final String accountsPath = System.getProperty("user.home") + "/.javaquariumusers.txt";
    @Dependency private CryptographyService cryptographyService;
    private String currentAccount; /* "username base64salt base64hash" */

    /** Creates the users file if it doesn't already exist. */
    public AccountService() {
        try {
            //noinspection ResultOfMethodCallIgnored
            new File(accountsPath).createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create users file: " + e.getMessage());
        }
    }

    /**
     * Logs in as an arbitrary guest user, doesn't read any files. Fires LOGIN.
     */
    public void loginAsGuest() {
        currentAccount = "Guest N/A N/A";
        Firework.queueAutomaticEvent(Events.LOGIN);
    }

    /**
     * Fires LOGOUT.
     */
    public void logout() {
        currentAccount = "not_logged_in N/A N/A";
        Firework.queueAutomaticEvent(Events.LOGOUT);
    }

    /**
     * Reads the users file and attempts to login. Fires LOGIN.
     *
     * @param username username
     * @param password password
     * @return A boolean indicating if the login was successful.
     * @throws FileNotFoundException     the users file doesn't exist.
     * @throws InvalidUsersFileException the users file is invalid.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
                if (cryptographyService.testPassword(hash, password)) {
                    currentAccount = line;
                    Firework.queueAutomaticEvent(Events.LOGIN);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Attempts to register a new account to the users file.
     *
     * @param username username to be registered
     * @param password password to be registered with the username
     * @return A boolean indicating if the registration was successful or not.
     * @throws IOException               the the users file can't be written to.
     * @throws InvalidUsersFileException the users file is invalid.
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
        pw.println(username + " " + cryptographyService.saltHashPassword(password));
        pw.close();
        return true;
    }

    /**
     * Returns the username of the currently logged in account.
     * @return the username of the currently logged in account
     */
    public String getUsername() {
        return currentAccount.substring(0, currentAccount.indexOf(" "));
    }

    /**
     * Returns the salt of the currently logged in account.
     * @return the salt of the currently logged in account
     */
    public String getSalt() {
        return currentAccount.substring(currentAccount.indexOf(' ') + 1, currentAccount.lastIndexOf(' '));
    }

    /**
     * Returns the salted hash of the currently logged in account.
     * @return the salted hash of the currently logged in account
     * */
    public String getHash() {
        return currentAccount.substring(currentAccount.lastIndexOf(' ') + 1);
    }
}
