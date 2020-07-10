package com.javaquarium.backend.services;

import com.firework.Dependency;
import com.firework.OnEvent;
import com.firework.Service;
import com.firework.View;
import com.firework.javafx.Theater;
import com.javaquarium.Event;
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

    @Dependency private AlertService alertService;
    @Dependency private AccountService accountService;
    @Dependency private ThemeService themeService;

    private final Stage stage = Theater.getPrimaryStage();

    @OnEvent(Event.LOGIN)
    private void openApp() {
        stage.setScene(Theater.getScene(AppView.class));
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {
            if (!alertService.confirm("Close?")) e.consume();
            else accountService.logout();
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

    @OnEvent(Event.LOGOUT)
    private void openStart() {
        stage.setScene(Theater.getScene(StartView.class));
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {});
    }
}
