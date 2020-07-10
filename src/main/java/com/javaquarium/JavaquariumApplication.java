package com.javaquarium;

import com.javaquarium.backend.services.StageService;
import com.javaquarium.views.StartView;
import com.firework.EventSystem;
import com.firework.Firework;
import com.firework.Logger;
import com.firework.Services;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaquariumApplication extends Application {

    public static void actualMain(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Logger.setLogger(System.out::println);
        Firework.startServices();
        EventSystem.queueAutomaticEvent(Event.STAGEREADY, stage);
        Services.getService(StageService.class).setView(StartView.class);

        stage.setTitle("Javaquarium");
        stage.show();
    }
}
