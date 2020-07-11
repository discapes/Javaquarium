package com.firework;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static com.firework.MyLogger.getString;

/** Is a class that represents one "view". */
public abstract class View extends StackPane {

    private final static Logger logger = new MyLogger();

    private final static ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool((r) -> {
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        String name = thread.getName();
        thread.setName("view-loader-" + name);
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> {});
        return thread;
    });

    /** Loads the conventionally named FXML file and applies CSS.
     * Presenters are gotten by Theater.
     */
    public Parent getRoot() {
        logger.log("getRoot called in             " + getString(this));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getResourceName(".fxml")));
        fxmlLoader.setControllerFactory(Theater::buildPresenterIfAbsent);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        Parent parent = fxmlLoader.getRoot();
        URL cssURI = getClass().getResource(getResourceName(".css"));
        if (cssURI != null) {
            String uriToCss = cssURI.toExternalForm();
            parent.getStylesheets().add(uriToCss);
        }
        return parent;
    }

    private String getResourceName(String ending) {
        String name = getConventionalName(true) + ending;
        URL found = getClass().getResource(name);
        if (found != null) return name;
        name = getConventionalName(false) + ending;
        return name;
    }

    private String getConventionalName(boolean lowercase) {
        String clazz = stripEnding(getClass().getSimpleName());
        return lowercase ? clazz.toLowerCase() : clazz;
    }

    private String stripEnding(String clazz) {
        if (!clazz.endsWith("View")) return clazz;
        return clazz.substring(0, clazz.lastIndexOf("View"));
    }


    /** Creates a new thread, where this::getView supplies a Parent for the consumer parameter. */
    public void getRootAsync(Consumer<Parent> consumer) {
        CompletableFuture.supplyAsync(this::getRoot, EXECUTOR_SERVICE).thenAcceptAsync(consumer, Platform::runLater);
    }
}
