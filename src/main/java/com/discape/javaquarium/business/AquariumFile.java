package com.discape.javaquarium.business;

import com.discape.javaquarium.Utils;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javafx.collections.FXCollections.observableArrayList;

abstract class AquariumFile {

    static Aquarium getAquarium(String fileName) throws IOException {
        ObservableList<Fish> fishList = observableArrayList();

        Path file = Paths.get(fileName);

        for (String line : Files.readAllLines(file)) {
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

    static void setAquarium(Aquarium aquarium, String fileName) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));

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
