package com.javaquarium.views.simple;

import com.javaquarium.backend.services.AccountService;
import com.management.Dependency;
import com.javaquarium.Event;
import com.management.LawnMower;
import com.management.OnEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SimplePresenter {

    @Dependency private AccountService accountService;
    @Dependency private SimpleService simpleService;
    @FXML private Label label;

    @OnEvent(Event.LOGIN)
    private void changeLabel()  {
        label.setText(simpleService.getText() + " " + accountService.getUsername());
    }

    @FXML
    private void trigger1() {
        accountService.loginAsGuest();
    }

    @FXML
    private void trigger2() {
        accountService.logout();
    }
}
