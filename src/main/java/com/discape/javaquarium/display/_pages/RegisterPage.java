package com.discape.javaquarium.display._pages;

import com.discape.javaquarium.Utils;
import com.discape.javaquarium.display.Alerts;
import com.discape.javaquarium.display.StageUtilities;
import com.discape.javaquarium.logic.AccountManager;
import com.discape.javaquarium.logic.InvalidUsersFileException;
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

public class RegisterPage extends Page {

    @Inject private AccountManager accountManager;
    @Inject private Book book;
    @Inject private StageUtilities stageUtilities;
    @Inject private Alerts alerts;
    @Inject private MainPage mainPage;

    @Override
    public Parent getView() {
        LoginRegisterPage loginRegisterPage = book.getPage(LoginRegisterPage.class);
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stageUtilities.setPage(loginRegisterPage));
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

    @Override
    public void modifyStage(Stage stage) {
        stage.setTitle("Register");
    }

    private void tryRegister(String username, String pwd) {
        try {
            if (accountManager.register(username, pwd)) {
                alerts.inform("Created user " + username + " with password " + pwd + ".", "Account created");
                if (accountManager.login(username, pwd)) {
                    stageUtilities.setPage(mainPage);
                } else {
                    alerts.errorAlert("Created user but could not log in...");
                }
            } else {
                alerts.errorAlert(username + " already exists!");
            }
        } catch (IOException e) {
            alerts.errorAlert(e.getMessage());
            System.out.println("Could not write to users files.");
        } catch (InvalidUsersFileException e) {
            alerts.errorAlert(e.getMessage());
        }
    }
}
