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

/** Reads aquarium data from files and handles the errors by showing alerts. */
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
                "111.1901731292121 97.23256384546005\n" +
                "Jacob BLUETANG 158 #6884ee 0\n" +
                "Michael NEONTETRA 131 #00b2ff 100\n" +
                "Matthew NORTHERNPIKE 99 #d3de87 57\n" +
                "Joshua COD 54 #d2c08c 33\n" +
                "Emily REDSNAPPER 67 #cd775c 87\n" +
                "Hannah NEONTETRA 324 #00ff88 77\n" +
                "Christopher COD 147 #d29a8c 0\n" +
                "Nicholas GOLFISH 88 #ff9700 19\n" +
                "Madison NEONTETRA 308 #00ff7d 33\n" +
                "Andrew COD 63 #cad28c 0\n" +
                "Ashley BLUETANG 155 #c768ee 53\n" +
                "Sarah GOLFISH 106 #ffff00 100\n" +
                "Alexis BLUETANG 31 #8268ee 53\n" +
                "Samantha COD 143 #d1d28c 14\n" +
                "Joseph COD 81 #d2bd8c 51\n" +
                "Daniel BLUETANG 145 #687fee 51\n" +
                "Tyler GOLFISH 126 #ff2b00 78\n" +
                "William COD 227 #d28c9a 73\n" +
                "Jessica OCEANSUNFISH 100 #f7ffcd 6\n" +
                "Elizabeth MAHIMAHI 350 #2e8b6e 60\n" +
                "Brandon GOLFISH 73 #ff2400 100\n" +
                "Taylor COD 197 #d29f8c 100\n" +
                "Lauren CLOWNFISH 23 #b9ff00 45\n" +
                "Ryan NEONTETRA 136 #00ecff 100\n" +
                "John NORTHERNPIKE 55 #de8798 88\n" +
                "Alyssa BLUETANG 42 #689fee 88\n" +
                "Zachary OCEANSUNFISH 232 #ffcdd1 22\n" +
                "Kayla GOLFISH 67 #ffcf00 82\n" +
                "Megan BLUETANG 70 #687fee 100\n");
            }
        } catch (IOException e) {
            alertService.errorAlert("Could not create default aquarium file " + settingService.defaultAquarium + ": " + e);
        }
    }

    /** Saves the current data of the aquarium to a file, with an optional encryption key.
     * Encryption is used unless the key is empty.
     * @param file file the data will be written to
     * @param key key the data will be encrypted with, or empty
     */
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

    /** Tries to load the data from a file into the aquarium, with an optional decryption key.
     * Assumes data is encrypted unless the key is empty.
     * @param file file that data will be read from
     * @param key key the data was encrypted with, or empty
     */
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
    private void loadDefault() {
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
