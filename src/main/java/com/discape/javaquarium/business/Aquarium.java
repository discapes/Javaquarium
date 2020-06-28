package com.discape.javaquarium.business;

import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import static javafx.collections.FXCollections.observableArrayList;

public class Aquarium {

    private final ReadOnlyFloatWrapper amountOxygen = new ReadOnlyFloatWrapper();
    private final ReadOnlyFloatWrapper amountFood = new ReadOnlyFloatWrapper();
    private final ObservableList<Fish> fishList;

    public Aquarium() {
        fishList = observableArrayList();
    }

    public Aquarium(ObservableList<Fish> fishList)  {
        this.fishList = fishList;
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

    private int ticks = 0;
    private float foodAddAmount = 0;
    private float oxygenAddAmount = 0;
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

    @Inject private Integer tickRateMs;

    public void startClock() {
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        };
        java.util.Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(updateTask, tickRateMs, tickRateMs);
    }

    private static void add(float val, ReadOnlyFloatWrapper wrapper) {
        wrapper.set(wrapper.get() + val);
    }
}
