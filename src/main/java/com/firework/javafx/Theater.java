package com.firework.javafx;

import com.firework.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.HashMap;
import java.util.Set;

import static com.firework.Services.instantiateClass;

public abstract class Theater {

    private static final HashMap<Class<? extends View>, Scene> scenes = new HashMap<>();
    private static Stage primaryStage;

    public static Scene getScene(Class<? extends View> clazz) {
        Logger.log("Reqested scene                " + clazz.getSimpleName());
        Scene scene = scenes.get(clazz);
        if (scene != null) Logger.log("EXISTING SCENE FOUND");
        if (scene == null) scene = buildScene(clazz);
        return scene;
    }

    public static <T> T buildPresenterIfAbsent(Class<T> clazz) {
        Logger.log("Requested presenter           " + clazz.getSimpleName());
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

    private static Scene buildScene(Class<? extends View> clazz) {
        Logger.log("Building scene                " + clazz.getSimpleName());
        View view = instantiateClass(clazz);
        Injector.injectDependencies(view);
        Scene scene = new Scene(view.getRoot());
        scenes.put(clazz, scene);
        return scene;
    }

    public static void buildScenes() {
        Logger.log("Starting scenes...");
        Reflections reflections = new Reflections("", new SubTypesScanner(false));

        Set<Class<? extends View>> viewClasses = reflections.getSubTypesOf(View.class);
        for (Class<? extends View> clazz : viewClasses) {
            buildScene(clazz);
        }
    }
}
