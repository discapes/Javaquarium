package com.javaquarium.backend;

import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.PALEGOLDENROD;

/**
 * An enumerator with all the possible species of fish, their base speeds and base colors
 */
public enum FishSpecies {
    BLUETANG(100, BLUE, "Blue tang"),
    COD(120, PALEGOLDENROD, "Cod");

    private final int speed;
    private final Color color;
    private final String name;

    FishSpecies(int speed, Color color, String name) {
        this.speed = speed;
        this.color = color;
        this.name = name;
    }

    public int getSpeed() { return speed; }

    public Color getColor() { return color; }

    @Override
    public String toString() {
        return name;
    }
}
