package com.discape.javaquarium.gui;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.business.Aquarium;
import com.discape.javaquarium.gui.app.AppView;
import com.discape.javaquarium.gui.settings.SettingsView;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static javafx.collections.FXCollections.observableArrayList;

public class JavaquariumApplication extends Application {

    @Inject private IThemes themes;
    @Inject private Aquarium aquarium;
    @Inject private Stages stages;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Map<String, Object> customProperties = new HashMap<>();
        Injector.setConfigurationSource(customProperties::get);
        customProperties.put("chartUpdateRateMs", new SimpleIntegerProperty(100));
        customProperties.put("chartHistoryS", new SimpleIntegerProperty(10));
        customProperties.put("tickRateMs", 20);
        Injector.setModelOrService(IThemes.class, new Themes());
        Injector.injectMembers(getClass(), this);

        themes.setCurrentTheme("Dark");

        stages.setAppStage(stage);
        Scene scene = new Scene(new AppView().getView());
        stage.setTitle("Javaquarium");
        stage.setScene(themes.setTheme(scene));
        stage.show();
        aquarium.startClock();
    }

    @Override
    public void stop() {
        Injector.forgetAll();
    }
}
