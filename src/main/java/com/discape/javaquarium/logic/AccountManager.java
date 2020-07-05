package com.discape.javaquarium.logic;

import javax.inject.Inject;
import java.io.*;
import java.util.Scanner;

public class AccountManager {

    private final String accountsPath = System.getProperty("user.home") + "/.javaquariumusers";
    @Inject private Cryptographer cryptographer;
    private String currentAccount;

    public AccountManager() {
        try {
            //noinspection ResultOfMethodCallIgnored
            new File(accountsPath).createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create users file: " + e.getMessage());
        }
    }

    public void loginAsGuest() {
        currentAccount = "Guest N/A N/A";
    }

    public void logout() {
        currentAccount = "not_logged_in N/A N/A";
    }

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
                    return true;
                }
            }
        }
        return false;
    }

    public boolean register(String username, String password) throws IOException, InvalidUsersFileException {
        File file = new File(accountsPath);
        PrintWriter pw = new PrintWriter(new FileWriter(file, true));
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            System.out.println(line);
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

    public String getUsername() {
        return currentAccount.substring(0, currentAccount.indexOf(" "));
    }

    public String getSalt() {
        return currentAccount.substring(currentAccount.indexOf(' ') + 1, currentAccount.lastIndexOf(' '));
    }

    public String getHash() {
        return currentAccount.substring(currentAccount.lastIndexOf(' ') + 1);
    }
}

