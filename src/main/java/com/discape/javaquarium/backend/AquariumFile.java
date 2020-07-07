package com.discape.javaquarium.backend;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Is responsible for loading and saving the aquarium to a file, and displaying error alerts.
 */
public class AquariumFile {

    @Inject private Alerts alerts;
    @Inject private Cryptographer cryptographer;

    /**
     * Saves an aquarium to a file.
     * @param key key aquarium will be encrypted with if length > 0.
     * @param file file aquarium will be written to.
     * @param aquarium aquarium that will be written to the file.
     */
    public void save(String key, File file, Aquarium aquarium) {
        String str;
        if (key.length() > 0) {
            str = cryptographer.encrypt(aquarium.toString(), key);
        } else {
            str = aquarium.toString();
        }
        try {
            Files.writeString(file.toPath(), str);
        } catch (IOException e) {
            alerts.errorAlert("Could not write to file: " + e.getMessage());
            return;
        }
        alerts.inform("Saved aquarium to " + file.getPath() + (key.length() > 0 ? " encrypted with " + key : ""));
    }

    /**
     * Attempts to load an aquarium from a file.
     * @param key key aquarium was encrypted with if length > 0.
     * @param file file aquarium was written to.
     * @return An aquarium read from the file, or null.
     */
    public Aquarium read(String key, File file) {
        String rawStr;
        try {
            rawStr = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            alerts.errorAlert("Could not read from file: " + e.getMessage());
            return null;
        }

        String aquariumStr = rawStr;
        if (key.length() > 0) {
            try {
                aquariumStr = cryptographer.decrypt(rawStr, key);
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                alerts.errorAlert("Incorrect key or invalid data.");
                return null;
            }
        }

        Aquarium aquarium;
        try {
            aquarium = new Aquarium(aquariumStr);
        } catch (Exception e) {
            alerts.errorAlert("Invalid data: " + e);
            e.printStackTrace();
            return null;
        }
        alerts.inform("Loaded aquarium from " + file.getPath() + (key.length() > 0 ? " encrypted with " + key : ""));
        return aquarium;
    }

    /**
     * Attempts to load a default aquarium from ~/.javaquariumdefault.txt.
     * @return An aquarium read from the file, or an empty one.
     */
    public Aquarium loadDefault() {
        String filePath = System.getProperty("user.home") + "/.javaquariumdefault.txt";
        String aquariumStr;

        try {
            aquariumStr = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            alerts.errorAlert("Default file " + filePath + " can't be read from: " + e);
            return new Aquarium();
        }

        try {
            return new Aquarium(aquariumStr);
        } catch (Exception e) {
            alerts.errorAlert("Default file " + filePath + " is invalid:  " + e);
        }
        return new Aquarium();
    }
}
