package com.discape.javaquarium.gui;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.business.Cryptographer;
import com.discape.javaquarium.gui.toolbar.Intro;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LoginView {

    private final Stage stage;
    @Inject private Cryptographer cryptographer;
    @Inject private String usersFile;
    @Inject private StringProperty accountLine;
    @Inject private Intro intro;

    public LoginView(Stage stage) {this.stage = stage;}

    // TODO register button
    public Parent getView() {
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> intro.show(stage));
        VBox.setMargin(backBtn, new Insets(0, 0, 20, 0));

        Label username = new Label("Username:");
        TextField nameField = new TextField();
        nameField.setPromptText("Username");
        VBox.setMargin(nameField, new Insets(0, 0, 10, 0));

        Label password = new Label("Password:");
        TextField pwdField = new TextField();
        pwdField.setPromptText("Password");
        VBox.setMargin(pwdField, new Insets(0, 0, 10, 0));

        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> login(nameField.getText(), pwdField.getText()));

        nameField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER)
                pwdField.requestFocus();
        });

        pwdField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER)
                login(nameField.getText(), pwdField.getText());
        });

        return new VBox(backBtn, username, nameField, password, pwdField, loginBtn);
    }

    public void login(String username, String password) {
        Scanner scanner;
        try {
            scanner = new Scanner(new File(usersFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("This file should have already been created?");
            return;
        }
        boolean rightPassword = false;
        while (scanner.hasNext() && !rightPassword) {
            String line = scanner.nextLine();
            String lineUsername = line.substring(0, line.indexOf(" "));
            String hash = line.substring(line.indexOf(" ") + 1);
            if (username.equals(lineUsername)) {
                if (cryptographer.testPassword(hash, password)) {
                    rightPassword = true;
                    accountLine.set(line);
                    AppStarter appStarter = new AppStarter();
                    Injector.injectMembers(appStarter.getClass(), appStarter);
                    appStarter.start(stage);
                }
            } else {
                System.out.println(username + " != " + lineUsername);
            }

        }
    }
}
