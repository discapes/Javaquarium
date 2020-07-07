package com.discape.javaquarium.frontend.persistent;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.Utils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.WeakHashMap;

public class LazyViewSetter implements IViewSetter {

    @Inject private ThemeManager themeManager;
    @Inject private Stage primaryStage;
    private Stage secondaryStage = null;

    WeakHashMap<Class<? extends IMainView>, Pair<IMainView, Scene>> pages = new WeakHashMap<>();

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

    @Override public void applyView(Class<? extends IMainView> clazz) {
        applyView(clazz, primaryStage);
    }

    private void applyView(Class<? extends IMainView> clazz, Stage stage) {
        Pair<IMainView, Scene> pair = pages.get(clazz);
        IMainView view;
        Scene scene;
        if (pair == null) {
            try {
                view = clazz.getDeclaredConstructor().newInstance();
                Injector.injectMembers(clazz, view);
                scene = new Scene(view.getRoot());
                themeManager.applyTheme(scene);
                pages.put(clazz, new Pair<>(view, scene));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return;
            }
        } else {
            view = pair.getKey();
            scene = pair.getValue();
        }
        stage.setScene(scene);
        view.modifyStage(stage);
    }

    @Override public void clearViews() {
        pages.clear();
    }

    @Override
    public Stage quickStage(Parent parent, String title) {
        Stage stage = new Stage();
        Utils.setAnchors(parent, 50);
        Scene scene = new Scene(new AnchorPane(parent));
        themeManager.applyTheme(scene);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }
}
