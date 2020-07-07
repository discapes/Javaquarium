package com.discape.javaquarium.frontend.views;

import com.discape.javaquarium.Utils;
import com.discape.javaquarium.backend.AccountManager;
import com.discape.javaquarium.backend.Alerts;
import com.discape.javaquarium.backend.InvalidUsersFileException;
import com.discape.javaquarium.frontend.persistent.IMainView;
import com.discape.javaquarium.frontend.persistent.IViewSetter;
import com.discape.javaquarium.frontend.views.app.AppView;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;

public class RegisterView implements IMainView {

    @Inject private Alerts alerts;
    @Inject private IViewSetter viewSetter;
    @Inject private AccountManager accountManager;

    private final Parent root;

    public RegisterView() {
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> viewSetter.applyView(StartView.class));
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
        root = new AnchorPane(vBox);
    }

    @Override public Parent getRoot() {
        return root;
    }

    @Override public void modifyStage(Stage stage) {
        stage.setTitle("Register");
    }

    private void tryRegister(String username, String pwd) {
        if (username.length() == 0) {
            alerts.errorAlert("Can't have empty username.");
            return;
        }
        try {
            if (accountManager.register(username, pwd)) {
                alerts.inform("Created user " + username + " with password " + pwd + ".", "Account created");
                if (accountManager.login(username, pwd)) {
                    viewSetter.applyView(AppView.class);
                } else {
                    alerts.errorAlert("Created user but could not log in...?");
                    return;
                }
            } else {
                alerts.errorAlert(username + " already exists!");
                return;
            }
        } catch (IOException e) {
            alerts.errorAlert(e.getMessage());
            System.out.println("Could not write to users files.");
            return;
        } catch (InvalidUsersFileException e) {
            alerts.errorAlert(e.getMessage());
        }
    }
}
