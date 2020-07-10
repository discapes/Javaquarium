package com.javaquarium;

import com.firework.EventSystem;
import com.firework.Theater;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaquariumApplication extends Application {

    public static void actualMain(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Theater.initTheaterAndStartFireworkAndPreloadScenes(stage);
        EventSystem.queueAutomaticEvent(Events.STARTVIEW);

        stage.setTitle("Javaquarium");
        stage.show();
    }
}
