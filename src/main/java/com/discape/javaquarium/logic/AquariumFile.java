package com.discape.javaquarium.logic;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.display.Alerts;
import com.discape.javaquarium.display.StageUtilities;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;

public class AquariumFile {

    private String key = "";
    private File file = new File(getDefaultFilePath());

    public void setFile(File file) {
        this.file = file;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Inject private Alerts alerts;
    @Inject private Cryptographer cryptographer;
    private Aquarium aquarium = new Aquarium();
    @Inject private StageUtilities stageUtilities;

    public String getDefaultFilePath() {
        return System.getProperty("user.home") + "/.javaquariumdefault.txt";
    }

    public Aquarium load() {
        return load(key, file);
    }

    public void save(String key, File file) {
        String str;
        if (key.length() > 0) {
            try {
                str = cryptographer.encrypt(aquarium.toString(), key);
            } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                alerts.errorAlert("Error: " + e.getMessage());
                return;
            }
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

    public Aquarium load(String key, File file) {
        String rawStr;
        try {
            rawStr = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            alerts.errorAlert("Could not read from file: " + e.getMessage());
            return aquarium;
        }
        String aquariumStr = rawStr;
        if (key.length() > 0) {
            try {
                aquariumStr = cryptographer.decrypt(rawStr, key);
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                alerts.errorAlert("Wrong key or bad encryption!");
                return aquarium;
            } catch (InvalidKeyException e) {
                alerts.errorAlert("Invalid key!");
                return aquarium;
            } catch (IllegalArgumentException e) {
                alerts.errorAlert(e.getMessage());
                return aquarium;
            }
        }

        try {
            aquarium = Aquarium.fromString(aquariumStr);
            Injector.injectMembers(Aquarium.class, aquarium);
            Injector.setModelOrService(Aquarium.class, aquarium);
            aquarium.postConstruct();
        } catch (Exception e) {
            alerts.errorAlert("Invalid data: " + e.getMessage());
            return this.aquarium;
        }
        alerts.inform("Loaded aquarium from " + file.getPath() + (key.length() > 0 ? " encrypted with " + key : ""));
        return aquarium;
    }
}
