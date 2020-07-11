package com.javaquarium.views;

import com.firework.View;
import javafx.scene.Parent;

import java.util.function.Consumer;

/** Simple view with no presenter. */
public abstract class StandaloneView extends View {

    @Override public abstract Parent getRoot();

    @Override
    public void getRootAsync(Consumer<Parent> consumer) {
        throw new IllegalStateException("This view cannot and should not be loaded asynchronously.");
    }
}
