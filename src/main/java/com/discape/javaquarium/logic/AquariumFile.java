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

    @Inject private Alerts alerts;
    @Inject private Cryptographer cryptographer;
    @Inject private Aquarium aquarium;
    @Inject private StageUtilities stageUtilities;

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

    public void load(String key, File file) {
        String rawStr;
        try {
            rawStr = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            alerts.errorAlert("Could not read from file: " + e.getMessage());
            return;
        }
        String aquariumStr = rawStr;
        if (key.length() > 0) {
            try {
                aquariumStr = cryptographer.decrypt(rawStr, key);
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                alerts.errorAlert("Wrong key or bad encryption!");
                return;
            } catch (InvalidKeyException e) {
                alerts.errorAlert("Invalid key!");
                return;
            } catch (IllegalArgumentException e) {
                alerts.errorAlert(e.getMessage());
                return;
            }
        }
        try {
            Aquarium aquarium = Aquarium.fromString(aquariumStr);
            Injector.setModelOrService(Aquarium.class, aquarium);
        } catch (Exception e) {
            alerts.errorAlert("Invalid data: " + e.getMessage());
            return;
        }
        alerts.inform("Loaded aquarium from " + file.getPath() + (key.length() > 0 ? " encrypted with " + key : ""));
    }
}
