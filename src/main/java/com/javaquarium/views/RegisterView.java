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

import java.io.IOException;

public class RegisterView extends View {

    @Dependency private AlertService alertService;
    @Dependency private StageService stageService;
    @Dependency private AccountService accountService;

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

        Button registerBtn = new Button("Register");
        registerBtn.setOnAction(e ->
                tryRegister(usernameField.getText(), passwordField.getText()));

        usernameField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER)
                passwordField.requestFocus();
        });
        passwordField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER)
                tryRegister(usernameField.getText(), passwordField.getText());
        });

        VBox vBox = new VBox(backBtn, usernameLabel, usernameField, passwordLabel, passwordField, registerBtn);
        Utils.setAnchors(vBox, 50);
        return new AnchorPane(vBox);
    }

    private void tryRegister(String username, String pwd) {
        if (username.length() == 0) {
            alertService.errorAlert("Can't have empty username.");
            return;
        }
        try {
            if (accountService.register(username, pwd)) {
                alertService.inform("Created user " + username + " with password " + pwd + ".", "Account created");
                if (!accountService.login(username, pwd)) {
                    alertService.errorAlert("Created user but could not log in...?");
                }
            } else {
                alertService.errorAlert(username + " already exists!");
            }
        } catch (IOException e) {
            alertService.errorAlert(e.getMessage());
            System.out.println("Could not write to users files.");
        } catch (InvalidUsersFileException e) {
            alertService.errorAlert(e.getMessage());
        }
    }
}
