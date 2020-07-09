package com.javaquarium;

import com.javaquarium.views.simple.SimpleView;
import com.management.LawnMower;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaquariumApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) {
        LawnMower.setLOG(s -> System.out.println(s));
        LawnMower.startServicesAndPresenters();
        LawnMower.queueAutomaticEvent(Event.INIT);
        stage.setScene(LawnMower.getScene(SimpleView.class));
        stage.setTitle("Javaquarium");
        stage.show();
    }
}
