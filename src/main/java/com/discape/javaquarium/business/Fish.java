package com.discape.javaquarium.business;

import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

// AAAAAAAAAAAAAAAAAAAAAAAA why cant i use kotlin this is so ugly

public class Fish {

    private final String name;
    private final int speed;
    private final Color color;
    private final FishSpecies species;
    private int saturation = 100;

    public Fish(String name, FishSpecies species, int speed, Color color, int saturation) {
        this.saturation = saturation;
        this.name = name;
        this.speed = speed;
        this.color = color;
        this.species = species;
    }

    public Fish(String name, FishSpecies species) {
        this.name = name;
        this.species = species;
        this.speed = (int) (ThreadLocalRandom.current().nextDouble(0.25f, 2.f) * species.getSpeed());
        this.color = species.getColor().deriveColor(ThreadLocalRandom.current().nextDouble(-50, 50), 1, 1, 1);
    }

    public int getSaturation() { return saturation; }

    public String getName() { return name; }

    public int getSpeed() { return speed; }

    public Color getColor() { return color; }

    public String getSpeciesName() { return species.getName(); }

    public FishSpecies getSpecies() { return species; }
}
