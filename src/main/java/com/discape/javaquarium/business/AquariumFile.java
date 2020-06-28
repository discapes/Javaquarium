package com.discape.javaquarium.business;

import com.discape.javaquarium.Utils;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javafx.collections.FXCollections.observableArrayList;

public abstract class AquariumFile {

    public static Aquarium getAquarium(File file) throws IOException {
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

    public static void setAquarium(Aquarium aquarium, File file) throws FileNotFoundException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (NullPointerException e) {}
        if (fileOutputStream == null) return;
        PrintWriter pw = new PrintWriter(fileOutputStream);

        for (Fish fish : aquarium.getFishList()) {
            pw.print(fish.getName() + " ");
            pw.print(fish.getSpecies().name() + " ");
            pw.print(fish.getSpeed() + " ");
            pw.print(Utils.colorToString(fish.getColor()) + " ");
            pw.print(fish.getSaturation() + " ");

            pw.println();
        }
        pw.close();
    }

}
