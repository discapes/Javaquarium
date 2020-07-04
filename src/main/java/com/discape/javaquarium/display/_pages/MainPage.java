package com.discape.javaquarium.display._pages;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.display.Alerts;
import com.discape.javaquarium.display.app.AppView;
import com.discape.javaquarium.logic.Aquarium;
import com.discape.javaquarium.logic.ChartDataUpdater;
import javafx.scene.Parent;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainPage extends Page {

    @Inject private ChartDataUpdater chartDataUpdater;
    @Inject private Alerts alerts;

    @Override
    public Parent getView() {
        Aquarium aquarium = null;
        try {
            aquarium = Aquarium.fromString(new String(Files.readAllBytes(Paths.get("javaquarium/fish.txt"))));
        } catch (Exception e) { System.out.println("No valid default file fish.txt"); }
        if (aquarium == null) aquarium = new Aquarium();
        Injector.injectMembers(Aquarium.class, aquarium);
        Injector.setModelOrService(Aquarium.class, aquarium);

        aquarium.init();
        chartDataUpdater.init(aquarium);

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
