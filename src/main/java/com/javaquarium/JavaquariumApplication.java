package com.javaquarium;

import com.firework.Services;
import com.firework.javafx.Theater;
import com.javaquarium.backend.services.StageService;
import com.javaquarium.views.StartView;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaquariumApplication extends Application {

    public static void actualMain(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Theater.initTheaterAndStartFirework(stage);
        Services.getService(StageService.class).setView(StartView.class);

        stage.setTitle("Javaquarium");
        stage.show();
    }
}
