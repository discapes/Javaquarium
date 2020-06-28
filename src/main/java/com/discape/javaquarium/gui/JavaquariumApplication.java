package com.discape.javaquarium.gui;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.business.Aquarium;
import com.discape.javaquarium.business.AquariumFile;
import com.discape.javaquarium.gui.app.AppView;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//TODO when writing about dialog, mention that higher update speeds make more lag
//TODO inspect code

public class JavaquariumApplication extends Application {

    @Inject private IThemes themes;
    @Inject private Aquarium aquarium;
    @Inject private Stages stages;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Injector.setModelOrService(Aquarium.class, AquariumFile.getAquarium(new File("fish.txt")));
        } catch (IOException e) { System.out.println("No default file fish.txt"); }
        Map<String, Object> customProperties = new HashMap<>();
        Injector.setConfigurationSource(customProperties::get);
        customProperties.put("chartUpdateRateMs", new SimpleIntegerProperty(50));
        customProperties.put("chartHistoryS", new SimpleIntegerProperty(10));
        customProperties.put("tickRateMs", new SimpleIntegerProperty(20));
        Injector.setModelOrService(IThemes.class, new Themes());
        Injector.injectMembers(getClass(), this);
        Injector.injectMembers(Aquarium.class, aquarium);
        aquarium.postConstruct();

        themes.setCurrentTheme("Dark");

        stages.setAppStage(stage);
        Scene scene = new Scene(new AppView().getView());
        stage.setTitle("Javaquarium");
        stage.setScene(themes.setTheme(scene));
        stage.show();
        aquarium.restartClock();
    }

    @Override
    public void stop() {
        Injector.forgetAll();
    }
}
