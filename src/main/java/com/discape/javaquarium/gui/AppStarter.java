package com.discape.javaquarium.gui;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.Utils;
import com.discape.javaquarium.business.model.Aquarium;
import com.discape.javaquarium.gui.app.AppView;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AppStarter {
    @Inject private Themes themes;

    //TODO re-center window
    public void start(Stage stage) {
        Aquarium aquarium = null;
        try {
            aquarium = Aquarium.fromString(new String(Files.readAllBytes(Paths.get("javaquarium/fish.txt"))));
        } catch (Exception e) { System.out.println("No valid default file fish.txt"); }
        if (aquarium == null) aquarium = new Aquarium();
        Injector.injectMembers(Aquarium.class, aquarium);
        Injector.setModelOrService(Aquarium.class, aquarium);
        ChartDataUpdater chartDataUpdater = (ChartDataUpdater) Injector.instantiateModelOrService(ChartDataUpdater.class);
        Stages stages = (Stages) Injector.instantiateModelOrService(Stages.class);

        stages.setAppStage(stage);
        aquarium.init();
        chartDataUpdater.init();

        stage.setOnCloseRequest((evt) -> {
            if (!Utils.confirm("Close?")) evt.consume();
        });
        Scene scene = new Scene(new AppView().getView());
        themes.setTheme(scene);
        stage.setScene(scene);
        stage.setTitle("Javaquarium");
    }
}
