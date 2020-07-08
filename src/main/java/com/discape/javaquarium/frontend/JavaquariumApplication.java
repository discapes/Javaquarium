package com.discape.javaquarium.frontend;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.backend.aquarium.Aquarium;
import com.discape.javaquarium.backend.events.Events;
import com.discape.javaquarium.backend.listeners.AquariumLoader;
import com.discape.javaquarium.backend.listeners.ChartDataUpdater;
import com.discape.javaquarium.frontend.persistent.IViewSetter;
import com.discape.javaquarium.frontend.persistent.LazyViewSetter;
import com.discape.javaquarium.frontend.views.StartView;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class JavaquariumApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static Map<String, Object> getDefaults() {
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("chartNumData", new SimpleIntegerProperty(200));
        defaults.put("chartHistory", new SimpleIntegerProperty(10));
        defaults.put("tickRate", new SimpleIntegerProperty(20));
        defaults.put("defaultFile", System.getProperty("user.home") + "/.javaquariumdefault.txt");
        return defaults;
    }

    @Override
    public void start(Stage stage) {
      //  Thread.setDefaultUncaughtExceptionHandler((t, e) -> {});
        for (Events e : Events.values()) {
            e.e().addListener(p -> System.out.println(e.name()));
        }
        Injector.setModelOrService(Stage.class, stage);

        Map<String, Object> customProperties = new HashMap<>(getDefaults());
        Injector.setConfigurationSource(customProperties::get);

        IViewSetter viewSetter = new LazyViewSetter();
        Injector.injectMembers(viewSetter.getClass(), viewSetter);
        Injector.setModelOrService(IViewSetter.class, viewSetter);
        Injector.instantiateModelOrService(AquariumLoader.class);
        Injector.instantiateModelOrService(ChartDataUpdater.class);

        viewSetter.applyView(StartView.class);
        stage.show();
    }

    @Override
    public void stop() {
        Injector.forgetAll();
    }
}
