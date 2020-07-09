package com.javaquarium.backend.services;

import com.javaquarium.Event;
import com.javaquarium.views.app.AppView;
import com.management.Dependency;
import com.management.LawnMower;
import com.management.OnEvent;
import com.management.Service;
import javafx.stage.Stage;

import java.util.Stack;

@Service
public class StageService {

    @Dependency private AlertService alertService;
    @Dependency private AccountService accountService;

    private Stage stage;
    @OnEvent(Event.STAGEREADY)
    private void setStage(Stage stage) {
        this.stage = stage;
    }


    @OnEvent(Event.LOGIN)
    private void modStage() {
        stage.setScene(LawnMower.getScene(AppView.class));
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {
            if (!alertService.confirm("Close?")) e.consume();
            else accountService.logout();
        });
    }
}
