package com.discape.javaquarium.backend.aquarium;

import com.discape.javaquarium.backend.Fish;
import com.discape.javaquarium.backend.FishSpecies;
import com.discape.javaquarium.backend.Logger;
import com.discape.javaquarium.backend.events.Events;
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

/**
 * This class contains and updates all of data of the Aquarium,
 * and all data that is used by the GUI is gotten from here.
 */
public class Aquarium {

    private final ReadOnlyFloatWrapper oxygen = new ReadOnlyFloatWrapper(100);
    /**
     * Returns a read only property of the amount of oxygen in the aquarium.
     */
    public ReadOnlyFloatProperty getOxygen() { return oxygen.getReadOnlyProperty(); }
    /** Adds to the amount of oxygen. */
    public void plusOxygen() { add(10, oxygen); }
    /** Removes 10 from the amount of oxygen. */
    public void minusOxygen() { add(-10, oxygen);if (oxygen.get() < 0) { oxygen.set(0); } }

    private final ReadOnlyFloatWrapper food = new ReadOnlyFloatWrapper(100);
    /**
     * Returns a read only property of the amount of food in the aquarium.
     */
    public ReadOnlyFloatProperty getFood() { return food.getReadOnlyProperty(); }
    /** Adds to the amount of food. */
    public void plusFood() { add(10, food); }
    /** Removes 10 from the amount of food. */
    public void minusFood() { add(-10, food); if (food.get() < 0) { food.set(0); } }

    private final ObservableList<Fish> fish;
    /**
     * Returns list of all the fish.
     */
    public ObservableList<Fish> getFish() { return fish; }
    private final ObservableList<Fish> filteredFish = observableArrayList();
    /**
     * Returns the filtered list that the TableView should display.
     */
    public ObservableList<Fish> getFilteredFish() { return filteredFish; }

    /** Aquarium with no fish. */
    public Aquarium() {
        Logger.log("Aquarium", "Aquarium()");
        this.fish = observableArrayList();
        addListeners();
    }


    /**
     * Aquarium from a string that contains all of the fish.
     * @throws IndexOutOfBoundsException string is invalid.
     * */
    public Aquarium(String string) throws IndexOutOfBoundsException {
        Logger.log("Aquarium", "Aquarium(String)");
        fish = observableArrayList();
        Scanner scanner = new Scanner(string);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            fish.add(new Fish(
                    parts[0],
                    FishSpecies.valueOf(parts[1]),
                    Integer.parseInt(parts[2]),
                    Color.web(parts[3]),
                    Integer.parseInt(parts[4])));
        }
        over0HPFish.addAll(fish);
        addListeners();
    }

    private void addListeners() {
        Events.TICKRATECHANGE.e().addListener(v -> restartClock());
        Events.NEWAQUARIUM.e().addListener(aquarium -> {
            if (aquarium == this) restartClock();
            else {
                clockTask.cancel();
                Logger.log(this, "clockTask.cancel()");
            }
        });
    }

    /* So we only decrease HP from fish that are over 0 HP */
    private final ObservableList<Fish> over0HPFish = observableArrayList();
    /* So we only increase HP on fish that are under 100 HP */
    private final ObservableList<Fish> under100HPFish = observableArrayList();

    /** Updates the health of a fish depending on the amount of oxygen and food. */
    private void updateFishHealth() {
        if (fish.size() > 0)
            for (Float amount : List.of(food.get(), oxygen.get()))
                if ((amount < 50 || amount > 150) && over0HPFish.size() > 0) {
                    Fish fish = over0HPFish.get(ThreadLocalRandom.current().nextInt(0, over0HPFish.size()));
                    if (fish.getHealth() == 100) under100HPFish.add(fish);
                    fish.setHealth(fish.getHealth() - (int) (Math.abs(100 - amount) * 0.2));

                    if (fish.getHealth() < 0) {
                        fish.setHealth(0);
                        over0HPFish.remove(fish);
                    }

                } else if (under100HPFish.size() > 0) {
                    Fish fish = under100HPFish.get(ThreadLocalRandom.current().nextInt(0, under100HPFish.size()));
                    if (fish.getHealth() == 0) over0HPFish.add(fish);
                    fish.setHealth(fish.getHealth() + 5);

                    if (fish.getHealth() > 100) {
                        fish.setHealth(100);
                        under100HPFish.remove(fish);
                    }
                }
    }

    private int ticks = 0;
    private float foodAddAmount = 0;
    private float oxygenAddAmount = 0;
    private void tick() {
        { /* every ten ticks we change the direction of the line in the chart */
            if (ticks % 10 == 5)
                oxygenAddAmount = (float) ThreadLocalRandom.current().nextDouble(-1, 1);
            add(oxygenAddAmount, oxygen);
            if (oxygen.get() < 0) { oxygen.set(0); }

            if (ticks % 10 == 0)
                foodAddAmount = (float) ThreadLocalRandom.current().nextDouble(-1, 1);
            add(foodAddAmount, food);

            if (food.get() < 0) { food.set(0); }
        }

        if (ticks % 100 == 0)
            updateFishHealth();

        ticks++;
    }

    private final Timer timer = new Timer(true);
    private TimerTask clockTask = null;
    /** Threads are added to sessionManager, as running tasks logically are part of a sessionManager. */
    /** Tick rate in milliseconds, changed by settings. */
    @Inject private IntegerProperty tickRate;
    /** Starts/Restarts the clock, running once per tickRate ms.
     * @return The scheduled TimerTask.
     * */
    public void restartClock() {
        Logger.log(this, "restartClock()");
        if (clockTask != null) clockTask.cancel();
        clockTask = new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        };
        if (tickRate.get() > 0) timer.scheduleAtFixedRate(clockTask, tickRate.get(), tickRate.get());
    }

    @Override public String toString() {
        StringBuilder string = new StringBuilder();
        for (Fish fish : fish) {
            string.append(fish.toString()).append("\n");
        }
        return string.toString();
    }

    private static void add(float val, ReadOnlyFloatWrapper wrapper) { wrapper.set(wrapper.get() + val); }
}
