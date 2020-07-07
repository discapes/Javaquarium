package com.discape.javaquarium.frontend.persistent;

import com.discape.javaquarium.Utils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.WeakHashMap;

public class LazyViewSetter implements IViewSetter {

    @Inject private ThemeManager themeManager;
    @Inject private Stage primaryStage;
    private Stage secondaryStage = null;

    WeakHashMap<Class<? extends IMainView>, IMainView> views;

    @Override public void applySecondaryView(Class<? extends IMainView> clazz) {
        boolean createStage = secondaryStage == null;
        if (createStage) secondaryStage = new Stage();
        applyView(clazz, secondaryStage);
        secondaryStage.showAndWait();
    }

    @Override public void closeSecondary() {
        secondaryStage.close();
        secondaryStage = null;
    }


    @Override
    public void applyView(Class<? extends IMainView> clazz) {
        applyView(clazz, primaryStage);
    }

    private void applyView(Class<? extends IMainView> clazz, Stage stage) {
        IMainView view = views.get(clazz);
        if (view == null) {
            try {
                view = clazz.getDeclaredConstructor().newInstance();
                views.put(clazz, view);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                return;
            }
        }
        setView(view, stage);
    }

    private void setView(IMainView view, Stage stage) {
        Parent root = view.getRoot();
        Scene scene = new Scene(root);
        themeManager.applyTheme(scene);
        stage.setScene(scene);
        view.modifyStage(stage);
    }

    @Override public void clearViews() {
        views.clear();
    }

    @Override
    public Stage quickStage(Parent parent, String title) {
        Stage stage = new Stage();
        setView(new IMainView() {
            @Override public void modifyStage(Stage stage) {
                stage.setTitle(title);
            }
            @Override public Parent getRoot() {
                Utils.setAnchors(parent, 50);
                return new AnchorPane(parent);
            }
        }, stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }
}
