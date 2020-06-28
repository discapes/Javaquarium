package com.discape.javaquarium.business;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.collections.ObservableList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
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
    @Inject private IntegerProperty tickRateMs;

    public Aquarium() {
        fishList = observableArrayList();
    }

    public Aquarium(ObservableList<Fish> fishList) {
        this.fishList = fishList;
    }

    private static void add(float val, ReadOnlyFloatWrapper wrapper) {
        wrapper.set(wrapper.get() + val);
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

    public void restartClock() {
        if (updateTask != null) updateTask.cancel();
        updateTask = new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        };
        timer.scheduleAtFixedRate(updateTask, tickRateMs.get(), tickRateMs.get());
    }

    @PostConstruct
    public void postConstruct() {
        tickRateMs.addListener((observable, oldValue, newValue) -> {
            restartClock();
        });
    }
}
