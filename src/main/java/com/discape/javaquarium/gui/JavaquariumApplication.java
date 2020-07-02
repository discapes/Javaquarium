package com.discape.javaquarium.gui;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.Utils;
import com.discape.javaquarium.gui.toolbar.Intro;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JavaquariumApplication extends Application {

    @Inject private Themes themes;

    public static void main(String[] args) {
        launch(args);
    }

    public static Map<String, Object> getDefaults() {
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("chartDataPoints", new SimpleIntegerProperty(200));
        defaults.put("chartHistoryS", new SimpleIntegerProperty(10));
        defaults.put("tickRateMs", new SimpleIntegerProperty(20));
        defaults.put("defaultTheme", "Dark");
        defaults.put("usersFile", System.getProperty("user.home") + "/.javaquariumusers");
        defaults.put("accountLine", new SimpleStringProperty("Guest N/A N/A"));
        return defaults;
    }

    @Override
    public void start(Stage stage) {
        Map<String, Object> customProperties = new HashMap<>(getDefaults());
        //noinspection SuspiciousMethodCalls
        Injector.setConfigurationSource(customProperties::get);
        String defaultTheme = (String) customProperties.get("defaultTheme");
        Injector.injectMembers(this.getClass(), this);

        try {
            //noinspection ResultOfMethodCallIgnored
            new File((String) customProperties.get("usersFile")).createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create users file: " + e.getMessage());
        }

        themes.setCurrentTheme(defaultTheme);
        Utils.setThemes(themes);

        ((Intro) Injector.instantiateModelOrService(Intro.class)).show(stage);
    }

    @Override
    public void stop() {
        Injector.forgetAll();
    }
}
