package com.discape.javaquarium.business.model;

import com.discape.javaquarium.Utils;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static javafx.collections.FXCollections.observableArrayList;

public abstract class AquariumFile {

    public static Aquarium getAquarium(File file) throws Exception {
        ObservableList<Fish> fishList = observableArrayList();

        Path path = file.toPath();

        for (String line : Files.readAllLines(path)) {
            String[] parts = line.split("\\s+");
            fishList.add(new Fish(
                    parts[0],
                    FishSpecies.valueOf(parts[1]),
                    Integer.parseInt(parts[2]),
                    Color.web(parts[3]),
                    Integer.parseInt(parts[4])));
        }
        return new Aquarium(fishList);
    }

    public static void setAquarium(Aquarium aquarium, File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (NullPointerException ignored) {
        } catch (FileNotFoundException e) {
            Utils.errorAlert(e.getMessage());
        }
        if (fileOutputStream == null) return;
        PrintWriter pw = new PrintWriter(fileOutputStream);

        pw.print(aquarium.toString());

        pw.close();
    }

}