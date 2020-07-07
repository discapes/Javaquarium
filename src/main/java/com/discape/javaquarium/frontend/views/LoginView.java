package com.discape.javaquarium.frontend.views;

import com.discape.javaquarium.Utils;
import com.discape.javaquarium.frontend.views.app.AppView;
import com.discape.javaquarium.backend.AccountManager;
import com.discape.javaquarium.backend.Alerts;
import com.discape.javaquarium.backend.InvalidUsersFileException;
import com.discape.javaquarium.frontend.persistent.IMainView;
import com.discape.javaquarium.frontend.persistent.IViewSetter;
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
import java.io.FileNotFoundException;

public class LoginView implements IMainView {

    @Inject private Alerts alerts;
    @Inject private AccountManager accountManager;
    @Inject private IViewSetter viewSetter;

    private final Parent root;

    public LoginView() {
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
        root = new AnchorPane(vBox);
    }

    @Override public Parent getRoot() {
        return root;
    }

    @Override public void modifyStage(Stage stage) {
        stage.setTitle("Login");
    }

    private void tryLogin(String username, String pwd) {
        try {
            if (accountManager.login(username, pwd))
                viewSetter.applyView(AppView.class);
            else {
                alerts.errorAlert("Incorrect username or password!");
            }
        } catch (FileNotFoundException e) {
            alerts.errorAlert("Users file not found: " + e.getMessage());
        } catch (InvalidUsersFileException e) {
            alerts.errorAlert(e.getMessage());
        }
    }
}
