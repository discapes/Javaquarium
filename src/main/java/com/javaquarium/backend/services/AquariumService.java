package com.javaquarium.backend.services;

import com.javaquarium.Event;
import com.javaquarium.backend.Fish;
import com.javaquarium.backend.FishSpecies;
import com.javaquarium.backend.Settings;
import com.firework.OnEvent;
import com.firework.Service;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import static javafx.collections.FXCollections.observableArrayList;

@Service
public class AquariumService {

    private final ReadOnlyDoubleWrapper oxygen = new ReadOnlyDoubleWrapper(100);
    private final ReadOnlyDoubleWrapper food = new ReadOnlyDoubleWrapper(100);
    private final ObservableList<Fish> over0HPFish = observableArrayList();
    private final ObservableList<Fish> under100HPFish = observableArrayList();
    private final Timer timer = new Timer(true);
    private final ObservableList<Fish> fish = observableArrayList();
    private int ticks = 0;
    private double foodAddAmount = 0;
    private double oxygenAddAmount = 0;
    private TimerTask timerTask;

    public ObservableList<Fish> getFish() {
        return fish;
    }

    @SuppressWarnings("ConstantConditions")
    private void updateFishHealth() {
        if (fish.size() > 0)
            for (double amount : List.of(food.get(), oxygen.get())) {
                if (amount < 50 || amount > 150) {
                    if (over0HPFish.size() > 0) {
                        Fish fish = getRandomFish(over0HPFish);
                        if (fish.getHealth() == 100) under100HPFish.add(fish);
                        fish.setHealth(fish.getHealth() - (int) (Math.abs(100 - amount) * 0.2));

                        if (fish.getHealth() < 0) {
                            fish.setHealth(0);
                            over0HPFish.remove(fish);
                        }
                    }
                } else if (under100HPFish.size() > 0) {
                    Fish fish = getRandomFish(under100HPFish);
                    if (fish.getHealth() == 0) over0HPFish.add(fish);
                    fish.setHealth(fish.getHealth() + 5);

                    if (fish.getHealth() > 100) {
                        fish.setHealth(100);
                        under100HPFish.remove(fish);
                    }
                }
            }
    }

    private Fish getRandomFish(ObservableList<Fish> list) {
        if (list.size() == 0) return null;
        return list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
    }

    private void tick() {
        if (ticks % 10 == 5) oxygenAddAmount = ThreadLocalRandom.current().nextDouble(-1, 1);
        if (ticks % 10 == 0) foodAddAmount = ThreadLocalRandom.current().nextDouble(-1, 1);

        addOxygen(oxygenAddAmount);
        addFood(foodAddAmount);

        if (oxygen.get() < 0) { oxygen.set(0); }
        if (food.get() < 0) { food.set(0); }

        if (ticks % 100 == 0) updateFishHealth();

        ticks++;
    }

    @OnEvent(Event.LOGIN)
    private void startTimer() {
        assert timerTask == null;
        timerTask = newTimerTask();
        if (Settings.tickRate > 0) timer.scheduleAtFixedRate(timerTask, Settings.tickRate, Settings.tickRate);
    }

    @OnEvent(Event.TICKRATECHANGE)
    private void restartTimer() {
        stopTimer();
        startTimer();
    }

    private void stopTimer() {
        assert (timerTask != null);
        timerTask.cancel();
        timerTask = null;
    }

    @OnEvent(Event.LOGOUT)
    private void close() {
        stopTimer();

        oxygen.set(100);
        food.set(100);
        fish.clear();
    }

    private TimerTask newTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        };
    }

    public ReadOnlyDoubleProperty getOxygen() {
        return oxygen.getReadOnlyProperty();
    }

    public ReadOnlyDoubleProperty getFood() {
        return food.getReadOnlyProperty();
    }

    public void addOxygen(double amount) {
        oxygen.set(oxygen.get() + amount);
    }

    public void addFood(double amount) {
        food.set(food.get() + amount);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(oxygen.get()).append(" ").append(food.get()).append("\n");
        for (Fish fish : fish) {
            string.append(fish.toString()).append("\n");
        }
        return string.toString();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean loadFromString(String string) {
        ObservableList<Fish> loadedFish = observableArrayList();
        Scanner s = new Scanner(string);
        try {
            oxygen.set(Double.parseDouble(s.next()));
            food.set(Double.parseDouble(s.next()));
            while (s.hasNext()) {
                loadedFish.add(new Fish(s.next(),
                        FishSpecies.valueOf(s.next()),
                        Integer.parseInt(s.next()),
                        Color.web(s.next()),
                        Integer.parseInt(s.next())));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        fish.setAll(loadedFish);
        over0HPFish.setAll(loadedFish);
        return true;
    }
}
