package com.javaquarium;

import com.firework.Firework;
import com.firework.Theater;
import javafx.application.Application;
import javafx.stage.Stage;

/** The main JavaFX Application class. */
public class JavaquariumApplication extends Application {

    /** Called from Javaquarium.
     * @param args command line arguments */
    public static void actualMain(String[] args) {
        launch(args);
    }

    /** Starts the application. */
    @Override public void start(Stage stage) {
        Theater.initTheaterAndStartFireworkAndPreloadScenes(stage, "com.javaquarium");
        Firework.queueAutomaticEvent(Events.STARTVIEW);
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {});

        stage.setTitle("Javaquarium");
        stage.show();
    }
}
