package com.javaquarium;

import com.javaquarium.backend.services.AccountService;
import com.management.LawnMower;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaquariumApplication extends Application {

    public static void actualMain(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) {
        LawnMower.setLOG(s -> System.out.println(s));
        LawnMower.startServicesAndPresenters();
        LawnMower.queueAutomaticEvent(Event.STAGEREADY, stage);

        LawnMower.getService(AccountService.class).loginAsGuest();
        stage.setTitle("Javaquarium");
        stage.show();
    }
}
