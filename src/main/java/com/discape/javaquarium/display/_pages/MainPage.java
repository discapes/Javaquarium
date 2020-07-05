package com.discape.javaquarium.display._pages;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.display.Alerts;
import com.discape.javaquarium.display.app.AppView;
import com.discape.javaquarium.logic.Aquarium;
import com.discape.javaquarium.logic.ChartDataUpdater;
import com.discape.javaquarium.logic.Session;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainPage extends Page {

    private final BooleanProperty hardReset = new SimpleBooleanProperty(true);
    @Inject private ChartDataUpdater chartDataUpdater;
    @Inject private Alerts alerts;
    @Inject private Session session;
    private Aquarium aquarium = null;

    public void setAquarium(Aquarium aquarium) {
        this.aquarium = aquarium;
        session.stop();
    }

    @Override
    public Parent getView() {
        if (hardReset.get()) {
            try {
                aquarium = Aquarium.fromString(new String(Files.readAllBytes(Paths.get("javaquarium/fish.txt"))));
            } catch (Exception e) { System.out.println("No valid default file fish.txt"); }
            if (aquarium == null) aquarium = new Aquarium();
            Injector.injectMembers(Aquarium.class, aquarium);
            aquarium.postConstruct();
            Injector.setModelOrService(Aquarium.class, aquarium);
            session.addResetProperty(hardReset);
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
