package com.firework;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

import static com.firework.Services.getString;

public abstract class Theater {

    private static final HashMap<Class<?>, Object> presenters = new HashMap<>();
    private static final Logger logger = new MyLogger();
    private static final HashMap<Class<? extends View>, Scene> scenes = new HashMap<>();
    private static Stage primaryStage;

    public static Scene getScene(Class<? extends View> clazz) {
        logger.log("Reqested scene                " + clazz.getSimpleName());
        Scene scene = scenes.get(clazz);
        if (scene == null) scene = buildScene(clazz);
        return scene;
    }

    @SuppressWarnings("unchecked")
    public static <T> T buildPresenterIfAbsent(Class<T> clazz) {
        logger.log("Requested presenter           " + clazz.getSimpleName());
        if (presenters.containsValue(clazz)) return (T) presenters.get(clazz);
        T presenter = Services.getService(clazz);
        if (presenter == null) {
            presenter = instantiateClass(clazz);
            Injector.injectDependencies(presenter);
        } else {
            logger.log("Presenter found as service");
        }
        presenters.put(clazz, presenter);
        return presenter;
    }

    public static void initTheaterAndStartFireworkAndPreloadScenes(Stage primaryStage) {
        Theater.primaryStage = primaryStage;
        Firework.startServices();
        /* load scenes in the background (OMG SO FAAST) */
        new Thread(Theater::buildScenes).start();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static Scene buildScene(Class<? extends View> clazz) {
        logger.log("Building scene                " + clazz.getSimpleName());
        View view = instantiateClass(clazz);
        Injector.injectDependencies(view);
        Scene scene = new Scene(view.getRoot());
        scenes.put(clazz, scene);
        return scene;
    }

    public static void startRebuildScenes() {
        scenes.clear();
        new Thread(Theater::buildScenes).start();
    }

    public static void buildScenes() {
        logger.log("Starting scenes...");
        Reflections reflections = new Reflections("", new SubTypesScanner(false), new TypeAnnotationsScanner());
        Set<Class<? extends View>> sceneViewClasses = reflections.getSubTypesOf(View.class);
        sceneViewClasses.removeIf(clazz -> clazz.getAnnotation(SceneView.class) == null);

        sceneViewClasses.forEach(Theater::buildScene);
    }

    public static <T> T instantiateClass(Class<T> clazz) {
        try {
            T o = clazz.getConstructor().newInstance();
            logger.log("Instantiated new object       " + getString(o));
            return o;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate a new " + clazz.getSimpleName(), e);
        }
    }
}
