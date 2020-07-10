package com.javaquarium.backend.services;

import com.javaquarium.Event;
import com.javaquarium.backend.Settings;
import com.firework.Dependency;
import com.firework.OnEvent;
import com.firework.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class AquariumLoaderService {

    @Dependency private AlertService alertService;
    @Dependency private CryptographyService cryptographyService;
    @Dependency private AquariumService aquariumService;

    public void save(File file, String key) {
        String str = aquariumService.toString();
        if (key.length() > 0) {
            str = cryptographyService.encrypt(str, key);
        }
        try {
            Files.writeString(file.toPath(), str);
        } catch (IOException e) {
            alertService.errorAlert("Could not write to " + file.getPath() + " : " + e);
        }
        alertService.inform("Saved aquarium to " + file.getPath() + (key.length() > 0 ? " encrypted with key " + key : ""));
    }

    public void load(File file, String key) {
        String str;
        try {
            str = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            alertService.errorAlert("Could not read from " + file.getPath() + " : " + e);
            return;
        }
        if (key.length() > 0) {
            try {
                str = cryptographyService.decrypt(str, key);
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                alertService.errorAlert("Incorrect key or invalid data");
                return;
            }
        }
        if (!aquariumService.loadFromString(str)) {
            alertService.errorAlert("Invalid aquarium file " + file);
            return;
        }
        alertService.inform("Loaded aquarium from " + file.getPath() + (key.length() > 0 ? " encrypted with key " + key : ""));
    }

    @OnEvent(Event.LOGIN)
    public void loadDefault() {
        String str;
        try {
            str = new String(Files.readAllBytes(Paths.get(Settings.defaultAquarium)));
        } catch (IOException e) {
            alertService.errorAlert("Could not read from " + Settings.defaultAquarium + " : " + e);
            return;
        }
        if (!aquariumService.loadFromString(str)) {
            alertService.errorAlert("Invalid aquarium file " + Settings.defaultAquarium);
        }
    }
}
