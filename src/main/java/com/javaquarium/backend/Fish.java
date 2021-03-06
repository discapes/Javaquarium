package com.javaquarium.backend;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Data class of one fish.
 */
public class Fish {

    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleIntegerProperty speed = new SimpleIntegerProperty();
    private final SimpleObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<FishSpecies> species = new SimpleObjectProperty<>();
    private final SimpleIntegerProperty health = new SimpleIntegerProperty(100);

    public Fish(String name, FishSpecies species, int speed, Color color, int health) {
        this.health.set(health);
        this.name.set(name.replace('%', ' '));
        this.speed.set(speed);
        this.color.set(color);
        this.species.set(species);
    }

    /**
     * Creates a new fish, with the species being random, and the color and speed being random,
     * but influenced by the species's base speed and color.
     * @param name name of the fish to be added
     */
    public Fish(String name) {
        this.name.set(name);
        this.species.set(FishSpecies.values()[ThreadLocalRandom.current().nextInt(FishSpecies.values().length)]);
        this.speed.set((int) (ThreadLocalRandom.current().nextDouble(0.25f, 2.f) * species.get().getSpeed()));
        this.color.set(species.get().getColor().deriveColor(ThreadLocalRandom.current().nextDouble(-50, 50), 1, 1, 1));
    }

    /** Returns a string containing all of the fish's data.
     * @return a string that contains all of that fish's data
     * */
    @Override public String toString() {
        return name.get().replace(' ', '%') + " " + species.get().name() + " " + speed.get() + " " + Utils.colorToString(color.get()) + " " + health.get();
    }

    public SimpleStringProperty nameProperty() { return name; }
    public SimpleIntegerProperty speedProperty() { return speed; }
    public SimpleObjectProperty<Color> colorProperty() { return color; }
    public SimpleObjectProperty<FishSpecies> speciesProperty() { return species; }
    public SimpleIntegerProperty healthProperty() { return health; }
    public FishSpecies getSpecies() { return species.get(); }
    public void setSpecies(FishSpecies species) { this.species.set(species); }
    public Integer getHealth() { return health.get(); }
    public void setHealth(int health) { this.health.set(health); }
    public int getSpeed() { return speed.get(); }
    public void setSpeed(int speed) { this.speed.set(speed); }
    public Color getColor() { return color.get(); }
    public void setColor(Color color) { this.color.set(color); }
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
}
