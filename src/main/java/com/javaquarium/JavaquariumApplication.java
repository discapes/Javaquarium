package com.javaquarium;

import com.firework.Logger;
import com.firework.Services;
import com.firework.javafx.Theater;
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
        Theater.initTheaterAndStartFirework(stage);
        Services.getService(StageService.class).setView(StartView.class);

        stage.setTitle("Javaquarium");
        stage.show();

        /* load scenes in the background (OMG SO FAAST) */
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> Theater.buildScenes();
        executor.schedule(task, 500, TimeUnit.MILLISECONDS);
        executor.shutdown();
    }
}
