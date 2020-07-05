package com.discape.javaquarium.logic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import static javafx.collections.FXCollections.observableArrayList;

public class Aquarium {

    private final ReadOnlyFloatWrapper amountOxygen = new ReadOnlyFloatWrapper(100);
    private final ReadOnlyFloatWrapper amountFood = new ReadOnlyFloatWrapper(100);
    private final ObservableList<Fish> fishList;
    private final ObservableList<Fish> over0HPFish = observableArrayList();
    private final ObservableList<Fish> under100HPFish = observableArrayList();

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
        for (Fish fish : fishList) over0HPFish.add(fish);
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


        if (ticks % 100 == 0 && fishList.size() > 0) {
            for (Float amount : List.of(amountFood.get(), amountOxygen.get())) {
                if ((amount < 50 || amount > 150) && over0HPFish.size() > 0) {
                    Fish fish = over0HPFish.get(ThreadLocalRandom.current().nextInt(0, over0HPFish.size()));
                    if (fish.getHealth() == 100) under100HPFish.add(fish);
                    fish.setHealth(fish.getHealth() - (int) (Math.abs(100 - amount) * 0.2));
                    if (fish.getHealth() < 0) {
                        fish.setHealth(0);
                        over0HPFish.remove(fish);
                    }
                } else if (under100HPFish.size() > 0)  {
                    Fish fish = under100HPFish.get(ThreadLocalRandom.current().nextInt(0, under100HPFish.size()));
                    if (fish.getHealth() == 0) over0HPFish.add(fish);
                    fish.setHealth(fish.getHealth() + 5);
                    if (fish.getHealth() > 100) {
                        fish.setHealth(100);
                        under100HPFish.remove(fish);
                    }
                }
            }
        }

        ticks++;
    }

    public void increaseFood() {
        add(10, amountFood);
    }

    public void decreaseFood() {
        add(-10, amountFood);
        if (amountFood.get() < 0) { amountFood.set(0); }
    }

    public void increaseOxygen() {
        add(10, amountOxygen);
    }

    public void decreaseOxygen() {
        add(-10, amountOxygen);
        if (amountOxygen.get() < 0) { amountOxygen.set(0); }
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
