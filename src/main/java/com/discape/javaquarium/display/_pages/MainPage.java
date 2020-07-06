package com.discape.javaquarium.display._pages;

import com.discape.javaquarium.display.Alerts;
import com.discape.javaquarium.display.app.AppView;
import com.discape.javaquarium.logic.Aquarium;
import com.discape.javaquarium.logic.AquariumFile;
import com.discape.javaquarium.logic.ChartDataUpdater;
import com.discape.javaquarium.logic.Session;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class MainPage extends Page {

    private final BooleanProperty hardReset = new SimpleBooleanProperty(true);
    @Inject private ChartDataUpdater chartDataUpdater;
    @Inject private Alerts alerts;
    @Inject private Session session;
    @Inject private AquariumFile aquariumFile;

    @PostConstruct void postConstruct() {
        session.addResetProperty(hardReset);
    }

    @Override
    public Parent getView() {
        if (hardReset.get()) {
            Aquarium aquarium = aquariumFile.load();

            aquarium.restartClock();
            chartDataUpdater.init(aquarium);
            hardReset.set(false);
        }
        return new AppView().getView();
    }

    @Override
    public void modifyStage(Stage stage) {
        stage.setTitle("Javaquarium");
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {
            if (!alerts.confirm("Close?")) e.consume();
        });
    }
}
