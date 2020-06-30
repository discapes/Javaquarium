package com.discape.javaquarium.gui;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.Utils;
import com.discape.javaquarium.business.Aquarium;
import com.discape.javaquarium.business.AquariumFile;
import com.discape.javaquarium.gui.app.AppView;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

//TODO inspect code

public class JavaquariumApplication extends Application {

    @Inject private Themes themes;
    @Inject private ChartDataUpdater chartDataUpdater;
    @Inject private Stages stages;
    @Inject private Aquarium aquarium;

    public static void main(String[] args) {
        launch(args);
    }

    public static Map<String, Object> getDefaults() {
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("chartDataPoints", new SimpleIntegerProperty(200));
        defaults.put("chartHistoryS", new SimpleIntegerProperty(10));
        defaults.put("tickRateMs", new SimpleIntegerProperty(20));
        defaults.put("defaultTheme", "Dark");
        return defaults;
    }

    @Override
    public void start(Stage stage) {
        // set configuration
        Map<String, Object> customProperties = new HashMap<>(getDefaults());
        //noinspection SuspiciousMethodCalls
        Injector.setConfigurationSource(customProperties::get);
        String defaultTheme = (String) customProperties.get("defaultTheme");

        Aquarium aquarium = null;
        try {
            aquarium = AquariumFile.getAquarium(new File("javaquarium/fish.txt"));
        } catch (Exception e) { System.out.println("No valid default file fish.txt"); }
        if (aquarium == null) aquarium = new Aquarium();
        Injector.injectMembers(Aquarium.class, aquarium);
        Injector.setModelOrService(Aquarium.class, aquarium);

        Injector.injectMembers(this.getClass(), this);

        themes.setCurrentTheme(defaultTheme);
        Utils.setThemes(themes);
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
        stage.show();
    }

    @Override
    public void stop() {
        Injector.forgetAll();
    }
}
