package com.firework;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

import static com.firework.MyLogger.getString;

/** JavaFX component integrated into Firework. */
public abstract class Theater {

    private static final HashMap<Class<?>, Object> presenters = new HashMap<>();
    private static final Logger logger = new MyLogger();
    private static final HashMap<Class<? extends View>, Scene> scenes = new HashMap<>();
    private static Stage primaryStage;

    /** Gets a prebuilt scene, or builds it and adds it to the list.
     * @param clazz view scene should be built for
     * @return a scene built from the view parameter
     * */
    public static Scene getScene(Class<? extends View> clazz) {
        logger.log("Reqested scene                " + clazz.getSimpleName());
        Scene scene = scenes.get(clazz);
        if (scene == null) scene = buildScene(clazz);
        return scene;
    }

    @SuppressWarnings("unchecked")
    static <T> T buildPresenterIfAbsent(Class<T> clazz) {
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

    private static String pkg;

    /** Initializes the Firework with support for JavaFX and start's building scenes in the background.
     * @param primaryStage stage to keep in store. Is only useful with getPrimaryStage()
     * @param pkg package where all of the @Service and @MainView classes are. */
    public static void initTheaterAndStartFireworkAndPreloadScenes(Stage primaryStage, String pkg) {
        Theater.pkg = pkg;
        Theater.primaryStage = primaryStage;
        Firework.startServices(pkg);
        /* load scenes in the background (OMG SO FAAST) */
        new Thread(Theater::buildScenes).start();
    }

    /** Returns the primary stage Theater was initialized with.
     * @return the primary stage Theater was initialized with
     * */
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

    /** Clears the list of scenes and starts rebuilding them in the background. */
    public static void startRebuildScenes() {
        scenes.clear();
        new Thread(Theater::buildScenes).start();
    }

    private static void buildScenes() {
        logger.log("Starting scenes...");
        Reflections reflections = new Reflections(pkg, new SubTypesScanner(false), new TypeAnnotationsScanner());
        Set<Class<? extends View>> sceneViewClasses = reflections.getSubTypesOf(View.class);
        sceneViewClasses.removeIf(clazz -> clazz.getAnnotation(SceneView.class) == null);

        sceneViewClasses.forEach(Theater::buildScene);
    }

    static <T> T instantiateClass(Class<T> clazz) {
        try {
            T o = clazz.getConstructor().newInstance();
            logger.log("Instantiated new object       " + getString(o));
            return o;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate a new " + clazz.getSimpleName(), e);
        }
    }
}
