package com.discape.javaquarium.business;

import com.discape.javaquarium.Utils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

public class Fish {

    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleIntegerProperty speed = new SimpleIntegerProperty();
    private final SimpleObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<FishSpecies> species = new SimpleObjectProperty<>();
    private final SimpleIntegerProperty saturation = new SimpleIntegerProperty(100);

    public Fish(String name, FishSpecies species, int speed, Color color, int saturation) {
        this.saturation.set(saturation);
        this.name.set(name);
        this.speed.set(speed);
        this.color.set(color);
        this.species.set(species);
    }

    public Fish(String name, FishSpecies species) {
        this.name.set(name);
        this.species.set(species);
        this.speed.set((int) (ThreadLocalRandom.current().nextDouble(0.25f, 2.f) * species.getSpeed()));
        this.color.set(species.getColor().deriveColor(ThreadLocalRandom.current().nextDouble(-50, 50), 1, 1, 1));
    }

    @Override
    public String toString() {
        return name.get() + " " + species.get().name() + " " + speed.get() + " " + Utils.colorToString(color.get()) + " " + saturation.get();
    }

    public SimpleStringProperty nameProperty() { return name; }

    public SimpleIntegerProperty speedProperty() { return speed; }

    public SimpleObjectProperty<Color> colorProperty() { return color; }

    public SimpleObjectProperty<FishSpecies> speciesProperty() { return species; }

    public SimpleIntegerProperty saturationProperty() { return saturation; }

    public Integer getSaturation() { return saturation.get(); }

    public void setSaturation(int saturation) { this.saturation.set(saturation); }

    public String getName() { return name.get(); }

    public void setName(String name) { this.name.set(name); }

    public Integer getSpeed() { return speed.get(); }

    public void setSpeed(int speed) { this.speed.set(speed); }

    public Color getColor() { return color.get(); }

    public void setColor(Color color) { this.color.set(color); }

    public FishSpecies getSpecies() { return species.get(); }

    public void setSpecies(FishSpecies species) { this.species.set(species); }
}
