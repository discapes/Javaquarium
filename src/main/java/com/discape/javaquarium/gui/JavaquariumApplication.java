package com.discape.javaquarium.gui;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.Utils;
import com.discape.javaquarium.business.Aquarium;
import com.discape.javaquarium.business.AquariumFile;
import com.discape.javaquarium.gui.app.AppView;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//TODO when writing about dialog, mention that higher update speeds make more lag actually just set max numcategories
//TODO inspect code

public class JavaquariumApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // set configuration
        Map<String, Object> customProperties = new HashMap<>();
        Injector.setConfigurationSource(customProperties::get);
        customProperties.put("chartUpdateRateMs", new SimpleIntegerProperty(50));
        customProperties.put("chartHistoryS", new SimpleIntegerProperty(10));
        customProperties.put("tickRateMs", new SimpleIntegerProperty(20));
        String defaultTheme = "Metro Dark";


        // instantiate objects that have non-default constructors, or implement interfaces
        // first create all objects, then inject their dependencies
        Aquarium aquarium = null;
        try {
            aquarium = AquariumFile.getAquarium(new File("fish.txt"));
        } catch (Exception e) { System.out.println("No valid default file fish.txt"); }
        if (aquarium == null) aquarium = new Aquarium();
        Injector.setModelOrService(Aquarium.class, aquarium);

        ChartDataUpdater chartDataUpdater = new ChartDataUpdater();
        Injector.setModelOrService(IChartDataUpdater.class, chartDataUpdater);

        Themes themes = new Themes();
        themes.setCurrentTheme(defaultTheme);
        Injector.setModelOrService(IThemes.class, themes);

        Stages stages = new Stages();
        stages.setAppStage(stage);
        Injector.setModelOrService(Stages.class, stages);

        Injector.injectMembers(Aquarium.class, aquarium);
        Injector.injectMembers(ChartDataUpdater.class, chartDataUpdater);
        Injector.injectMembers(Themes.class, themes);
        Injector.injectMembers(Stages.class, stages);
        Utils.setThemes(themes);

        aquarium.init();
        chartDataUpdater.init();

        stage.setOnCloseRequest((evt) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Close?", ButtonType.YES, ButtonType.NO);
            themes.setTheme(alert.getDialogPane().getScene());
            ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
            if (result == ButtonType.NO) evt.consume();
        });
        Scene scene = new Scene(new AppView().getView());
        themes.setTheme(scene);
        stage.setScene(scene);
        stage.setTitle("Javaquarium");
        stage.show();
    }

    @Override
    public void stop() {
        Injector.forgetAll();
    }
}
