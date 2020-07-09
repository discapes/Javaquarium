package com.javaquarium.backend.services;

import com.javaquarium.Event;
import com.javaquarium.backend.Settings;
import com.javaquarium.backend.aquarium.Aquarium;
import com.javaquarium.backend.aquarium.InvalidAquariumFileException;
import com.management.Dependency;
import com.management.LawnMower;
import com.management.OnEvent;
import com.management.Service;

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
    private Aquarium aquarium;

    public void save(File file, String key) {
        String str = aquarium.toString();
        if (key.length() > 0) {
            str = cryptographyService.encrypt(str, key);
        }
        try {
            Files.writeString(file.toPath(), str);
        } catch (IOException e) {
            alertService.errorAlert("Could not write to " + file.getPath() + " : " + e);
        }
    }

    public void load(File file, String key) {
        String str = null;
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
        Aquarium aquarium;
        try {
            aquarium = new Aquarium(str);
        } catch (InvalidAquariumFileException e) {
            alertService.errorAlert("Invalid aquarium file " + Settings.defaultAquarium + " : " + e);
            return;
        }
        LawnMower.queueAutomaticEvent(Event.NEWAQUARIUM, aquarium);
    }

    @OnEvent(Event.LOGIN)
    public void loadDefault() {
        Aquarium aquarium = null;
        try {
            String str = new String(Files.readAllBytes(Paths.get(Settings.defaultAquarium)));
            aquarium = new Aquarium(str);
        } catch (IOException e) {
            alertService.errorAlert("Could not read from " + Settings.defaultAquarium + " : " + e);
        } catch (InvalidAquariumFileException e) {
            alertService.errorAlert("Invalid aquarium file " + Settings.defaultAquarium + " : " + e);
        }
        if (aquarium == null) aquarium = new Aquarium();
        LawnMower.queueAutomaticEvent(Event.NEWAQUARIUM, aquarium);
    }
}
