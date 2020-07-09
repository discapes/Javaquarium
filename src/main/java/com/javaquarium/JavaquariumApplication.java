package com.javaquarium;

import com.javaquarium.backend.services.StageService;
import com.javaquarium.views.StartView;
import com.management.LawnMower;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaquariumApplication extends Application {

    public static void actualMain(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        LawnMower.setLOG(System.out::println);
        LawnMower.startServicesAndPresenters();
        LawnMower.queueAutomaticEvent(Event.STAGEREADY, stage);

        LawnMower.getService(StageService.class).setView(StartView.class);

        stage.setTitle("Javaquarium");
        stage.show();
    }
}
