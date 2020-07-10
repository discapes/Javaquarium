package com.javaquarium.views;

import com.firework.Dependency;
import com.firework.View;
import com.javaquarium.backend.InvalidUsersFileException;
import com.javaquarium.backend.Utils;
import com.javaquarium.backend.services.AccountService;
import com.javaquarium.backend.services.AlertService;
import com.javaquarium.backend.services.StageService;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;

public class LoginView extends View {

    @Dependency private AlertService alertService;
    @Dependency private AccountService accountService;
    @Dependency private StageService stageService;

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Parent getRoot() {
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stageService.setView(StartView.class));
        VBox.setMargin(backBtn, new Insets(0, 0, 20, 0));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        VBox.setMargin(usernameField, new Insets(0, 0, 10, 0));

        Label passwordLabel = new Label("Password:");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        VBox.setMargin(passwordField, new Insets(0, 0, 10, 0));

        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> tryLogin(usernameField.getText(), passwordField.getText()));

        usernameField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER)
                passwordField.requestFocus();
        });
        passwordField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                tryLogin(usernameField.getText(), passwordField.getText());
            }
        });

        VBox vBox = new VBox(backBtn, usernameLabel, usernameField, passwordLabel, passwordField, loginBtn);
        Utils.setAnchors(vBox, 50);
        return new AnchorPane(vBox);
    }

    private void tryLogin(String username, String pwd) {
        try {
            if (!accountService.login(username, pwd))
                alertService.errorAlert("Incorrect username or password!");
        } catch (FileNotFoundException e) {
            alertService.errorAlert("Users file not found: " + e.getMessage());
        } catch (InvalidUsersFileException e) {
            alertService.errorAlert(e.getMessage());
        }
    }
}
