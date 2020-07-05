package com.discape.javaquarium.display._pages;

import com.discape.javaquarium.Utils;
import com.discape.javaquarium.display.StageUtilities;
import com.discape.javaquarium.logic.AccountManager;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.inject.Inject;

public class LoginRegisterPage extends Page {

    @Inject private Book book;
    @Inject private LoginPage loginPage;
    @Inject private RegisterPage registerPage;
    @Inject private MainPage mainPage;
    @Inject private AccountManager accountManager;

    @Inject private StageUtilities stageUtilities;

    @Override
    public Parent getView() {
        book.addPage(this);
        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> stageUtilities.setPage(loginPage));

        Button registerBtn = new Button("Register");
        registerBtn.setOnAction(e -> stageUtilities.setPage(registerPage));

        Button guestBtn = new Button("Guest");
        guestBtn.setOnAction(e -> {
            accountManager.loginAsGuest();
            stageUtilities.setPage(mainPage);
        });

        VBox vBox = new VBox(loginBtn, registerBtn, guestBtn);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        Utils.setAnchors(vBox, 50);

        return new AnchorPane(vBox);
    }

    @Override
    public void modifyStage(Stage stage) {
        stage.setTitle("Javaquarium");
        stage.centerOnScreen();
    }
}
