package com.javaquarium.backend;

import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.*;

/**
 * An enum containing all the possible species of fish, their base speeds and base colors.
 */
public enum FishSpecies {
    BLUETANG(100, MEDIUMSLATEBLUE, "Blue tang"),
    COD(120, TAN, "Cod"),
    GOLFISH(70, ORANGERED, "Goldfish"),
    MAHIMAHI(200, SEAGREEN, "Mahi-Mahi"),
    OCEANSUNFISH(150, BLANCHEDALMOND, "Ocean Sunfish"),
    CLOWNFISH(25, ORANGE, "Clownfish"),
    REDSNAPPER(100, INDIANRED, "Red Snapper"),
    NEONTETRA(170, AQUA, "Neon Tetra"),
    NORTHERNPIKE(50, BURLYWOOD, "Northern Pike");

    private final int speed;
    private final Color color;
    private final String name;

    FishSpecies(int speed, Color color, String name) {
        this.speed = speed;
        this.color = color;
        this.name = name;
    }

    /** Returns the base speed of a species.
     * @return base speed of the species
     */
    public int getSpeed() { return speed; }

    /** Returns the base color of a species.
     * @return base color of the species
     * */
    public Color getColor() { return color; }

    /** Returns a pretty string of the name of the fish that might contain spaces.
     * If you want to write to a configuration use name().
     */
    @Override public String toString() {
        return name;
    }
}
