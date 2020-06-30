package com.discape.javaquarium.business;

import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.PALEGOLDENROD;

@SuppressWarnings("unused")
public enum FishSpecies {
    BLUETANG(0, 100, BLUE, "Blue tang"),
    COD(1, 120, PALEGOLDENROD, "Cod");

    private final int value;
    private final int speed;
    private final Color color;
    private final String name;

    FishSpecies(int value, int speed, Color color, String name) {
        this.value = value;
        this.speed = speed;
        this.color = color;
        this.name = name;
    }

    public int getValue() { return value; }

    public int getSpeed() { return speed; }

    public Color getColor() { return color; }

    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }
}
