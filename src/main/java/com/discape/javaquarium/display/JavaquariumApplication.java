package com.discape.javaquarium.display;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.display._pages.LoginRegisterPage;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class JavaquariumApplication extends Application {

    @Inject private StageUtilities stageUtilities;
    @Inject private LoginRegisterPage loginRegisterPage;

    public static void main(String[] args) {
        launch(args);
    }

    public static Map<String, Object> getDefaults() {
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("chartNumData", new SimpleIntegerProperty(200));
        defaults.put("chartHistory", new SimpleIntegerProperty(10));
        defaults.put("tickRate", new SimpleIntegerProperty(20));
        return defaults;
    }

    @Override
    public void start(Stage stage) {
        Injector.setModelOrService(Stage.class, stage);

        Map<String, Object> customProperties = new HashMap<>(getDefaults());
        //noinspection SuspiciousMethodCalls
        Injector.setConfigurationSource(customProperties::get);

        Injector.injectMembers(this.getClass(), this);

        stageUtilities.setPage(loginRegisterPage);
        stage.show();
    }

    @Override
    public void stop() {
        Injector.forgetAll();
    }
}
