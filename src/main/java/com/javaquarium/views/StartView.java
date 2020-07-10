package com.javaquarium.views;

import com.firework.Dependency;
import com.firework.View;
import com.javaquarium.backend.Utils;
import com.javaquarium.backend.services.AccountService;
import com.javaquarium.backend.services.StageService;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class StartView extends View {

    @Dependency private StageService stageService;
    @Dependency private AccountService accountService;

    @Override
    public Parent getRoot() {
        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> stageService.setView(LoginView.class));

        Button registerBtn = new Button("Register");
        registerBtn.setOnAction(e -> stageService.setView(RegisterView.class));

        Button guestBtn = new Button("Guest");
        guestBtn.setOnAction(e -> accountService.loginAsGuest());

        VBox vBox = new VBox(loginBtn, registerBtn, guestBtn);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        Utils.setAnchors(vBox, 50);
        return new AnchorPane(vBox);
    }
}
