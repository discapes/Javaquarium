package com.javaquarium;

import com.firework.EventSystem;
import com.firework.Firework;
import com.firework.Services;
import com.firework.Theater;
import com.javaquarium.backend.services.StageService;
import com.javaquarium.views.StartView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JavaquariumApplication extends Application {

    public static void actualMain(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) {
        Theater.initTheaterAndStartFireworkAndPreloadScenes(stage, 0);
        EventSystem.queueAutomaticEvent(Events.STARTVIEW);

        stage.setTitle("Javaquarium");
        stage.show();
    }
}
