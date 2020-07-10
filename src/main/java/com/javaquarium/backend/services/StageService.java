package com.javaquarium.backend.services;

import com.firework.*;
import com.javaquarium.Events;
import com.javaquarium.backend.Utils;
import com.javaquarium.views.StartView;
import com.javaquarium.views.app.AppView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Service
public class StageService {

    private final Stage stage = Theater.getPrimaryStage();
    @Dependency private AlertService alertService;
    @Dependency private AccountService accountService;
    @Dependency private ThemeService themeService;

    @OnEvent(Events.LOGIN)
    private void openApp() {
        setView(AppView.class);
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {
            if (!alertService.confirm("Close?")) e.consume();
        });
    }

    public void setView(Class<? extends View> clazz, Stage stage) {
        Scene scene = Theater.getScene(clazz);
        themeService.applyTheme(scene);
        stage.setScene(scene);
    }

    public void setView(Class<? extends View> clazz) {
        setView(clazz, stage);
    }

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
        new Thread(() -> EventSystem.queueAutomaticEvent(Events.PRELOAD)).start();
    }

    @OnEvent(Events.LOGOUT)
    private void startView() {
        EventSystem.queueAutomaticEvent(Events.STARTVIEW);
    }
}
