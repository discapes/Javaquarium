package com.firework.javafx;

import com.firework.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

import static com.firework.Services.instantiateClass;

public abstract class Theater {

    private static final HashMap<Class<? extends View>, Scene> scenes = new HashMap<>();
    private static Stage primaryStage;

    public static Scene getScene(Class<? extends View> clazz) {
        Logger.log("Reqested scene                " + clazz.getSimpleName());
        View view = instantiateClass(clazz);
        Injector.injectDependencies(view);
        return scenes.computeIfAbsent(clazz, key -> new Scene(view.getRoot()));
    }

    public static <T> T buildPresenterIfAbsent(Class<T> clazz) {
        T presenter = Services.getService(clazz);
        if (presenter == null) {
            presenter = instantiateClass(clazz);
            Injector.injectDependencies(presenter);
        }
        return presenter;
    }

    public static void initTheaterAndStartFirework(Stage primaryStage) {
        Theater.primaryStage = primaryStage;
        Firework.startServices();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
