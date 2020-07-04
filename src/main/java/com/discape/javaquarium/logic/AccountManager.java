package com.discape.javaquarium.logic;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    public boolean login(String username, String password) {
        Scanner scanner;
        try {
            scanner = new Scanner(new File(accountsPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("This file should have already been created?");
            return false;
        }
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String lineUsername = line.substring(0, line.indexOf(" "));
            String hash = line.substring(line.indexOf(" ") + 1);
            if (username.equals(lineUsername))
                if (cryptographer.testPassword(hash, password)) {
                    currentAccount = line;
                    return true;
                }
        }
        return false;
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

