package com.javaquarium.backend.services;

import com.firework.*;
import com.javaquarium.Events;
import com.javaquarium.backend.Utils;
import com.javaquarium.views.StartView;
import com.javaquarium.views.app.AppView;
import com.javaquarium.views.settings.SettingsView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Service
public class StageService {

    private Stage settingsStage;
    private final Stage stage = Theater.getPrimaryStage();
    @Dependency private AlertService alertService;
    @Dependency private AccountService accountService;
    @Dependency private ThemeService themeService;

    @OnEvent(Events.LOGIN)
    private void openApp() {
        setView(AppView.class);
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {
            if (alertService.confirm("Close?")) Firework.queueAutomaticEvent(Events.EXIT);
        });
    }

    /** Sets the view of a custom stage. */
    public void setView(Class<? extends View> clazz, Stage stage) {
        Scene scene = Theater.getScene(clazz);
        themeService.applyTheme(scene);
        stage.setScene(scene);
    }

    /** Sets the view of the primary stage. */
    public void setView(Class<? extends View> clazz) {
        setView(clazz, stage);
    }

    /** Creates a quick stage from the parent passed to it, with a custom title.
     * Surrounds the passed parent with 50 pixels of padding.
     */
    public Stage quickStage(Parent parent, String title) {
        Stage stage = new Stage();
        Utils.setAnchors(parent, 50);
        Scene scene = new Scene(new AnchorPane(parent));
        themeService.applyTheme(scene);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

    @OnEvent(Events.STARTVIEW)
    private void openStart() {
        setView(StartView.class);
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {});
        new Thread(() -> Firework.queueAutomaticEvent(Events.PRELOAD)).start();
    }

    @OnEvent(Events.NEWTHEME)
    private void reloadAppView() {
        Theater.startRebuildScenes();
        setView(AppView.class);
        setView(SettingsView.class, settingsStage);
    }

    @OnEvent(Events.EXIT)
    private void exit() {
        stage.close();
        if (settingsStage != null) settingsStage.close();
    }

    @OnEvent(Events.LOGOUT)
    private void startView() {
        Firework.queueAutomaticEvent(Events.STARTVIEW);
    }

    public void setSettingsStage(Stage settingsStage) { this.settingsStage = settingsStage; }
}
