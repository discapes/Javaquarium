package com.discape.javaquarium.backend.listeners;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.backend.Alerts;
import com.discape.javaquarium.backend.Cryptographer;
import com.discape.javaquarium.backend.Logger;
import com.discape.javaquarium.backend.aquarium.Aquarium;
import com.discape.javaquarium.backend.events.Events;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AquariumLoader {

    @Inject private Alerts alerts;
    @Inject private String defaultFile;
    @Inject private Cryptographer cryptographer;
    private Aquarium aquarium;

    public void save(File file, String key) throws IOException {
        Logger.log(this, "save(File, String)");
        String str = aquarium.toString();
        if (key.length() > 0) {
            str = cryptographer.encrypt(str, key);
        }
        Files.writeString(file.toPath(), str);
    }

    public void load(File file, String key) throws IOException, BadPaddingException, IllegalBlockSizeException {
        Logger.log(this, "load(File, String)");
        String str = new String(Files.readAllBytes(file.toPath()));
        if (key.length() > 0) {
            str = cryptographer.decrypt(str, key);
        }
        finish(new Aquarium(str));
    }

    public void loadDefault() throws IOException {
        Logger.log(this, "loadDefault()");
        String str = new String(Files.readAllBytes(Paths.get(defaultFile)));
        finish(new Aquarium(str));
    }

    private void finish(Aquarium aquarium) {
        Injector.injectMembers(aquarium.getClass(), aquarium);
        Events.NEWAQUARIUM.e().fire(aquarium);
    }

    public AquariumLoader() {
        Logger.log("AquariumLoader", "AquariumLoader()");
        Events.NEWAQUARIUM.e().addListener(aquarium -> this.aquarium = (Aquarium) aquarium);
        Events.LOGIN.e().addListener(account -> {
            try {
                loadDefault();
            } catch (IOException e) {
                alerts.errorAlert("Could not read default file: " + e);
            }
        });
    }
}
