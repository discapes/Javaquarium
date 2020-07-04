package com.discape.javaquarium.logic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import static javafx.collections.FXCollections.observableArrayList;

public class Aquarium {

    private final ReadOnlyFloatWrapper amountOxygen = new ReadOnlyFloatWrapper();
    private final ReadOnlyFloatWrapper amountFood = new ReadOnlyFloatWrapper();
    private final ObservableList<Fish> fishList;
    private final Timer timer = new Timer(true);
    private TimerTask updateTask = null;
    private int ticks = 0;
    private float foodAddAmount = 0;
    private float oxygenAddAmount = 0;
    @Inject private IntegerProperty tickRate;
    @Inject private Session session;

    public Aquarium() {
        this(observableArrayList());
    }

    public Aquarium(ObservableList<Fish> fishList) {
        this.fishList = fishList;
    }

    private static void add(float val, ReadOnlyFloatWrapper wrapper) {
        wrapper.set(wrapper.get() + val);
    }

    public static Aquarium fromString(String string) {
        ObservableList<Fish> fishList = observableArrayList();

        Scanner scanner = new Scanner(string);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
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

    public void postConstruct() {
        tickRate.addListener((observable, oldValue, newValue) -> restartClock());
    }

    public void tick() {
        if (ticks % 10 == 5)
            oxygenAddAmount = (float) ThreadLocalRandom.current().nextDouble(-1, 1);
        add(oxygenAddAmount, amountOxygen);

        if (ticks % 10 == 0)
            foodAddAmount = (float) ThreadLocalRandom.current().nextDouble(-1, 1);

        add(foodAddAmount, amountFood);

        if (amountOxygen.get() < 0) { amountOxygen.set(0); }
        if (amountFood.get() < 0) { amountFood.set(0); }

        ticks++;
    }

    public void increaseFood() {
        add(10, amountFood);
    }

    public void decreaseFood() {
        add(-10, amountFood);
    }

    public void increaseOxygen() {
        add(10, amountOxygen);
    }

    public void decreaseOxygen() {
        add(-10, amountOxygen);
    }

    public ObservableList<Fish> getFishList() { return fishList; }

    public ReadOnlyFloatProperty getAmountFood() { return amountFood.getReadOnlyProperty(); }

    public ReadOnlyFloatProperty getAmountOxygen() { return amountOxygen.getReadOnlyProperty(); }

    public void restartClock() {
        if (updateTask != null) updateTask.cancel();
        updateTask = new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        };
        session.addTask(updateTask);
        if (tickRate.get() > 0) timer.scheduleAtFixedRate(updateTask, tickRate.get(), tickRate.get());
    }

    @Override
    public String toString() {
        String string = "";
        for (Fish fish : fishList) {
            string = string.concat(fish.toString() + "\n");
        }
        return string;
    }
}
