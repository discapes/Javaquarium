package com.javaquarium.backend.services;

import com.firework.AfterInjection;
import com.firework.Dependency;
import com.firework.OnEvent;
import com.firework.Service;
import com.javaquarium.Events;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class AquariumFileService {

    @Dependency private SettingService settingService;
    @Dependency private AlertService alertService;
    @Dependency private CryptographyService cryptographyService;
    @Dependency private AquariumDataService aquariumDataService;

    @AfterInjection
    private void initDefaultFile() {
        File defaultFile = new File(settingService.defaultAquarium);
        try {
            boolean isNew = defaultFile.createNewFile();
            if (isNew) {
                Files.writeString(defaultFile.toPath(),
                        "100 100\n" +
                        "Candice COD 164 #eedfaa 100\n" +
                        "Bob COD 234 #eebbaa 100\n" +
                        "Angelina BLUETANG 84 #00ff00 100\n" +
                        "Dave COD 197 #ff9980 100\n" +
                        "Jack BLUETANG 195 #800080 100\n" +
                        "Joe COD 118 #00cdff 100\n");
            }
        } catch (IOException e) {
            alertService.errorAlert("Could not create default aquarium file " + settingService.defaultAquarium + ": " + e);
        }
    }

    public void save(File file, String key) {
        String str = aquariumDataService.toString();
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
        if (!aquariumDataService.loadFromString(str)) {
            alertService.errorAlert("Invalid aquarium file " + file);
            return;
        }
        alertService.inform("Loaded aquarium from " + file.getPath() + (key.length() > 0 ? " encrypted with key " + key : ""));
    }

    @OnEvent(Events.PRELOAD)
    public void loadDefault() {
        String str;
        try {
            str = new String(Files.readAllBytes(Paths.get(settingService.defaultAquarium)));
        } catch (IOException e) {
            alertService.errorAlert("Could not read from " + settingService.defaultAquarium + " : " + e);
            return;
        }
        if (!aquariumDataService.loadFromString(str)) {
            alertService.errorAlert("Invalid aquarium file " + settingService.defaultAquarium);
        }
    }
}
